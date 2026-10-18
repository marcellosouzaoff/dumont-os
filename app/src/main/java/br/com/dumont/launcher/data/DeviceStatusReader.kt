package br.com.dumont.launcher.data

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import br.com.dumont.launcher.model.SystemStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Read-only Android system status; call only while the activity is resumed. */
class DeviceStatusReader(private val context: Context) {
    fun read(): SystemStatus {
        val date = SimpleDateFormat("EEE dd/MM  HH:mm", Locale("pt", "BR"))
            .format(Date())
            .replaceFirstChar { it.uppercase() }

        val sticky = context.registerReceiver(
            null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val battery = if (sticky != null) {
            val level = sticky.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = sticky.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level >= 0 && scale > 0) {
                val percent = level * 100 / scale
                val plugged = sticky.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0
                "$percent%" + if (plugged) " · carga" else ""
            } else "—"
        } else "—"

        val network = try {
            val cm = context.getSystemService(ConnectivityManager::class.java)
            val active = cm?.activeNetwork?.let { cm.getNetworkCapabilities(it) }
            when {
                active == null -> "Sem rede"
                active.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
                active.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
                active.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Dados"
                active.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                else -> "Conectado"
            }
        } catch (_: SecurityException) { "Rede indisponível" }

        return SystemStatus(date, battery, network)
    }
}
