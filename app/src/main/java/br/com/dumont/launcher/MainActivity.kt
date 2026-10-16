package br.com.dumont.launcher

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.com.dumont.launcher.data.AppCatalog
import br.com.dumont.launcher.data.AppRules
import br.com.dumont.launcher.data.DockStore
import br.com.dumont.launcher.model.AppEntry
import br.com.dumont.launcher.ui.DumontHome
import br.com.dumont.launcher.ui.DumontTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Normal Android HOME candidate. Never edits system settings or Knox. */
class MainActivity : ComponentActivity() {
    private var apps by mutableStateOf(emptyList<AppEntry>())
    private var favorites by mutableStateOf(emptyList<String>())
    private val dockStore by lazy { DockStore(this) }
    private var catalogLoading by mutableStateOf(true)
    private var catalogError by mutableStateOf(false)
    private var catalogLoaded = false
    private var catalogJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DumontTheme {
                DumontHome(
                    apps = apps,
                    favoriteKeys = favorites,
                    onLaunch = ::launch,
                    onToggleFavorite = ::toggleFavorite,
                    onChooseHome = ::chooseHome,
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
    }

    /** PackageManager and icon decoding must never block the UI thread. */
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
                favorites = dockStore.load(loaded)
                catalogLoaded = true
            }.onFailure {
                catalogError = true
            }
            catalogLoading = false
        }
    }

    private fun launch(app: AppEntry) {
        try {
            val intent = Intent.makeMainActivity(app.component)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (error: RuntimeException) {
            Toast.makeText(this, "Não foi possível abrir " + app.label, Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFavorite(app: AppEntry) {
        val revised = AppRules.toggleFavorite(favorites, app.key)
        if (revised == favorites) {
            Toast.makeText(this, "O Dock comporta até 9 favoritos", Toast.LENGTH_SHORT).show()
            return
        }
        favorites = revised
        dockStore.save(revised)
        Toast.makeText(this,
            if (app.key in revised) "Adicionado ao Dock" else "Removido do Dock",
            Toast.LENGTH_SHORT).show()
    }

    private fun chooseHome() {
        try {
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Abra Configurações > Aplicativos padrão > Tela inicial", Toast.LENGTH_LONG).show()
        }
    }
}
