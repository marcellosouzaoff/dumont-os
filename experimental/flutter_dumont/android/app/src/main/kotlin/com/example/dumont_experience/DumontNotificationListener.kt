package com.example.dumont_experience

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/** Private, volatile state only. No persistence, logs, or notification text. */
object DumontNotificationMemory {
    data class Summary(
        val key: String,
        val packageName: String,
        val appLabel: String,
        val postTimeMs: Long
    )

    private val recent = LinkedHashMap<String, Summary>()
    @Volatile private var connected = false

    @Synchronized
    fun onConnected(notifications: Array<StatusBarNotification>?, labelFor: (String) -> String) {
        connected = true
        recent.clear()
        notifications?.forEach { add(it, labelFor) }
    }

    @Synchronized
    fun add(notification: StatusBarNotification, labelFor: (String) -> String) {
        if (!connected) return
        if (notification.notification.flags and Notification.FLAG_ONGOING_EVENT != 0) return
        if (notification.isOngoing) return
        val key = notification.key ?: return
        val pkg = notification.packageName ?: return
        val item = Summary(
            key = key,
            packageName = pkg.take(120),
            appLabel = labelFor(pkg).take(70),
            postTimeMs = notification.postTime
        )
        recent.remove(key)
        recent[key] = item
        while (recent.size > 12) recent.remove(recent.keys.first())
    }

    @Synchronized
    fun remove(key: String?) {
        if (key != null) recent.remove(key)
    }

    @Synchronized
    fun onDisconnected() {
        connected = false
        recent.clear()
    }

    @Synchronized
    fun snapshot(): Pair<Boolean, List<Map<String, Any>>> {
        val summaries = if (connected) recent.values.toList().asReversed() else emptyList()
        return connected to summaries.map {
            mapOf(
                "packageName" to it.packageName,
                "appLabel" to it.appLabel,
                "postTimeMs" to it.postTimeMs
            )
        }
    }
}

class DumontNotificationListener : NotificationListenerService() {
    override fun onListenerConnected() {
        super.onListenerConnected()
        val active = try { activeNotifications } catch (_: Exception) { null }
        DumontNotificationMemory.onConnected(active, ::safeAppLabel)
    }

    override fun onListenerDisconnected() {
        DumontNotificationMemory.onDisconnected()
        super.onListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        DumontNotificationMemory.add(sbn, ::safeAppLabel)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        DumontNotificationMemory.remove(sbn.key)
    }

    override fun onDestroy() {
        DumontNotificationMemory.onDisconnected()
        super.onDestroy()
    }

    private fun safeAppLabel(pkg: String): String = try {
        val info = packageManager.getApplicationInfo(pkg, 0)
        packageManager.getApplicationLabel(info).toString()
    } catch (_: Exception) {
        pkg
    }
}
