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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.Role
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
    onChooseHome: () -> Unit,
    isCatalogLoading: Boolean,
    catalogError: Boolean,
    onRefreshCatalog: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var showApps by rememberSaveable { mutableStateOf(false) }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        val wide = maxWidth >= 600.dp
        val gridCount = if (wide) 5 else if (maxWidth < 370.dp) 3 else 4
        val termux = apps.firstOrNull { it.packageName == "com.termux" }
        val dockApps = remember(apps, favoriteKeys) {
            favoriteKeys.mapNotNull { key -> apps.firstOrNull { it.key == key } }
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = if (wide) 8.dp else 4.dp)
        ) {
            TopBar(
                compact = !wide,
                onApps = { showApps = !showApps },
                onRefresh = onRefreshCatalog,
                onTerminal = termux?.let { entry -> { onLaunch(entry) } }
            )
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Wallpaper(isDark)
                if (showApps) {
                    ApplicationWindow(
                        apps = apps,
                        gridCount = gridCount,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(if (wide) .98f else 1f)
                            .fillMaxHeight(.96f)
                            .heightIn(min = 180.dp, max = 760.dp),
                        isCatalogLoading = isCatalogLoading,
                        catalogError = catalogError,
                        onRefresh = onRefreshCatalog,
                        onClose = { showApps = false },
                        onLaunch = onLaunch,
                        onToggleFavorite = onToggleFavorite,
                        onChooseHome = onChooseHome
                    )
                }
            }
            BottomDock(
                favorites = dockApps,
                termux = termux,
                onLaunch = onLaunch,
                onApps = { showApps = !showApps },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun TopBar(
    compact: Boolean,
    onApps: () -> Unit,
    onRefresh: () -> Unit,
    onTerminal: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(if (compact) 48.dp else 44.dp)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (compact) 2.dp else 12.dp)
    ) {
        Text(
            "DUMONT",
            fontSize = if (compact) 15.sp else 16.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onApps) { Text("Aplicativos", fontSize = if (compact) 11.sp else 13.sp) }
        if (onTerminal != null) {
            TextButton(onClick = onTerminal) { Text("Termux", fontSize = if (compact) 11.sp else 13.sp) }
        }
        if (!compact) {
            TextButton(onClick = onRefresh) { Text("Atualizar", fontSize = 13.sp) }
        }
        // Android renders the actual status indicators outside this app.
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
        drawPath(accent, DumontColors.red.copy(alpha = .35f))
    }
}

@Composable
private fun ApplicationWindow(
    apps: List<AppEntry>,
    gridCount: Int,
    modifier: Modifier,
    isCatalogLoading: Boolean,
    catalogError: Boolean,
    onRefresh: () -> Unit,
    onClose: () -> Unit,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { showAll = !showAll }) {
                        Text(if (showAll) "Google" else "Todos")
                    }
                    TextButton(onClick = onRefresh) { Text("Atualizar", fontSize = 12.sp) }
                    TextButton(onClick = onClose) { Text("Fechar", fontSize = 12.sp) }
                }
            }
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar aplicativos") },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        TextButton(onClick = { query = "" }) {
                            Text("Limpar", fontSize = 12.sp)
                        }
                    }
                },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (showAll) "Todos os aplicativos visíveis · " + visible.size
                else "Google · " + visible.size,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(Modifier.height(8.dp))
            if (isCatalogLoading || catalogError || visible.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        when {
                            isCatalogLoading -> "Carregando aplicativos…"
                            catalogError -> "Falha ao consultar aplicativos. Toque em Atualizar no menu."
                            else -> "Nenhum aplicativo encontrado. Toque em Todos para ampliar a lista."
                        },
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
                Text("Segure para fixar no Dock", fontSize = 12.sp)
                TextButton(onClick = onChooseHome) { Text("Configurar Home", fontSize = 12.sp) }
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
            .combinedClickable(
                role = Role.Button,
                onClickLabel = "Abrir ${app.label}",
                onLongClickLabel = "Adicionar ou remover ${app.label} do Dock",
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 8.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        NativeAppIcon(app, Modifier.size(42.dp))
        Text(
            text = app.label,
            fontSize = 12.sp,
            lineHeight = 15.sp,
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
    termux: AppEntry?,
    onLaunch: (AppEntry) -> Unit,
    onApps: () -> Unit,
    modifier: Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(.99f).height(68.dp),
        shape = RoundedCornerShape(16.dp),
        color = DumontColors.black,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onApps) { Text("Apps", color = Color.White, fontSize = 13.sp) }
            if (termux != null) {
                Column(
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp))
                        .clickable(role = Role.Button, onClick = { onLaunch(termux) }),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    NativeAppIcon(termux, Modifier.size(35.dp))
                    Text("Termux", color = Color.White, fontSize = 10.sp)
                }
            }
            favorites.filterNot { it.packageName == "com.termux" }.forEach { app ->
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp))
                        .clickable(role = Role.Button, onClick = { onLaunch(app) }),
                    contentAlignment = Alignment.Center
                ) {
                    NativeAppIcon(app, Modifier.size(39.dp))
                }
            }
        }
    }
}
