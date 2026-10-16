package br.com.dumont.launcher

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.lifecycleScope
import br.com.dumont.launcher.data.AppCatalog
import br.com.dumont.launcher.data.AppRules
import br.com.dumont.launcher.data.DockStore
import br.com.dumont.launcher.data.DeviceStatusReader
import br.com.dumont.launcher.data.UsageStore
import br.com.dumont.launcher.integration.GalaxyBridge
import br.com.dumont.launcher.model.AppEntry
import br.com.dumont.launcher.model.SystemStatus
import br.com.dumont.launcher.orchestrator.AppLaunchPolicy
import br.com.dumont.launcher.ui.DumontHome
import br.com.dumont.launcher.ui.DumontTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Ordinary launcher candidate. Does not replace One UI without user action. */
class MainActivity : ComponentActivity() {
    private var apps by mutableStateOf(emptyList<AppEntry>())
    private var pinnedKeys by mutableStateOf(emptyList<String>())
    private var usageScores by mutableStateOf(emptyMap<String, Long>())
    private var usageAccessGranted by mutableStateOf(false)
    private var barOpacity by mutableFloatStateOf(1f)
    private var catalogLoading by mutableStateOf(true)
    private var catalogError by mutableStateOf(false)
    private var deviceStatus by mutableStateOf(SystemStatus())
    private var wallpaperBitmap by mutableStateOf<ImageBitmap?>(null)
    private var notificationsEnabled by mutableStateOf(false)
    private var catalogLoaded = false
    private var catalogJob: Job? = null
    private val dockStore by lazy { DockStore(this) }
    private val usageStore by lazy { UsageStore(this) }
    private val deviceStatusReader by lazy { DeviceStatusReader(this) }
    private val wallpaperPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val granted = runCatching {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }.isSuccess
            if (granted) {
                appearancePrefs.edit().putString("wallpaper_uri", uri.toString()).apply()
                loadWallpaper(uri)
            } else {
                Toast.makeText(this, "Galeria: acesso à imagem não autorizado", Toast.LENGTH_LONG).show()
            }
        }
    }
    private val appearancePrefs by lazy {
        getSharedPreferences("dumont_appearance", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        barOpacity = appearancePrefs.getFloat("bar_opacity", 1f).coerceIn(0.20f, 1f)
        appearancePrefs.getString("wallpaper_uri", null)?.let { loadWallpaper(Uri.parse(it)) }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (true) {
                    val result = withContext(Dispatchers.IO) { runCatching { deviceStatusReader.read() } }
                    result.onSuccess { deviceStatus = it }
                    notificationsEnabled = NotificationManagerCompat
                        .getEnabledListenerPackages(this@MainActivity).contains(packageName)
                    delay(30_000)
                }
            }
        }
        setContent {
            DumontTheme {
                DumontHome(
                    apps = apps,
                    pinnedKeys = pinnedKeys,
                    usageScores = usageScores,
                    barOpacity = barOpacity,
                    usageAccessGranted = usageAccessGranted,
                    deviceStatus = deviceStatus,
                    wallpaperBitmap = wallpaperBitmap,
                    notificationsEnabled = notificationsEnabled,
                    onChooseWallpaper = { wallpaperPicker.launch(arrayOf("image/*")) },
                    onResetWallpaper = ::resetWallpaper,
                    onNotificationPermission = ::openNotificationPermission,
                    onNotificationClick = ::openNotification,
                    onLaunch = ::launch,
                    onTogglePinned = ::togglePinned,
                    onOpenSettings = ::openSystemSettings,
                    onGalaxyAction = ::openGalaxyAction,
                    onOpenTerminal = ::openTerminal,
                    onChooseHome = ::chooseHome,
                    onOpenUsageAccess = ::openUsageAccess,
                    onOpacityChange = { barOpacity = it.coerceIn(0.20f, 1f) },
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
            startActivity(AppLaunchPolicy.intentFor(app.component))
            usageStore.recordLaunch(app.key)
            updateUsageRanking()
        } catch (_: RuntimeException) {
            Toast.makeText(this, "Não foi possível abrir " + app.label, Toast.LENGTH_SHORT).show()
        }
    }

    private fun togglePinned(app: AppEntry) {
        if (app.packageName == "com.termux") {
            Toast.makeText(this, "Terminal fica somente no menu superior", Toast.LENGTH_SHORT).show()
            return
        }
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

    /** A Samsung/Android action is a real public Settings activity, never a hidden API. */
    private fun openGalaxyAction(id: String) {
        val intent = GalaxyBridge.intentFor(id) ?: return
        try {
            startActivity(intent)
        } catch (_: RuntimeException) {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
                Toast.makeText(this, "Esta tela não está disponível: mostrando Configurações",
                    Toast.LENGTH_SHORT).show()
            } catch (_: RuntimeException) {
                Toast.makeText(this, "Função indisponível neste dispositivo",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openSystemSettings() {
        try { startActivity(Intent(Settings.ACTION_SETTINGS)) }
        catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Configurações Android indisponíveis", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadWallpaper(uri: Uri) {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                        val original = info.size
                        val maxSide = maxOf(original.width, original.height)
                        val factor = minOf(1f, 2048f / maxSide.coerceAtLeast(1))
                        decoder.setTargetSize(
                            (original.width * factor).toInt().coerceAtLeast(1),
                            (original.height * factor).toInt().coerceAtLeast(1)
                        )
                    }.asImageBitmap()
                }
            }
            result.onSuccess { wallpaperBitmap = it }
                .onFailure {
                    wallpaperBitmap = null
                    Toast.makeText(this@MainActivity, "Não foi possível carregar a imagem", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun resetWallpaper() {
        wallpaperBitmap = null
        appearancePrefs.edit().remove("wallpaper_uri").apply()
    }

    private fun openNotificationPermission() {
        try { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
        catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Abra Configurações > Notificações > Acesso às notificações",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun openNotification(key: String) {
        val service = br.com.dumont.launcher.notifications.DumontNotificationListener.current
        val notification = try { service?.activeNotifications?.firstOrNull { it.key == key } }
        catch (_: SecurityException) { null }
        try { notification?.notification?.contentIntent?.send() }
        catch (_: android.app.PendingIntent.CanceledException) {
            Toast.makeText(this, "Notificação indisponível", Toast.LENGTH_SHORT).show()
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
