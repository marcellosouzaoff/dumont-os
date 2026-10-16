package br.com.dumont.launcher.notifications

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow

/** Transient notifications only. No disk, network, server or notification logging. */
data class NotificationEntry(
    val key: String,
    val packageName: String,
    val title: String,
    val detail: String
)

object NotificationFeed {
    val entries = MutableStateFlow<List<NotificationEntry>>(emptyList())
}

class DumontNotificationListener : NotificationListenerService() {
    companion object {
        @Volatile var current: DumontNotificationListener? = null
            private set
    }
    override fun onListenerConnected() {
        current = this
        publish()
    }
    override fun onListenerDisconnected() {
        current = null
        NotificationFeed.entries.value = emptyList()
    }
    override fun onDestroy() {
        current = null
        NotificationFeed.entries.value = emptyList()
        super.onDestroy()
    }
    override fun onNotificationPosted(sbn: StatusBarNotification?) = publish()
    override fun onNotificationRemoved(sbn: StatusBarNotification?) = publish()

    private fun publish() {
        NotificationFeed.entries.value = try {
            activeNotifications.orEmpty()
                .asSequence()
                .filter { it.isClearable && it.notification.visibility != Notification.VISIBILITY_SECRET }
                .sortedByDescending { it.postTime }
                .take(30)
                .map { sbn ->
                    val extras = sbn.notification.extras
                    NotificationEntry(
                        key = sbn.key,
                        packageName = sbn.packageName,
                        title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
                            ?.take(120).orEmpty().ifBlank { sbn.packageName },
                        detail = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
                            ?.take(240).orEmpty()
                    )
                }.toList()
        } catch (_: SecurityException) {
            emptyList()
        }
    }
}
