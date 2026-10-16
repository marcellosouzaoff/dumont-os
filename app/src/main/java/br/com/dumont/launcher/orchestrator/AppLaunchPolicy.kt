package br.com.dumont.launcher.orchestrator

import android.content.ComponentName
import android.content.Intent

/**
 * Conservative task policy for Samsung DeX/multi-window.
 * Never force windowing mode, task removal, task reparenting, HOME or Knox access.
 */
object AppLaunchPolicy {
    fun intentFor(component: ComponentName): Intent =
        Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(component)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

    fun safeFlags(): Int = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
}
