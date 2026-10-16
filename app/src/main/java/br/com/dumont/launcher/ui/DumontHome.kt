package br.com.dumont.launcher.ui

import android.widget.ImageView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import br.com.dumont.launcher.data.AppRules
import br.com.dumont.launcher.model.AppEntry

@Composable
fun DumontHome(
    apps: List<AppEntry>,
    favoriteKeys: List<String>,
    onLaunch: (AppEntry) -> Unit,
    onToggleFavorite: (AppEntry) -> Unit,
    onChooseHome: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        val wide = maxWidth >= 600.dp
        val gridCount = if (wide) 5 else if (maxWidth < 370.dp) 3 else 4
        val dockApps = remember(apps, favoriteKeys) {
            favoriteKeys.mapNotNull { key -> apps.firstOrNull { it.key == key } }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = if (wide) 20.dp else 8.dp)
        ) {
            TopBar()
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Wallpaper(isDark)
                ApplicationWindow(
                    apps = apps,
                    gridCount = gridCount,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(if (wide) .86f else .98f)
                        .fillMaxHeight(if (wide) .86f else .94f)
                        .heightIn(min = 180.dp, max = 620.dp),
                    onLaunch = onLaunch,
                    onToggleFavorite = onToggleFavorite,
                    onChooseHome = onChooseHome
                )
            }
            BottomDock(dockApps, onLaunch, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "DUMONT",
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.6.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        // The real Android status bar shows system indicators. Never fabricate them here.
    }
}

@Composable
private fun Wallpaper(isDark: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val wave1 = Path().apply {
            moveTo(0f, h * .65f)
            cubicTo(w * .25f, h * .43f, w * .5f, h * .9f, w, h * .53f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(wave1, DumontColors.mocha.copy(alpha = if (isDark) .35f else .42f))
        val wave2 = Path().apply {
            moveTo(0f, h * .88f)
            cubicTo(w * .4f, h * .6f, w * .6f, h * .99f, w, h * .66f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(wave2, DumontColors.black.copy(alpha = if (isDark) .65f else .88f))
        val accent = Path().apply {
            moveTo(w * .65f, 0f)
            cubicTo(w * .42f, h * .1f, w * .92f, h * .25f, w, h * .36f)
            lineTo(w, 0f)
            close()
        }
        drawPath(accent, DumontColors.red.copy(alpha = .55f))
    }
}

@Composable
private fun ApplicationWindow(
    apps: List<AppEntry>,
    gridCount: Int,
    modifier: Modifier,
    onLaunch: (AppEntry) -> Unit,
    onToggleFavorite: (AppEntry) -> Unit,
    onChooseHome: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var showAll by remember { mutableStateOf(false) }
    val visible = remember(apps, query, showAll) {
        apps.filter { app ->
            (showAll || AppRules.isGoogleApp(app.packageName)) &&
                AppRules.matches(app.label, app.packageName, query)
        }
    }

    Surface(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Aplicativos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { showAll = !showAll }) {
                    Text(if (showAll) "Google" else "Todos")
                }
            }
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar aplicativos") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (showAll) "Todos os aplicativos visíveis · " + visible.size
                else "Google · " + visible.size,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(Modifier.height(8.dp))
            if (visible.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        if (apps.isEmpty()) "Carregando aplicativos…"
                        else "Nenhum aplicativo encontrado. Toque em Todos para ampliar a lista.",
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridCount),
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(visible, key = { it.key }) { app ->
                        AppTile(app, onClick = { onLaunch(app) }, onLongClick = { onToggleFavorite(app) })
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Segure um ícone para fixar no Dock", fontSize = 10.sp)
                TextButton(onClick = onChooseHome) { Text("Tela inicial", fontSize = 11.sp) }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppTile(app: AppEntry, onClick: () -> Unit, onLongClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 8.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        NativeAppIcon(app, Modifier.size(42.dp))
        Text(
            text = app.label,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun NativeAppIcon(app: AppEntry, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                contentDescription = app.label
            }
        },
        update = { view ->
            view.contentDescription = app.label
            view.setImageDrawable(app.icon.constantState?.newDrawable()?.mutate() ?: app.icon)
        }
    )
}

@Composable
private fun BottomDock(
    favorites: List<AppEntry>,
    onLaunch: (AppEntry) -> Unit,
    modifier: Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(.98f).height(72.dp),
        shape = RoundedCornerShape(20.dp),
        color = DumontColors.black,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (favorites.isEmpty()) {
                Text("Dock · fixe aplicativos Google", color = Color.White, fontSize = 13.sp)
            } else {
                favorites.forEach { app ->
                    Box(
                        modifier = Modifier.size(54.dp).clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = { onLaunch(app) }),
                        contentAlignment = Alignment.Center
                    ) {
                        NativeAppIcon(app, Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}
