package br.com.dumont.launcher.ui

import android.widget.ImageView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as listItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import br.com.dumont.launcher.data.AppRules
import br.com.dumont.launcher.integration.GalaxyBridge
import br.com.dumont.launcher.model.AppEntry
import br.com.dumont.launcher.model.SystemStatus
import br.com.dumont.launcher.notifications.NotificationFeed
import kotlin.math.roundToInt

/**
 * Original DUMONT wallpaper fills the entire Android Activity, including behind the safe
 * drawing insets. Bars float above it; Android owns the status and navigation system UI.
 */
@Composable
fun DumontHome(
    apps: List<AppEntry>,
    pinnedKeys: List<String>,
    usageScores: Map<String, Long>,
    barOpacity: Float,
    usageAccessGranted: Boolean,
    deviceStatus: SystemStatus,
    wallpaperBitmap: ImageBitmap?,
    notificationsEnabled: Boolean,
    onChooseWallpaper: () -> Unit,
    onResetWallpaper: () -> Unit,
    onNotificationPermission: () -> Unit,
    onNotificationClick: (String) -> Unit,
    onLaunch: (AppEntry) -> Unit,
    onTogglePinned: (AppEntry) -> Unit,
    onOpenSettings: () -> Unit,
    onGalaxyAction: (String) -> Unit,
    onOpenTerminal: () -> Unit,
    onChooseHome: () -> Unit,
    onOpenUsageAccess: () -> Unit,
    onOpacityChange: (Float) -> Unit,
    onOpacitySave: () -> Unit,
    isCatalogLoading: Boolean,
    catalogError: Boolean,
    onRefreshCatalog: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var showApps by rememberSaveable { mutableStateOf(false) }
    var showAppearance by rememberSaveable { mutableStateOf(false) }
    var activeSidebar by rememberSaveable { mutableStateOf("Notificações") }
    var sidebarExpanded by rememberSaveable { mutableStateOf(false) }
    val dockKeys = remember(apps, pinnedKeys, usageScores) {
        AppRules.dockKeys(
            pinned = pinnedKeys,
            usage = usageScores,
            available = apps.map { it.key }.toSet(),
            essentials = AppRules.essentialDockKeys(apps.map { it.packageName to it.key }),
            excluded = apps.filter { it.packageName == "com.termux" }.map { it.key }.toSet()
        )
    }
    val dockApps = remember(apps, dockKeys) {
        dockKeys.mapNotNull { key -> apps.firstOrNull { it.key == key } }
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val wide = maxWidth >= 600.dp
        // Both bars share the same horizontal inset: the original top bar is the standard.
        val barSideInset = if (wide) 12.dp else 4.dp
        if (wallpaperBitmap == null) {
            Wallpaper(isDark)
        } else {
            Image(
                bitmap = wallpaperBitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(Modifier.fillMaxSize().safeDrawingPadding()) {
            TopBar(
                alpha = barOpacity,
                compact = !wide,
                deviceStatus = deviceStatus,
                onApps = { showApps = true; showAppearance = false; sidebarExpanded = false },
                onSettings = onOpenSettings,
                onTerminal = onOpenTerminal,
                onAppearance = {
                    showAppearance = !showAppearance
                    showApps = false
                    sidebarExpanded = false
                },
                modifier = Modifier.align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = barSideInset, vertical = 4.dp)
            )
            if (showApps) {
                ApplicationWindow(
                    apps = apps,
                    pinnedKeys = pinnedKeys,
                    usageScores = usageScores,
                    minCellSize = if (wide) 105.dp else 82.dp,
                    modifier = Modifier.align(Alignment.Center)
                        .fillMaxWidth(if (wide) .91f else .96f)
                        .fillMaxHeight()
                        .padding(top = 64.dp, bottom = 94.dp, end = if (wide) 68.dp else 60.dp),
                    isCatalogLoading = isCatalogLoading,
                    catalogError = catalogError,
                    onRefresh = onRefreshCatalog,
                    onClose = { showApps = false },
                    onLaunch = { app -> showApps = false; onLaunch(app) },
                    onTogglePinned = onTogglePinned
                )
            }
            BottomDock(
                apps = dockApps,
                pinnedKeys = pinnedKeys,
                alpha = barOpacity,
                onLaunch = onLaunch,
                onTogglePinned = onTogglePinned,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = barSideInset, vertical = 8.dp)
            )
            if (sidebarExpanded) Row(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .padding(top = 64.dp, bottom = 94.dp, end = barSideInset)
                    .fillMaxHeight()
                    .fillMaxWidth(if (wide) .43f else .94f)
                    .pointerInput(Unit) {
                        var total = 0f
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, delta -> total += delta },
                            onDragEnd = {
                                if (total < -45f) sidebarExpanded = true
                                if (total > 45f) sidebarExpanded = false
                                total = 0f
                            }
                        )
                    },
                horizontalArrangement = Arrangement.End
            ) {
                Box(Modifier.weight(1f).fillMaxHeight()) {
                        if (activeSidebar == "Notificações") {
                            NotificationPanel(
                                enabled = notificationsEnabled,
                                onEnable = onNotificationPermission,
                                onOpen = onNotificationClick,
                                onClose = { sidebarExpanded = false },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            GalaxyPanel(
                                apps = apps,
                                onOpenAction = onGalaxyAction,
                                onLaunch = onLaunch,
                                onClose = { sidebarExpanded = false },
                                section = activeSidebar,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                RightRail(
                    selected = activeSidebar,
                    alpha = barOpacity,
                    onChoose = { section ->
                        sidebarExpanded = if (activeSidebar == section) !sidebarExpanded else true
                        activeSidebar = section
                        showApps = false
                        showAppearance = false
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            // Invisible, narrow edge target: no rail, icon, background or shadow while closed.
            if (!sidebarExpanded) {
                Box(
                    Modifier.align(Alignment.CenterEnd)
                        .padding(top = 64.dp, bottom = 94.dp, end = barSideInset)
                        .width(26.dp)
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            var displacement = 0f
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { _, delta -> displacement += delta },
                                onDragEnd = {
                                    if (displacement < -38f) {
                                        activeSidebar = "Notificações"
                                        sidebarExpanded = true
                                        showApps = false
                                        showAppearance = false
                                    }
                                    displacement = 0f
                                }
                            )
                        }
                        .semantics { contentDescription = "Abrir painel lateral com gesto da direita" }
                )
            }
            if (showAppearance) {
                AppearancePanel(
                    alpha = barOpacity,
                    usageAccessGranted = usageAccessGranted,
                    onOpacityChange = onOpacityChange,
                    onOpacitySave = onOpacitySave,
                    onChooseHome = onChooseHome,
                    onRefresh = onRefreshCatalog,
                    onUsageAccess = onOpenUsageAccess,
                    hasCustomWallpaper = wallpaperBitmap != null,
                    onChooseWallpaper = onChooseWallpaper,
                    onResetWallpaper = onResetWallpaper,
                    onClose = { showAppearance = false },
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(top = 58.dp, end = if (wide) 16.dp else 6.dp)
                        .fillMaxWidth(if (wide) .34f else .88f)
                        .widthIn(max = 340.dp)
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    alpha: Float,
    compact: Boolean,
    deviceStatus: SystemStatus,
    onApps: () -> Unit,
    onSettings: () -> Unit,
    onTerminal: () -> Unit,
    onAppearance: () -> Unit,
    modifier: Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(15.dp),
        color = DumontColors.black.copy(alpha = alpha),
        shadowElevation = 3.dp
    ) {
        // Equal slots keep system status in the actual center of the header.
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = if (compact) 5.dp else 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (compact) 1.dp else 8.dp)
            ) {
                MenuItem("Aplicativos", onApps, compact)
                MenuItem("Configurações", onSettings, compact)
                MenuItem("Terminal", onTerminal, compact)
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(40.dp, 24.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.fillMaxSize()) {
                        val stroke = 1.4.dp.toPx()
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(size.width * .07f, size.height * .15f),
                            size = androidx.compose.ui.geometry.Size(size.width * .83f, size.height * .70f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(stroke * 2),
                            style = Stroke(width = stroke)
                        )
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(size.width * .92f, size.height * .38f),
                            size = androidx.compose.ui.geometry.Size(size.width * .06f, size.height * .24f)
                        )
                    }
                    Text(deviceStatus.battery.substringBefore(" ").take(4),
                        fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.width(if (compact) 2.dp else 8.dp))
                Text(deviceStatus.dateTime, maxLines = 1, color = Color.White,
                    fontSize = if (compact) 9.sp else 12.sp)
                Spacer(Modifier.width(if (compact) 2.dp else 8.dp))
                WifiStatusIcon(deviceStatus.network)
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onAppearance,
                    contentPadding = PaddingValues(horizontal = if (compact) 4.dp else 12.dp)
                ) {
                    Text(
                        "DUMONT",
                        fontSize = if (compact) 12.sp else 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = .9.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Real Samsung entrypoints and Android Settings screens.
 * Features available only to system/Knox stay in the existing Samsung environment.
 */
@Composable
private fun GalaxyPanel(
    apps: List<AppEntry>,
    onOpenAction: (String) -> Unit,
    onLaunch: (AppEntry) -> Unit,
    onClose: () -> Unit,
    section: String,
    modifier: Modifier
) {
    val samsungApps = remember(apps) { GalaxyBridge.availableSamsungApps(apps) }
    val sections = remember(section) {
        GalaxyBridge.systemActions.filter { it.section == section }.groupBy { it.section }
    }
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 12.dp
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    section,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(onClick = onClose) { Text("Fechar") }
            }
            Text(
                "Aplicativos instalados e configurações oficiais da Samsung/Android. " +
                    "Recursos internos protegidos continuam na One UI.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                sections.forEach { (section, actions) ->
                    item(key = "section-$section") {
                        Spacer(Modifier.height(10.dp))
                        Text(section, fontWeight = FontWeight.SemiBold)
                    }
                    listItems(actions, key = { "settings-" + it.id }) { action ->
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenAction(action.id) }
                                .padding(horizontal = 12.dp, vertical = 9.dp)
                        ) {
                            Text(action.title, color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium)
                            Text(action.description, fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RightRail(
    selected: String,
    alpha: Float,
    onChoose: (String) -> Unit,
    modifier: Modifier
) {
    val sections = listOf("Notificações", "Conexões", "Dispositivo", "Segurança", "Sistema")
    Surface(modifier = modifier.width(60.dp), shape = RoundedCornerShape(14.dp),
        color = DumontColors.black.copy(alpha = alpha.coerceAtLeast(.88f)), shadowElevation = 6.dp) {
        Column(Modifier.fillMaxSize().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sections.forEachIndexed { i, section ->
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(10.dp))
                    .background(if (selected == section) DumontColors.mocha.copy(alpha = .92f)
                        else Color.Transparent)
                    .clickable { onChoose(section) }
                    .semantics { contentDescription = section },
                    contentAlignment = Alignment.Center) {
                    Text(listOf("●", "⌁", "▣", "◇", "⚙")[i],
                        color = Color.White, fontSize = 23.sp)
                }
            }
        }
    }
}

/** Neutral outline bell: standard notification affordance, not a DUMONT mark. */
@Composable
private fun NotificationBell() {
    Canvas(Modifier.size(21.dp)) {
        val w = size.width
        val h = size.height
        val bell = Path().apply {
            moveTo(w * .18f, h * .73f)
            lineTo(w * .29f, h * .58f)
            lineTo(w * .29f, h * .39f)
            cubicTo(w * .29f, h * .12f, w * .71f, h * .12f, w * .71f, h * .39f)
            lineTo(w * .71f, h * .58f)
            lineTo(w * .82f, h * .73f)
            close()
        }
        drawPath(
            bell,
            Color.White,
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        )
        drawCircle(Color.White, radius = 1.5.dp.toPx(), center = Offset(w * .5f, h * .82f))
    }
}

@Composable
private fun WifiStatusIcon(network: String) {
    Canvas(
        Modifier.size(20.dp).semantics { contentDescription = network }
    ) {
        val stroke = 1.8.dp.toPx()
        for (r in listOf(.90f, .60f, .30f)) {
            val d = size.minDimension * r
            drawArc(
                color = Color.White,
                startAngle = 215f,
                sweepAngle = 110f,
                useCenter = false,
                topLeft = Offset((size.width - d) / 2, (size.height - d) / 2),
                size = androidx.compose.ui.geometry.Size(d, d),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        if (network != "Wi-Fi") {
            drawLine(Color.White.copy(alpha=.7f), Offset(0f, size.height),
                Offset(size.width, 0f), strokeWidth=stroke)
        }
    }
}

@Composable
private fun MenuItem(label: String, onClick: () -> Unit, compact: Boolean) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = if (compact) 4.dp else 8.dp)
    ) {
        Text(label, color = Color.White, fontSize = if (compact) 11.sp else 13.sp, maxLines = 1)
    }
}

@Composable
private fun AppearancePanel(
    alpha: Float,
    usageAccessGranted: Boolean,
    onOpacityChange: (Float) -> Unit,
    onOpacitySave: () -> Unit,
    onChooseHome: () -> Unit,
    onRefresh: () -> Unit,
    onUsageAccess: () -> Unit,
    hasCustomWallpaper: Boolean,
    onChooseWallpaper: () -> Unit,
    onResetWallpaper: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Aparência DUMONT", fontWeight = FontWeight.SemiBold)
                TextButton(onClick = onClose) { Text("Fechar") }
            }
            Text("Transparência das barras: ${(100 - alpha * 100).roundToInt()}%")
            Text("Cor sólida: preto DUMONT (#252525). Em 0%, as barras são opacas.", fontSize = 12.sp)
            Slider(
                value = alpha,
                onValueChange = onOpacityChange,
                onValueChangeFinished = onOpacitySave,
                valueRange = .20f..1f
            )
            Text(
                "Os favoritos são fixados ao segurar um aplicativo. Outros aparecem após o uso.",
                fontSize = 12.sp
            )
            Text(
                if (usageAccessGranted) "Mais usados: últimos 7 dias no dispositivo"
                else "Mais usados: abertos pelo DUMONT",
                fontSize = 12.sp
            )
            if (!usageAccessGranted) {
                TextButton(onClick = onUsageAccess) { Text("Autorizar acesso ao uso") }
            }
            TextButton(onClick = onChooseWallpaper) { Text("Escolher papel de parede") }
            if (hasCustomWallpaper) {
                TextButton(onClick = onResetWallpaper) { Text("Usar papel de parede original") }
            }
            TextButton(onClick = onRefresh) { Text("Atualizar aplicativos") }
            TextButton(onClick = onChooseHome) { Text("Escolher aplicativo de tela inicial") }
        }
    }
}

@Composable
private fun NotificationPanel(
    enabled: Boolean,
    onEnable: () -> Unit,
    onOpen: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier
) {
    val entries by NotificationFeed.entries.collectAsState()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notificações", fontWeight = FontWeight.SemiBold)
                TextButton(onClick = onClose) { Text("Fechar") }
            }
            if (!enabled) {
                Text(
                    "Autorize o acesso às notificações nas Configurações do Android. " +
                        "O DUMONT não guarda ou envia mensagens.",
                    fontSize = 13.sp
                )
                TextButton(onClick = onEnable) { Text("Permitir acesso no Android") }
            } else if (entries.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma notificação disponível.", textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(9.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    item {
                        Text("Mural de notificações · ${entries.size}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp)
                    }
                    listItems(entries, key = { it.key }) { notification ->
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onOpen(notification.key) }
                                .background(DumontColors.charcoal.copy(alpha = .16f))
                                .border(
                                    1.dp,
                                    DumontColors.mocha.copy(alpha = .35f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Text(
                                notification.title,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                            if (notification.detail.isNotBlank()) {
                                Text(
                                    notification.detail,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                notification.packageName,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
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
    pinnedKeys: List<String>,
    usageScores: Map<String, Long>,
    minCellSize: Dp,
    modifier: Modifier,
    isCatalogLoading: Boolean,
    catalogError: Boolean,
    onRefresh: () -> Unit,
    onClose: () -> Unit,
    onLaunch: (AppEntry) -> Unit,
    onTogglePinned: (AppEntry) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Todos") }
    val sortedApps = remember(apps) { apps.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label }) }
    val visible = remember(sortedApps, query, category, usageScores) {
        sortedApps.filter { app ->
            AppRules.matches(app.label, app.packageName, query) && when (category) {
                "Samsung" -> app.packageName.startsWith("com.samsung.") ||
                    app.packageName.startsWith("com.sec.")
                "Google" -> app.packageName.startsWith("com.google.") ||
                    app.packageName == "com.android.chrome"
                "Sistema" -> app.packageName.startsWith("com.android.") ||
                    app.packageName.startsWith("com.sec.android.")
                "Recentes" -> (usageScores[app.key] ?: 0L) > 0L
                else -> true
            }
        }.let { result ->
            if (category == "Recentes") result.sortedByDescending { usageScores[it.key] ?: 0L }
            else result
        }
    }

    Surface(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = .97f),
        shadowElevation = 9.dp
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Aplicativos", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        TextButton(onClick = { query = "" }) { Text("Limpar", fontSize = 12.sp) }
                    }
                },
                singleLine = true
            )
            Spacer(Modifier.height(6.dp))
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                listOf("Todos", "Recentes", "Samsung", "Google", "Sistema").forEach { tab ->
                    TextButton(onClick = { category = tab }) {
                        Text(
                            tab,
                            color = if (category == tab) DumontColors.mocha
                                else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (category == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            Text(
                if (category == "Recentes") "Aplicativos retomáveis · ${visible.size} (não representa janelas em execução)"
                else "Aplicativos disponíveis · ${visible.size}", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(8.dp))
            if (isCatalogLoading || catalogError || visible.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        when {
                            isCatalogLoading -> "Carregando aplicativos…"
                            catalogError -> "Falha na consulta. Toque em Atualizar."
                            else -> "Nenhum aplicativo corresponde à busca."
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minCellSize),
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(visible, key = { it.key }) { app ->
                        AppTile(
                            app = app,
                            isPinned = app.key in pinnedKeys && app.packageName != "com.termux",
                            onClick = { onLaunch(app) },
                            onLongClick = { onTogglePinned(app) }
                        )
                    }
                }
            }
            Text("Segure um ícone para fixar ou desafixar no Dock", fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppTile(app: AppEntry, isPinned: Boolean, onClick: () -> Unit, onLongClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                role = Role.Button,
                onClickLabel = "Abrir ${app.label}",
                onLongClickLabel = "Fixar ou desafixar ${app.label} do Dock",
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 6.dp, horizontal = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NativeAppIcon(app, Modifier.size(42.dp))
        Text(app.label, fontSize = 12.sp, lineHeight = 14.sp,
            textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface)
        if (isPinned) Text("Fixado", fontSize = 10.sp, color = DumontColors.mocha)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BottomDock(
    apps: List<AppEntry>,
    pinnedKeys: List<String>,
    alpha: Float,
    onLaunch: (AppEntry) -> Unit,
    onTogglePinned: (AppEntry) -> Unit,
    modifier: Modifier
) {
    Surface(
        modifier = modifier.height(76.dp),
        shape = RoundedCornerShape(20.dp),
        color = DumontColors.black.copy(alpha = alpha),
        shadowElevation = 5.dp
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                apps.forEach { app ->
                    Column(
                        modifier = Modifier.size(58.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .combinedClickable(
                                role = Role.Button,
                                onClickLabel = "Abrir ${app.label}",
                                onLongClickLabel = "Fixar ou desafixar ${app.label}",
                                onClick = { onLaunch(app) },
                                onLongClick = { onTogglePinned(app) }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        NativeAppIcon(app, Modifier.size(41.dp))
                        if (app.key in pinnedKeys) {
                            Text("•", color = Color.White, fontSize = 10.sp, lineHeight = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
