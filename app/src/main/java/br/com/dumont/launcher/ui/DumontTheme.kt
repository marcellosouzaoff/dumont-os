package br.com.dumont.launcher.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object DumontColors {
    val cloud = Color(0xFFF1EEE7)
    val white = Color(0xFFFFFFFF)
    val charcoal = Color(0xFF4E5353)
    val black = Color(0xFF252525)
    val mocha = Color(0xFFA47864)
    val red = Color(0xFFCD212A)
    val unknown = Color(0xFF4E5353)
}

private val light = lightColorScheme(
    primary = DumontColors.black,
    onPrimary = DumontColors.white,
    background = DumontColors.cloud,
    onBackground = DumontColors.black,
    surface = DumontColors.white,
    onSurface = DumontColors.black,
    outline = DumontColors.charcoal
)
private val dark = darkColorScheme(
    primary = DumontColors.cloud,
    onPrimary = DumontColors.black,
    background = DumontColors.black,
    onBackground = DumontColors.cloud,
    surface = DumontColors.charcoal,
    onSurface = DumontColors.white,
    outline = DumontColors.cloud
)

@Composable
fun DumontTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) dark else light,
        content = content
    )
}
