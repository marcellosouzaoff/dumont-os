package br.com.dumont.launcher.orchestrator

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppLaunchPolicyTest {
    @Test fun usesExistingTasksWhenAllowedAndAvoidsDestructiveFlags() {
        val flags = AppLaunchPolicy.safeFlags()
        assertTrue(flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
        assertTrue(flags and Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED != 0)
        assertFalse(flags and Intent.FLAG_ACTIVITY_MULTIPLE_TASK != 0)
        assertFalse(flags and Intent.FLAG_ACTIVITY_CLEAR_TASK != 0)
        assertFalse(flags and Intent.FLAG_ACTIVITY_CLEAR_TOP != 0)
        assertFalse(flags and Intent.FLAG_ACTIVITY_NO_HISTORY != 0)
    }
}
