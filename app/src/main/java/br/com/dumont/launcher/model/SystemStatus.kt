package br.com.dumont.launcher.model

/** Device-derived values, never simulated indicators. */
data class SystemStatus(
    val dateTime: String = "",
    val battery: String = "—",
    val network: String = "—"
)
