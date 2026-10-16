package br.com.dumont.launcher.data

import android.content.Context
import br.com.dumont.launcher.model.AppEntry

/** Private local preferences: no backup, account, server, or telemetry. */
class DockStore(context: Context) {
    private val prefs = context.getSharedPreferences("dumont_dock", Context.MODE_PRIVATE)

    fun load(apps: List<AppEntry>): List<String> {
        val raw = prefs.getString("favorites", null)
        return if (raw == null) {
            AppRules.initialDockKeys(apps.map { it.packageName to it.key })
        } else {
            raw.split("\n").filter { it.isNotBlank() }.distinct().take(9)
        }
    }

    fun save(keys: List<String>) {
        prefs.edit().putString("favorites", keys.distinct().take(9).joinToString("\n")).apply()
    }
}
