package br.com.dumont.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import br.com.dumont.launcher.model.AppEntry
import java.text.Collator
import java.util.Locale

/**
 * Only MAIN/LAUNCHER activities are queried; this app does not request
 * QUERY_ALL_PACKAGES, accessibility access or private storage permissions.
 */
class AppCatalog(private val context: Context) {
    fun load(): List<AppEntry> {
        val pm = context.packageManager
        val query = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val matches = if (Build.VERSION.SDK_INT >= 33) {
            pm.queryIntentActivities(query, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(query, 0)
        }
        val sorter = Collator.getInstance(Locale("pt", "BR"))
        return matches.mapNotNull { resolved ->
            val info = resolved.activityInfo ?: return@mapNotNull null
            if (info.packageName == context.packageName) return@mapNotNull null
            AppEntry(
                label = resolved.loadLabel(pm)?.toString() ?: info.packageName,
                packageName = info.packageName,
                component = android.content.ComponentName(info.packageName, info.name),
                icon = resolved.loadIcon(pm)
            )
        }.distinctBy { it.key }.sortedWith { a, b -> sorter.compare(a.label, b.label) }
    }
}
