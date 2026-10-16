package br.com.dumont.launcher.data

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import br.com.dumont.launcher.model.AppEntry

/**
 * No analytics or network. Counts launches from DUMONT, optionally supplements those
 * counts with Android's last-7-day foreground usage after an explicit Usage Access grant.
 */
class UsageStore(private val context: Context) {
    private val prefs = context.getSharedPreferences("dumont_local_launches", Context.MODE_PRIVATE)

    fun recordLaunch(key: String) {
        val previous = prefs.getLong(key, 0L)
        prefs.edit().putLong(key, (previous + 1L).coerceAtMost(Long.MAX_VALUE)).apply()
    }

    fun localLaunches(): Map<String, Long> =
        prefs.all.mapNotNull { (key, value) ->
            (value as? Long)?.takeIf { it > 0L }?.let { key to it }
        }.toMap()

    fun hasSystemUsageAccess(): Boolean {
        val appOps = context.getSystemService(AppOpsManager::class.java) ?: return false
        return appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName
        ) == AppOpsManager.MODE_ALLOWED
    }

    fun systemUsageLast7Days(): Map<String, Long> {
        if (!hasSystemUsageAccess()) return emptyMap()
        val usageStatsManager = context.getSystemService(UsageStatsManager::class.java) ?: return emptyMap()
        val now = System.currentTimeMillis()
        val weekAgo = now - 7L * 24L * 60L * 60L * 1000L
        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, weekAgo, now
        ).orEmpty()
            .groupBy { it.packageName }
            .mapValues { (_, entries) -> entries.sumOf { it.totalTimeInForeground } }
            .filterValues { it > 0L }
    }

    fun scores(apps: List<AppEntry>, systemUsage: Map<String, Long>): Map<String, Long> {
        val local = localLaunches()
        // Android usage is a single source for ranking; don't mix milliseconds with launches.
        return if (systemUsage.isNotEmpty()) {
            apps.associate { it.key to (systemUsage[it.packageName] ?: 0L) }
        } else {
            apps.associate { it.key to (local[it.key] ?: 0L) }
        }
    }
}
