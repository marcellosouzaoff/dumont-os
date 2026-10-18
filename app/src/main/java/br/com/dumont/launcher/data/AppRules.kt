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

    /** The starter Dock is useful before Usage Access or local usage history exists. */
    private val essentialPackages = listOf(
        "com.android.chrome",
        "com.sec.android.app.myfiles",
        "com.google.android.apps.nbu.files",
        "com.google.android.gm",
        "com.google.android.calendar",
        "com.google.android.apps.docs",
        "com.google.android.apps.photos",
        "com.android.vending",
        "com.whatsapp"
    )

    fun essentialDockKeys(packagesAndKeys: List<Pair<String, String>>): List<String> =
        essentialPackages.mapNotNull { packageName ->
            packagesAndKeys.firstOrNull { it.first == packageName }?.second
        }.distinct()

    /**
     * Manual pins first, then genuine usage, then installed essentials to fill empty slots.
     * Terminal is always available from the TOP menu, never in the bottom Dock.
     */
    fun dockKeys(
        pinned: List<String>,
        usage: Map<String, Long>,
        available: Set<String>,
        essentials: List<String> = emptyList(),
        excluded: Set<String> = emptySet(),
        limit: Int = 9
    ): List<String> {
        val allowed = available - excluded
        val fixed = pinned.distinct().filter { it in allowed }.take(limit)
        val frequent = usage.entries.asSequence()
            .filter { (key, score) -> key in allowed && score > 0L && key !in fixed }
            .sortedWith(compareByDescending<Map.Entry<String, Long>> { it.value }.thenBy { it.key })
            .map { it.key }
            .take((limit - fixed.size).coerceAtLeast(0))
            .toList()
        val selected = fixed + frequent
        val fallback = essentials.distinct()
            .filter { it in allowed && it !in selected }
            .take((limit - selected.size).coerceAtLeast(0))
        return selected + fallback
    }

    fun toggleFavorite(current: List<String>, key: String, limit: Int = 9): List<String> =
        if (key in current) current - key
        else if (current.size < limit) current + key
        else current
}
