package com.example.dumont_experience

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.text.Collator
import java.util.Locale

/**
 * DUMONT Flutter Preview's public Android bridge.
 * Only launcher-visible activities are returned and launchable.
 * No privileged system API, QUERY_ALL_PACKAGES, Knox hook, or HOME role.
 */
class MainActivity : FlutterActivity() {
    private val channel = "br.com.dumont.experience/native"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "listApps" -> {
                        try {
                            result.success(launchableActivities().map {
                                mapOf(
                                    "label" to it.label,
                                    "packageName" to it.component.packageName,
                                    "component" to it.component.flattenToString()
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
                    else -> result.notImplemented()
                }
            }
    }

    private data class LaunchableActivity(
        val label: String,
        val component: ComponentName
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
                ComponentName(info.packageName, info.name)
            )
        }.distinctBy { it.component.flattenToString() }
            .sortedWith { a, b -> collation.compare(a.label, b.label) }
    }
}
