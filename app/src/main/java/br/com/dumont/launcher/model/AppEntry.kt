package br.com.dumont.launcher.model

import android.content.ComponentName
import android.graphics.drawable.Drawable

/** A launchable activity, discovered through Android's public PackageManager API. */
data class AppEntry(
    val label: String,
    val packageName: String,
    val component: ComponentName,
    val icon: Drawable
) {
    val key: String get() = component.flattenToString()
}
