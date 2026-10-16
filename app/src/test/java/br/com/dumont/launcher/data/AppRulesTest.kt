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
}
