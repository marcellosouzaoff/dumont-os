package com.example.dumont_experience

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.content.pm.ResolveInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.provider.Settings
import java.io.ByteArrayOutputStream
import android.content.pm.PackageManager
import android.os.Build
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.EventChannel
import java.text.Collator
import java.util.Locale

/**
 * DUMONT Flutter Preview's public Android bridge.
 * Only launcher-visible activities are returned and launchable.
 * No privileged system API, QUERY_ALL_PACKAGES, Knox hook, or HOME role.
 */
class MainActivity : FlutterActivity() {
    private val channel = "br.com.dumont.experience/native"
    private val statusEventsChannel = "br.com.dumont.experience/device_events"
    private var deviceEvents: DumontDeviceEventStream? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        deviceEvents = DumontDeviceEventStream(this, ::deviceSnapshot).also { bridge ->
            EventChannel(flutterEngine.dartExecutor.binaryMessenger, statusEventsChannel)
                .setStreamHandler(bridge)
        }
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "listApps" -> {
                        try {
                            result.success(launchableActivities().map {
                                mapOf(
                                    "label" to it.label,
                                    "packageName" to it.component.packageName,
                                    "component" to it.component.flattenToString(),
                                    "iconPng" to safeIconPng(it.icon)
                                )
                            })
                        } catch (_: Exception) {
                            result.error("catalog_unavailable", "Android launcher catalog unavailable", null)
                        }
                    }
                    "launchApp" -> {
                        val component = call.argument<String>("component")
                        if (component.isNullOrBlank()) {
                            result.error("invalid_component", "Missing launcher component", null)
                        } else {
                            // Re-query prevents arbitrary component launches through the channel.
                            val allowed = try {
                                launchableActivities().firstOrNull {
                                    it.component.flattenToString() == component
                                }
                            } catch (_: Exception) {
                                null
                            }
                            if (allowed == null) {
                                result.error("not_launchable", "Component not in visible launcher catalog", null)
                            } else {
                                try {
                                    val intent = Intent(Intent.ACTION_MAIN)
                                        .addCategory(Intent.CATEGORY_LAUNCHER)
                                        .setComponent(allowed.component)
                                    startActivity(intent)
                                    result.success(true)
                                } catch (_: Exception) {
                                    result.error("launch_failed", "Android could not launch application", null)
                                }
                            }
                        }
                    }
                    "getDeviceStatus" -> {
                        try {
                            result.success(deviceSnapshot())
                        } catch (_: Exception) {
                            result.error("device_status_unavailable", "Device snapshot unavailable", null)
                        }
                    }
                    "openSettings" -> {
                        val id = call.argument<String>("id")
                        val intent = publicSettingsIntent(id)
                        if (intent == null) {
                            result.error("invalid_settings", "Unsupported public settings action", null)
                        } else {
                            try {
                                startActivity(intent)
                                result.success(true)
                            } catch (_: Exception) {
                                result.error("settings_unavailable", "Android settings action unavailable", null)
                            }
                        }
                    }
                    "getNotificationSnapshot" -> {
                        // Never send records if Android access was revoked.
                        val access = hasNotificationListenerAccess()
                        val (connected, entries) = if (access) {
                            DumontNotificationMemory.snapshot()
                        } else {
                            false to emptyList<Map<String, Any>>()
                        }
                        result.success(mapOf(
                            "accessGranted" to access,
                            "connected" to connected,
                            "notifications" to entries
                        ))
                    }
                    "openNotificationAccessSettings" -> {
                        try {
                            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                            result.success(true)
                        } catch (_: Exception) {
                            result.error("notification_settings_unavailable",
                                "Android notification listener settings unavailable", null)
                        }
                    }
                    else -> result.notImplemented()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        deviceEvents?.emitIfChanged()
    }

    override fun onDestroy() {
        deviceEvents?.dispose()
        deviceEvents = null
        super.onDestroy()
    }

    private fun hasNotificationListenerAccess(): Boolean {
        val service = ComponentName(this, DumontNotificationListener::class.java)
        return try {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.isNotificationListenerAccessGranted(service)
        } catch (_: Exception) {
            false
        }
    }

    private data class LaunchableActivity(
        val label: String,
        val component: ComponentName,
        val icon: Drawable?
    )

    private fun launchableActivities(): List<LaunchableActivity> {
        val query = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val pm = packageManager
        val activities = if (Build.VERSION.SDK_INT >= 33) {
            pm.queryIntentActivities(query, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(query, 0)
        }
        val collation = Collator.getInstance(Locale("pt", "BR"))
        return activities.mapNotNull { resolve ->
            val info = resolve.activityInfo ?: return@mapNotNull null
            if (!info.exported || info.packageName == packageName) return@mapNotNull null
            LaunchableActivity(
                resolve.loadLabel(pm)?.toString() ?: info.packageName,
                ComponentName(info.packageName, info.name),
                try { resolve.loadIcon(pm) } catch (_: Exception) { null }
            )
        }.distinctBy { it.component.flattenToString() }
            .sortedWith { a, b -> collation.compare(a.label, b.label) }
    }
    /** Limit each PNG to 56dp canvas pixels, avoid sending arbitrary bitmap sizes. */
    private fun safeIconPng(icon: Drawable?): ByteArray? {
        if (icon == null) return null
        return try {
            val size = 56
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            icon.setBounds(0, 0, size, size)
            icon.draw(canvas)
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, output)
            bitmap.recycle()
            output.toByteArray().takeIf { it.size <= 32768 }
        } catch (_: Exception) {
            null
        }
    }

    /** Public read-only snapshot; no SSID, phone identifiers or location. */
    private fun deviceSnapshot(): Map<String, Any?> {
        val battery = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = battery?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val percentage = if (level >= 0 && scale > 0)
            ((level.toDouble() * 100.0) / scale).toInt().coerceIn(0, 100)
        else null
        val chargeState = battery?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val charging: Boolean? = when (chargeState) {
            BatteryManager.BATTERY_STATUS_CHARGING,
            BatteryManager.BATTERY_STATUS_FULL -> true
            BatteryManager.BATTERY_STATUS_DISCHARGING,
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> false
            else -> null
        }
        val network: String = try {
            val connectivity = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = connectivity.getNetworkCapabilities(connectivity.activeNetwork)
            when {
                capabilities == null -> "offline"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "cellular"
                else -> "other"
            }
        } catch (_: Exception) {
            "unknown"
        }
        return mapOf(
            "batteryPercent" to percentage,
            "isCharging" to charging,
            "networkType" to network,
            "model" to Build.MODEL,
            "androidVersion" to Build.VERSION.RELEASE
        )
    }

    /** Launch Android's settings UI; never mutate a setting silently. */
    private fun publicSettingsIntent(id: String?): Intent? {
        val action = when (id) {
            "internet" -> if (Build.VERSION.SDK_INT >= 29)
                Settings.Panel.ACTION_INTERNET_CONNECTIVITY else Settings.ACTION_WIFI_SETTINGS
            "wifi" -> Settings.ACTION_WIFI_SETTINGS
            "bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
            "display" -> Settings.ACTION_DISPLAY_SETTINGS
            "sound" -> Settings.ACTION_SOUND_SETTINGS
            "battery" -> Settings.ACTION_BATTERY_SAVER_SETTINGS
            "storage" -> Settings.ACTION_INTERNAL_STORAGE_SETTINGS
            "apps" -> Settings.ACTION_APPLICATION_SETTINGS
            "notifications" -> Settings.ACTION_APP_NOTIFICATION_SETTINGS
            "privacy" -> Settings.ACTION_PRIVACY_SETTINGS
            "security" -> Settings.ACTION_SECURITY_SETTINGS
            "accessibility" -> Settings.ACTION_ACCESSIBILITY_SETTINGS
            "vpn" -> Settings.ACTION_VPN_SETTINGS
            "wallpaper" -> Intent.ACTION_SET_WALLPAPER
            else -> return null
        }
        return Intent(action).apply {
            if (id == "notifications") {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
        }
    }

}
