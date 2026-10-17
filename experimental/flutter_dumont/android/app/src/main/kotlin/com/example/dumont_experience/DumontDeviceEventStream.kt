package com.example.dumont_experience

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import io.flutter.plugin.common.EventChannel

/**
 * Event-driven device status, active only while Flutter subscribes.
 *
 * No timers, scans, SSIDs, unique identifiers, privileged permissions or
 * background service. Release the Android registrations on cancellation.
 */
class DumontDeviceEventStream(
    private val context: Context,
    private val snapshotProvider: () -> Map<String, Any?>
) : EventChannel.StreamHandler {
    private val handler = Handler(Looper.getMainLooper())
    private val connectivity =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var sink: EventChannel.EventSink? = null
    private var lastSnapshot: Map<String, Any?>? = null
    private var batteryRegistered = false
    private var networkRegistered = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) emitIfChanged()
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = emitOnMain()
        override fun onLost(network: Network) = emitOnMain()
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) = emitOnMain()
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
        onCancel(null)
        sink = events
        lastSnapshot = null

        try {
            // ACTION_BATTERY_CHANGED is a system broadcast.
            context.registerReceiver(
                batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            batteryRegistered = true
        } catch (_: Exception) {
            batteryRegistered = false
        }
        try {
            // Permission already declared: ACCESS_NETWORK_STATE (normal).
            connectivity.registerDefaultNetworkCallback(networkCallback)
            networkRegistered = true
        } catch (_: Exception) {
            networkRegistered = false
        }
        emitIfChanged()
    }

    override fun onCancel(arguments: Any?) {
        sink = null
        lastSnapshot = null
        if (batteryRegistered) {
            try { context.unregisterReceiver(batteryReceiver) } catch (_: Exception) {}
            batteryRegistered = false
        }
        if (networkRegistered) {
            try { connectivity.unregisterNetworkCallback(networkCallback) } catch (_: Exception) {}
            networkRegistered = false
        }
    }

    fun emitIfChanged() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            emitOnMain()
            return
        }
        val listener = sink ?: return
        val snapshot = try {
            snapshotProvider()
        } catch (_: Exception) {
            // Unknown values are safer than fabricated readings.
            mapOf<String, Any?>(
                "batteryPercent" to null, "isCharging" to null,
                "networkType" to "unknown", "model" to "Desconhecido",
                "androidVersion" to "Desconhecido"
            )
        }
        if (snapshot != lastSnapshot) {
            lastSnapshot = snapshot
            listener.success(snapshot)
        }
    }

    private fun emitOnMain() {
        handler.post { emitIfChanged() }
    }

    fun dispose() = onCancel(null)
}
