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

    /**
     * Only pinned or actually-used launchable apps appear in the Dock.
     * Existing pin order wins over descending usage; no automatic Google/Termux entries.
     */
    fun dockKeys(
        pinned: List<String>,
        usage: Map<String, Long>,
        available: Set<String>,
        limit: Int = 9
    ): List<String> {
        val fixed = pinned.distinct().filter { it in available }.take(limit)
        val frequent = usage.entries.asSequence()
            .filter { (key, score) -> key in available && score > 0L && key !in fixed }
            .sortedWith(compareByDescending<Map.Entry<String, Long>> { it.value }.thenBy { it.key })
            .map { it.key }
            .take((limit - fixed.size).coerceAtLeast(0))
            .toList()
        return fixed + frequent
    }

    fun toggleFavorite(current: List<String>, key: String, limit: Int = 9): List<String> =
        if (key in current) current - key
        else if (current.size < limit) current + key
        else current
}
