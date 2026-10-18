package br.com.dumont.launcher.integration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class GalaxyBridgeTest {
    @Test fun allSystemActionsAreExplicitAndUnique() {
        val ids = GalaxyBridge.systemActions.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        assertEquals(ids.toSet(), GalaxyBridge.publicSettingsIds)
        assertTrue(setOf("internet", "wifi", "bluetooth", "battery", "privacy", "apps")
            .all { it in ids })
        assertFalse("knox_root" in ids)
        assertFalse("disable_knox" in ids)
    }

    @Test fun galaxyAppMappingIsNamedAndDoesNotAssumePresence() {
        val pkg = GalaxyBridge.samsungPackages.map { it.packageName }
        assertEquals(pkg.size, pkg.toSet().size)
        assertTrue("com.sec.android.gallery3d" in pkg)
        assertTrue("com.sec.android.app.camera" in pkg)
        assertTrue("com.sec.android.app.myfiles" in pkg)
        assertFalse("com.samsung.android.knox.kpecore" in pkg)
    }
}
