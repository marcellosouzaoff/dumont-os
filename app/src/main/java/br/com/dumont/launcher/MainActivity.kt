package br.com.dumont.launcher

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import br.com.dumont.launcher.data.AppCatalog
import br.com.dumont.launcher.data.AppRules
import br.com.dumont.launcher.data.DockStore
import br.com.dumont.launcher.data.UsageStore
import br.com.dumont.launcher.model.AppEntry
import br.com.dumont.launcher.ui.DumontHome
import br.com.dumont.launcher.ui.DumontTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Ordinary launcher candidate. Does not replace One UI without user action. */
class MainActivity : ComponentActivity() {
    private var apps by mutableStateOf(emptyList<AppEntry>())
    private var pinnedKeys by mutableStateOf(emptyList<String>())
    private var usageScores by mutableStateOf(emptyMap<String, Long>())
    private var usageAccessGranted by mutableStateOf(false)
    private var barOpacity by mutableFloatStateOf(0.70f)
    private var catalogLoading by mutableStateOf(true)
    private var catalogError by mutableStateOf(false)
    private var catalogLoaded = false
    private var catalogJob: Job? = null
    private val dockStore by lazy { DockStore(this) }
    private val usageStore by lazy { UsageStore(this) }
    private val appearancePrefs by lazy {
        getSharedPreferences("dumont_appearance", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        barOpacity = appearancePrefs.getFloat("bar_opacity", 0.70f).coerceIn(0.20f, 0.95f)
        setContent {
            DumontTheme {
                DumontHome(
                    apps = apps,
                    pinnedKeys = pinnedKeys,
                    usageScores = usageScores,
                    barOpacity = barOpacity,
                    usageAccessGranted = usageAccessGranted,
                    onLaunch = ::launch,
                    onTogglePinned = ::togglePinned,
                    onOpenSettings = ::openSystemSettings,
                    onOpenTerminal = ::openTerminal,
                    onChooseHome = ::chooseHome,
                    onOpenUsageAccess = ::openUsageAccess,
                    onOpacityChange = { barOpacity = it.coerceIn(0.20f, 0.95f) },
                    onOpacitySave = { appearancePrefs.edit().putFloat("bar_opacity", barOpacity).apply() },
                    isCatalogLoading = catalogLoading,
                    catalogError = catalogError,
                    onRefreshCatalog = ::refreshCatalog
                )
            }
        }
        refreshCatalog()
    }

    override fun onResume() {
        super.onResume()
        if (!catalogLoaded && catalogJob?.isActive != true) refreshCatalog()
        else updateUsageRanking()
    }

    /** All launchable activities and their native icons are loaded off the main thread. */
    private fun refreshCatalog() {
        if (catalogJob?.isActive == true) return
        catalogLoading = true
        catalogError = false
        catalogJob = lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { AppCatalog(this@MainActivity).load() }
            }
            result.onSuccess { loaded ->
                apps = loaded
                pinnedKeys = dockStore.load(loaded)
                catalogLoaded = true
                updateUsageRanking()
            }.onFailure { catalogError = true }
            catalogLoading = false
        }
    }

    private fun updateUsageRanking() {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val allowed = usageStore.hasSystemUsageAccess()
                    Triple(allowed, usageStore.systemUsageLast7Days(), usageStore.localLaunches())
                }
            }
            result.onSuccess { (granted, systemScores, localScores) ->
                usageAccessGranted = granted
                // Never mix foreground milliseconds with per-launch counters.
                usageScores = if (systemScores.isNotEmpty()) {
                    apps.associate { it.key to (systemScores[it.packageName] ?: 0L) }
                } else {
                    apps.associate { it.key to (localScores[it.key] ?: 0L) }
                }
            }
        }
    }

    private fun launch(app: AppEntry) {
        try {
            startActivity(Intent.makeMainActivity(app.component).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            usageStore.recordLaunch(app.key)
            updateUsageRanking()
        } catch (_: RuntimeException) {
            Toast.makeText(this, "Não foi possível abrir " + app.label, Toast.LENGTH_SHORT).show()
        }
    }

    private fun togglePinned(app: AppEntry) {
        val revised = AppRules.toggleFavorite(pinnedKeys, app.key, limit = 9)
        if (revised == pinnedKeys) {
            Toast.makeText(this, "O Dock comporta até 9 apps", Toast.LENGTH_SHORT).show()
            return
        }
        pinnedKeys = revised
        dockStore.save(revised)
        Toast.makeText(this,
            if (app.key in revised) "Fixado no Dock" else "Removido dos fixados",
            Toast.LENGTH_SHORT).show()
    }

    private fun openTerminal() {
        val entry = apps.firstOrNull { it.packageName == "com.termux" }
        if (entry != null) {
            launch(entry)
        } else {
            val intent = packageManager.getLaunchIntentForPackage("com.termux")
            if (intent != null) {
                try { startActivity(intent) }
                catch (_: RuntimeException) { showTerminalUnavailable() }
            } else showTerminalUnavailable()
        }
    }

    private fun showTerminalUnavailable() =
        Toast.makeText(this, "Termux não está disponível neste perfil", Toast.LENGTH_SHORT).show()

    private fun openSystemSettings() {
        try { startActivity(Intent(Settings.ACTION_SETTINGS)) }
        catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Configurações Android indisponíveis", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUsageAccess() {
        try { startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
        catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Acesso ao uso indisponível neste aparelho", Toast.LENGTH_SHORT).show()
        }
    }

    private fun chooseHome() {
        try { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) }
        catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Configurações > Aplicativos padrão > Tela inicial", Toast.LENGTH_LONG).show()
        }
    }
}
