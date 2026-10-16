package br.com.dumont.launcher.data

/** Default home appearance uses installed Google app icons, never bundled copies. */
object AppRules {
    private val preferredOrder = listOf(
        "com.android.chrome",
        "com.google.android.gm",
        "com.google.android.calendar",
        "com.google.android.apps.docs",
        "com.google.android.apps.photos",
        "com.google.android.youtube",
        "com.google.android.apps.maps",
        "com.google.android.apps.meetings",
        "com.android.vending"
    )

    fun isGoogleApp(packageName: String): Boolean =
        packageName.startsWith("com.google.") ||
            packageName == "com.android.chrome" ||
            packageName == "com.android.vending"

    fun matches(label: String, packageName: String, query: String): Boolean {
        val term = query.trim()
        return term.isEmpty() || label.contains(term, ignoreCase = true) ||
            packageName.contains(term, ignoreCase = true)
    }

    fun initialDockKeys(packagesAndKeys: List<Pair<String, String>>): List<String> {
        return preferredOrder.mapNotNull { packageName ->
            packagesAndKeys.firstOrNull { it.first == packageName }?.second
        }.take(9)
    }

    fun toggleFavorite(current: List<String>, key: String, limit: Int = 9): List<String> =
        if (key in current) current - key
        else if (current.size < limit) current + key
        else current
}
