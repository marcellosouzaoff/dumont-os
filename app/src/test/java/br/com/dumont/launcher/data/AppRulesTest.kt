package br.com.dumont.launcher.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppRulesTest {
    @Test fun googleIconsArePreferred() {
        assertTrue(AppRules.isGoogleApp("com.google.android.gm"))
        assertTrue(AppRules.isGoogleApp("com.android.chrome"))
        assertFalse(AppRules.isGoogleApp("com.samsung.android.app.launcher"))
    }

    @Test fun searchIgnoresCaseAndWhitespace() {
        assertTrue(AppRules.matches("Google Fotos", "com.google.android.apps.photos", " FOTOS "))
        assertFalse(AppRules.matches("Chrome", "com.android.chrome", "Calendário"))
    }

    @Test fun dockOrderIsDeterministicAndBounded() {
        val entries = listOf(
            "com.google.android.youtube" to "yt",
            "com.android.chrome" to "chrome",
            "com.google.android.gm" to "gmail"
        )
        assertEquals(listOf("chrome", "gmail", "yt"), AppRules.initialDockKeys(entries))
        assertEquals(listOf("chrome", "gmail"), AppRules.toggleFavorite(listOf("chrome"), "gmail"))
        assertEquals(emptyList<String>(), AppRules.toggleFavorite(listOf("chrome"), "chrome"))
        assertEquals(listOf("one"), AppRules.toggleFavorite(listOf("one"), "two", limit = 1))
    }
    @Test fun dockShowsPinnedThenActualUsageOnly() {
        val available = setOf("terminal", "camera", "browser", "files")
        val scores = mapOf("camera" to 6L, "browser" to 12L, "files" to 0L)
        assertEquals(listOf("terminal", "browser", "camera"),
            AppRules.dockKeys(listOf("terminal"), scores, available))
        assertEquals(emptyList<String>(), AppRules.dockKeys(emptyList(), emptyMap(), available))
        assertEquals(listOf("browser"), AppRules.dockKeys(listOf("invalid"), scores, available, limit=1))
    }

    @Test fun essentialsFillEmptyDockButNeverOverridePinsOrUsage() {
        val known = listOf(
            "com.android.chrome" to "chrome",
            "com.google.android.gm" to "gmail",
            "com.android.vending" to "store",
            "com.termux" to "termux"
        )
        val essentials = AppRules.essentialDockKeys(known)
        assertEquals(listOf("chrome", "gmail", "store"), essentials)
        assertEquals(listOf("chrome", "gmail", "store"),
            AppRules.dockKeys(emptyList(), emptyMap(), known.map { it.second }.toSet(), essentials))
        assertEquals(listOf("gmail", "store", "chrome"),
            AppRules.dockKeys(listOf("gmail"), mapOf("store" to 9L), known.map { it.second }.toSet(), essentials))
    }

    @Test fun terminalNeverAppearsInDockEvenWhenPinnedOrUsed() {
        val available = setOf("termux", "chrome", "gmail")
        val essentials = listOf("chrome", "gmail")
        assertEquals(listOf("chrome", "gmail"),
            AppRules.dockKeys(
                pinned = listOf("termux"),
                usage = mapOf("termux" to 999L),
                available = available,
                essentials = essentials,
                excluded = setOf("termux")
            ))
    }

}
