package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ColorBlock
import com.example.model.EmbroideryDesign
import com.example.model.StitchType
import com.example.samples.EmbroiderySampleLibrary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WilcomBorder
import com.example.ui.theme.WilcomCyan
import com.example.ui.theme.WilcomGold
import com.example.ui.theme.WilcomGreen
import com.example.ui.theme.WilcomPanelBg
import com.example.ui.theme.WilcomPanelBgLight
import com.example.viewmodel.ChenilleType
import com.example.viewmodel.EmbroideryViewModel
import com.example.viewmodel.SelectionMode
import com.example.viewmodel.ToolTab
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WilcomTopBar(
    is3DActive: Boolean,
    onToggle3D: () -> Unit,
    isFooterVisible: Boolean,
    onToggleFooter: () -> Unit,
    isSidebarVisible: Boolean,
    onToggleSidebar: () -> Unit,
    onOpenSamples: () -> Unit,
    onOpenFilePicker: () -> Unit,
    onConvertChenille: () -> Unit,
    onAutoDigitize: () -> Unit,
    onExportDst: () -> Unit,
    onOpenProductionSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐ WELCOME MEGA v4.0",
                        color = WilcomGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "محرك التحكم الذكي بالغرز والتكبير",
                    color = TextSecondary,
                    fontSize = 10.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            // Toggle Footer Button
            OutlinedButton(
                onClick = onToggleFooter,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = WilcomGold
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    WilcomGold.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .testTag("toggle_footer_button")
            ) {
                Text(
                    text = if (isFooterVisible) "🔽 إخفاء البار" else "🔽 إظهار البار",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Toggle Sidebar / Tool Panels Button
            OutlinedButton(
                onClick = onToggleSidebar,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = WilcomGold
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    WilcomGold.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .testTag("toggle_sidebar_button")
            ) {
                Text(
                    text = if (isSidebarVisible) "◀️ إخفاء القوائم" else "▶️ إظهار القوائم",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 3D View Toggle
            OutlinedButton(
                onClick = onToggle3D,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (is3DActive) WilcomGold else TextSecondary
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (is3DActive) WilcomGold else WilcomBorder
                ),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .testTag("toggle_3d_button")
            ) {
                Icon(
                    Icons.Default.ViewInAr,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = if (is3DActive) "وضع 3D (مفعل)" else "وضع 3D (معطل)",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Open Button with dropdown
            var showOpenDropdown by remember { mutableStateOf(false) }
            Box {
                Button(
                    onClick = { showOpenDropdown = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WilcomGold,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .testTag("open_file_button")
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("📁 فتح ملف", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                DropdownMenu(
                    expanded = showOpenDropdown,
                    onDismissRequest = { showOpenDropdown = false },
                    modifier = Modifier.background(WilcomPanelBg)
                ) {
                    DropdownMenuItem(
                        text = { Text("📁 فتح ملف ماكينة (DST, PES, JEF, EXP, DSB)", color = TextPrimary, fontSize = 12.5.sp) },
                        onClick = {
                            showOpenDropdown = false
                            onOpenFilePicker()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("✨ مكتبة تصاميم ويلكم الجاهزة", color = WilcomGold, fontSize = 12.5.sp) },
                        onClick = {
                            showOpenDropdown = false
                            onOpenSamples()
                        }
                    )
                }
            }

            // Chenille Engine Convert Button (Glowing Coral/Orange)
            Button(
                onClick = onConvertChenille,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .testTag("convert_chenille_header_button")
            ) {
                Text("⚡ تحويل لشنيطة", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }

            // Auto-Digitizing Button (Green)
            Button(
                onClick = onAutoDigitize,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WilcomGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .testTag("auto_digitize_button")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("⚡ رقمنة تلقائية", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }

            // Save DST Button (Blue)
            Button(
                onClick = onExportDst,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WilcomCyan,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .testTag("save_dst_button")
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("💾 حفظ DST", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }

            // Production Sheet Button
            IconButton(
                onClick = onOpenProductionSheet,
                modifier = Modifier.testTag("production_sheet_button")
            ) {
                Icon(Icons.Default.Tune, contentDescription = "كرت التشغيل", tint = TextPrimary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WilcomPanelBg,
            titleContentColor = TextPrimary
        ),
        modifier = modifier.border(
            width = 1.dp,
            color = WilcomBorder
        )
    )
}

@Composable
fun WilcomCompactHeader(
    totalStitches: Int,
    onOpenSamples: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WilcomPanelBg,
        tonalElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = WilcomGold
            )
            .testTag("compact_header")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo / Title area
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "⭐ WELCOME MEGA v4.0",
                    color = WilcomGold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Right side: Quick samples & Stitches count badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onOpenSamples,
                    colors = ButtonDefaults.textButtonColors(contentColor = WilcomCyan),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("open_samples_header_btn")
                ) {
                    Text("✨ نماذج جاهزة", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF22222A),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(1.dp, WilcomGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("stitchCount")
                ) {
                    Text(
                        text = "الغرز: ${"%,d".format(totalStitches)}",
                        color = WilcomGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WilcomBottomToolbar(
    is3DActive: Boolean,
    isChenilleActive: Boolean,
    onOpenFilePicker: () -> Unit,
    onExportDst: () -> Unit,
    onConvertChenille: () -> Unit,
    onToggle3D: () -> Unit,
    onReset: () -> Unit,
    isToolDockExpanded: Boolean,
    onToggleToolDock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WilcomPanelBg,
        tonalElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = WilcomGold
            )
            .testTag("bottom_toolbar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1: Primary actions (Upload DST & Save DST)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 📁 رفع ملف DST (Primary Yellow)
                Button(
                    onClick = onOpenFilePicker,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WilcomGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("uploadDstBtn")
                ) {
                    Icon(
                        Icons.Default.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📁 رفع ملف DST",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 💾 حفظ الملف DST (Green #4caf50)
                Button(
                    onClick = onExportDst,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("saveDstBtn")
                ) {
                    Icon(
                        Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "💾 حفظ الملف DST",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Row 2: Secondary transformations & controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ⚡ تحويل لشنيطة (Gradient #ff9800 to #ff5722)
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF9800), Color(0xFFFF5722))
                            )
                        )
                        .clickable(onClick = onConvertChenille)
                        .padding(vertical = 10.dp, horizontal = 4.dp)
                        .testTag("chenilleBtn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡ تحويل لشنيطة",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // 🌐 وضع 3D (Toggle)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (is3DActive) WilcomCyan else Color(0xFF22222A))
                        .border(
                            1.dp,
                            if (is3DActive) WilcomCyan else WilcomCyan.copy(alpha = 0.5f),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable(onClick = onToggle3D)
                        .padding(vertical = 10.dp, horizontal = 4.dp)
                        .testTag("toggle3dBtn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (is3DActive) "🌐 وضع 3D (مفعل)" else "🌐 وضع 3D",
                        color = if (is3DActive) Color.Black else TextPrimary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // 🔄 إعادة ضبط (Reset)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF22222A))
                        .border(1.dp, WilcomBorder, RoundedCornerShape(6.dp))
                        .clickable(onClick = onReset)
                        .padding(vertical = 10.dp, horizontal = 4.dp)
                        .testTag("resetBtn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔄 إعادة ضبط",
                        color = TextPrimary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // 🛠️ لوحة الأدوات (Toggle Tool Dock)
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isToolDockExpanded) WilcomGold.copy(alpha = 0.2f) else Color(0xFF22222A))
                        .border(
                            1.dp,
                            if (isToolDockExpanded) WilcomGold else WilcomBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable(onClick = onToggleToolDock)
                        .padding(vertical = 10.dp, horizontal = 4.dp)
                        .testTag("toggle_tool_dock_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isToolDockExpanded) "🛠️ إخفاء الأدوات" else "🛠️ الأدوات الذكية",
                        color = if (isToolDockExpanded) WilcomGold else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun WilcomBottomFooter(
    modifier: Modifier = Modifier
) {
    Surface(
        color = WilcomPanelBg,
        tonalElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = WilcomBorder)
            .testTag("footer")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WELCOME MEGA Engine - Active",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = "شاشة الكانفاس فارغة بالأعلى",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun WilcomBottomBar(
    totalStitches: Int,
    isChenille: Boolean,
    rpm: Int,
    widthMm: Float,
    heightMm: Float,
    modifier: Modifier = Modifier
) {
    val estMinutes = ceil((totalStitches.toFloat()) / rpm.toFloat()).toInt()

    Surface(
        color = WilcomPanelBg,
        tonalElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, WilcomBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "عدد الغرز: ${"%,d".format(totalStitches)} غرزة",
                color = WilcomGold,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isChenille) "النمط: شنيطة" else "النمط: عادي",
                color = if (isChenille) Color(0xFFFF5722) else TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "WELCOME MEGA - Active Engine",
                color = if (isChenille) WilcomCyan else TextSecondary,
                fontSize = 10.5.sp
            )
            Text(
                text = "الوقت المقدر: $estMinutes دقيقة",
                color = TextPrimary,
                fontSize = 11.5.sp
            )
        }
    }
}

@Composable
fun WilcomToolDock(
    activeTab: ToolTab,
    onTabSelected: (ToolTab) -> Unit,
    viewModel: EmbroideryViewModel,
    design: EmbroideryDesign,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = WilcomPanelBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, WilcomBorder),
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Tabs Row
            ScrollableTabRow(
                selectedTabIndex = activeTab.ordinal,
                edgePadding = 4.dp,
                containerColor = WilcomPanelBgLight,
                contentColor = WilcomGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab.ordinal]),
                        color = WilcomGold
                    )
                }
            ) {
                Tab(
                    selected = activeTab == ToolTab.SCALE,
                    onClick = { onTabSelected(ToolTab.SCALE) },
                    text = { Text("🔍 التكبير والتصغير", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeTab == ToolTab.SELECTION,
                    onClick = { onTabSelected(ToolTab.SELECTION) },
                    text = { Text("✂️ التحديد والقص", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeTab == ToolTab.STITCH_TYPES,
                    onClick = { onTabSelected(ToolTab.STITCH_TYPES) },
                    text = { Text("🧵 أنواع الغرز", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeTab == ToolTab.CHENILLE,
                    onClick = { onTabSelected(ToolTab.CHENILLE) },
                    text = { Text("🧶 الشنيطة", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeTab == ToolTab.DENSITY,
                    onClick = { onTabSelected(ToolTab.DENSITY) },
                    text = { Text("⚙️ الكثافة والسحب", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeTab == ToolTab.COLORS,
                    onClick = { onTabSelected(ToolTab.COLORS) },
                    text = { Text("🎨 الألوان", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeTab == ToolTab.SIMULATOR,
                    onClick = { onTabSelected(ToolTab.SIMULATOR) },
                    text = { Text("▶️ المحاكي", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold) }
                )
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                when (activeTab) {
                    ToolTab.SCALE -> SmartScalePanelContent(viewModel, design)
                    ToolTab.SELECTION -> SelectionPanelContent(viewModel)
                    ToolTab.STITCH_TYPES -> StitchTypesPanelContent(viewModel)
                    ToolTab.CHENILLE -> ChenillePanelContent(viewModel)
                    ToolTab.DENSITY -> DensityPanelContent(viewModel)
                    ToolTab.COLORS -> ColorSequencePanelContent(viewModel, design)
                    ToolTab.SIMULATOR -> SimulatorPanelContent(viewModel, design)
                }
            }
        }
    }
}

/**
 * محرك التكبير والتصغير الذكي للغرز (Smart Scaling & Density Engine)
 */
@Composable
private fun SmartScalePanelContent(viewModel: EmbroideryViewModel, design: EmbroideryDesign) {
    val globalScale by viewModel.globalScale.collectAsStateWithLifecycle()
    val selectionScale by viewModel.selectionScale.collectAsStateWithLifecycle()
    val direction by viewModel.chenilleLoopDirection.collectAsStateWithLifecycle()
    val thickness by viewModel.chenilleThickness.collectAsStateWithLifecycle()
    val isChenille by viewModel.isChenilleMode.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. قسم تكبير وتصغير الرسمة بالكامل
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B22)),
            border = androidx.compose.foundation.BorderStroke(1.dp, WilcomGold.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "🔍 تكبير وتصغير الرسمة بالكامل",
                    color = WilcomGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حجم الرسمة (Global Scale):",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${globalScale.toInt()}%",
                        color = WilcomGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = globalScale,
                    onValueChange = { viewModel.applyGlobalScale(it) },
                    valueRange = 50f..200f,
                    colors = SliderDefaults.colors(
                        thumbColor = WilcomGold,
                        activeTrackColor = WilcomGold,
                        inactiveTrackColor = WilcomBorder
                    ),
                    modifier = Modifier.testTag("global_scale_slider")
                )
            }
        }

        // 2. قسم تكبير وتصغير المنطقة المحددة مع حذف وزيادة الغرز أوتوماتيكياً
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B22)),
            border = androidx.compose.foundation.BorderStroke(1.dp, WilcomCyan.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "✂️ تحديد ومنطقة التعديل (Selection Scale)",
                    color = WilcomCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حجم المنطقة المحددة:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${selectionScale.toInt()}%",
                        color = WilcomCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = selectionScale,
                    onValueChange = { viewModel.applySelectionScale(it) },
                    valueRange = 40f..250f,
                    colors = SliderDefaults.colors(
                        thumbColor = WilcomCyan,
                        activeTrackColor = WilcomCyan,
                        inactiveTrackColor = WilcomBorder
                    ),
                    modifier = Modifier.testTag("selection_scale_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.resetScales() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WilcomBorder),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reset_scale_button")
                    ) {
                        Text("إعادة ضبط", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { viewModel.optimizeStitchesDensity() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WilcomCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("optimize_density_button")
                    ) {
                        Text("تحسين وغربلة الغرز", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. قسم إعدادات الشنيطة
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B22)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5722).copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "🧶 إعدادات الشنيطة",
                    color = Color(0xFFFF5722),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("اتجاه الدوران:", color = TextSecondary, fontSize = 11.sp)
                    Text("${direction.toInt()}°", color = Color(0xFFFF9800), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = direction,
                    onValueChange = { viewModel.setChenilleDirection(it) },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFF9800),
                        activeTrackColor = Color(0xFFFF9800),
                        inactiveTrackColor = WilcomBorder
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("سمك الخيط:", color = TextSecondary, fontSize = 11.sp)
                    Text("${"%.1f".format(thickness)} مم", color = Color(0xFFFF5722), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = thickness,
                    onValueChange = { viewModel.setChenilleThickness(it) },
                    valueRange = 2.0f..9.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFF5722),
                        activeTrackColor = Color(0xFFFF5722),
                        inactiveTrackColor = WilcomBorder
                    )
                )
            }
        }

        // 4. قسم عداد الغرز والدقة الحقيقية
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16161D)),
            border = androidx.compose.foundation.BorderStroke(1.dp, WilcomBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "📊 عداد الغرز والدقة الحقيقية",
                    color = WilcomGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "الحالة: " + if (isChenille) "تطريز شنيطة مجسم مفعل" else "تطريز عادي (محسّن)",
                    color = if (isChenille) Color(0xFFFF5722) else WilcomCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "الغرز تتكيف أوتوماتيكياً مع التكبير والتصغير لمنع القطع.",
                    color = TextSecondary,
                    fontSize = 10.5.sp
                )
            }
        }
    }
}

@Composable
private fun SelectionPanelContent(viewModel: EmbroideryViewModel) {
    val selectionMode by remember { androidx.compose.runtime.derivedStateOf { viewModel.selectionMode.value } }
    val selectedIndices by remember { androidx.compose.runtime.derivedStateOf { viewModel.selectedStitchIndices.value } }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("نوع التحديد:", color = TextSecondary, fontSize = 11.5.sp)

            FilterChip(
                selected = selectionMode == SelectionMode.LASSO,
                onClick = { viewModel.setSelectionMode(SelectionMode.LASSO) },
                label = { Text("تحديد حر (Lasso)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WilcomGold,
                    selectedLabelColor = Color.Black
                )
            )
            FilterChip(
                selected = selectionMode == SelectionMode.RECTANGLE,
                onClick = { viewModel.setSelectionMode(SelectionMode.RECTANGLE) },
                label = { Text("مستطيل (Rect)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WilcomGold,
                    selectedLabelColor = Color.Black
                )
            )
            FilterChip(
                selected = selectionMode == SelectionMode.MERGED,
                onClick = { viewModel.setSelectionMode(SelectionMode.MERGED) },
                label = { Text("مدمج (Merged)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WilcomGold,
                    selectedLabelColor = Color.Black
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = { viewModel.cutSelectedStitches() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282832)),
                modifier = Modifier
                    .weight(1f)
                    .testTag("cut_stitches_button")
            ) {
                Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(13.dp), tint = WilcomGold)
                Spacer(modifier = Modifier.width(4.dp))
                Text("قص الجزء المحدد", fontSize = 10.5.sp, color = TextPrimary)
            }

            Button(
                onClick = { viewModel.mergeSelectedStitches() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282832)),
                modifier = Modifier
                    .weight(1f)
                    .testTag("merge_stitches_button")
            ) {
                Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(13.dp), tint = WilcomCyan)
                Spacer(modifier = Modifier.width(4.dp))
                Text("دمج الطبقات (Merge)", fontSize = 10.5.sp, color = TextPrimary)
            }

            Button(
                onClick = { viewModel.deleteSelectedStitches() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D1A1A)),
                modifier = Modifier
                    .weight(0.7f)
                    .testTag("delete_stitches_button")
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.Red)
                Spacer(modifier = Modifier.width(3.dp))
                Text("حذف", fontSize = 10.5.sp, color = Color.White)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.undo() }, modifier = Modifier.testTag("undo_button")) {
                Icon(Icons.Default.Undo, contentDescription = "تراجع", tint = TextPrimary)
            }
            IconButton(onClick = { viewModel.redo() }, modifier = Modifier.testTag("redo_button")) {
                Icon(Icons.Default.Redo, contentDescription = "إعادة", tint = TextPrimary)
            }
            IconButton(onClick = { viewModel.centerDesign() }, modifier = Modifier.testTag("center_design_button")) {
                Icon(Icons.Default.CenterFocusStrong, contentDescription = "توسيط", tint = WilcomGold)
            }
            IconButton(onClick = { viewModel.rotateDesign(90f) }, modifier = Modifier.testTag("rotate_design_button")) {
                Icon(Icons.Default.RotateRight, contentDescription = "تدوير 90°", tint = WilcomCyan)
            }
            IconButton(onClick = { viewModel.selectAll() }, modifier = Modifier.testTag("select_all_button")) {
                Icon(Icons.Default.SelectAll, contentDescription = "تحديد الكل", tint = TextPrimary)
            }
        }
    }
}

@Composable
private fun StitchTypesPanelContent(viewModel: EmbroideryViewModel) {
    val currentType by remember { androidx.compose.runtime.derivedStateOf { viewModel.currentStitchType.value } }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("نمط الغرزة الأساسي (Stitch Type Pattern):", color = TextSecondary, fontSize = 12.sp)

        StitchType.values().forEach { stType ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (currentType == stType) Color(0xFF2A2A38) else WilcomPanelBgLight)
                    .clickable { viewModel.setStitchTypeStyle(stType) }
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stType.titleAr,
                        color = if (currentType == stType) WilcomGold else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (currentType == stType) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = stType.titleEn,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                if (currentType == stType) {
                    Text("نشط ✓", color = WilcomGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ChenillePanelContent(viewModel: EmbroideryViewModel) {
    val direction by remember { androidx.compose.runtime.derivedStateOf { viewModel.chenilleLoopDirection.value } }
    val thickness by remember { androidx.compose.runtime.derivedStateOf { viewModel.chenilleThickness.value } }
    val chenilleDensity by remember { androidx.compose.runtime.derivedStateOf { viewModel.chenilleDensity.value } }
    val isChenilleMode by remember { androidx.compose.runtime.derivedStateOf { viewModel.isChenilleMode.value } }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Main Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = { viewModel.convertToChenille() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("convert_chenille_button")
            ) {
                Text("⚡ تحويل لشنيطة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.convertChenilleToRegular() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF282832),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("revert_chenille_button")
            ) {
                Text("🔄 عكس لعادي", fontSize = 12.sp)
            }
        }

        // Loop Direction
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "اتجاه الدوران (Loop Direction):",
                color = TextSecondary,
                fontSize = 11.5.sp
            )
            Text(
                text = "${direction.toInt()}°",
                color = WilcomCyan,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = direction,
            onValueChange = { viewModel.setChenilleDirection(it) },
            valueRange = 0f..360f,
            colors = SliderDefaults.colors(
                thumbColor = WilcomCyan,
                activeTrackColor = WilcomCyan,
                inactiveTrackColor = WilcomBorder
            ),
            modifier = Modifier.testTag("loop_dir_slider")
        )

        // Chenille Thickness
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "سمك الخيط البارز (Chenille Thickness):",
                color = TextSecondary,
                fontSize = 11.5.sp
            )
            Text(
                text = "${"%.1f".format(thickness)}mm",
                color = Color(0xFFFF9800),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = thickness,
            onValueChange = { viewModel.setChenilleThickness(it) },
            valueRange = 2.0f..9.0f,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFF9800),
                activeTrackColor = Color(0xFFFF9800),
                inactiveTrackColor = WilcomBorder
            ),
            modifier = Modifier.testTag("thickness_slider")
        )

        // Loop Density
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "كثافة الحلقات (Loop Density):",
                color = TextSecondary,
                fontSize = 11.5.sp
            )
            Text(
                text = "${chenilleDensity.toInt()}%",
                color = WilcomGold,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = chenilleDensity,
            onValueChange = { viewModel.setChenilleDensity(it) },
            valueRange = 50f..200f,
            colors = SliderDefaults.colors(
                thumbColor = WilcomGold,
                activeTrackColor = WilcomGold,
                inactiveTrackColor = WilcomBorder
            ),
            modifier = Modifier.testTag("chenille_density_slider")
        )

        // Conversion Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E28)),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isChenilleMode) Color(0xFFFF5722) else WilcomBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "📊 حالة النظام وتحويل الغرز:",
                    color = WilcomGold,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                if (isChenilleMode) {
                    Text(
                        text = "الحالة الحالية: تطريز شنيطة مفعل (Chenille Active)",
                        color = Color(0xFFFF5722),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "تم تطبيق خوارزمية الحلقات السميكة واتجاه الدوران بنجاح.",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                } else {
                    Text(
                        text = "الحالة الحالية: تطريز عادي (Regular Embroidery)",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "اضغط على زر \"تحويل لشنيطة\" لتطبيق خوارزمية الحلقات والسمك المجسم الفعلي فوراً.",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DensityPanelContent(viewModel: EmbroideryViewModel) {
    val density by remember { androidx.compose.runtime.derivedStateOf { viewModel.densityScale.value } }
    val length by remember { androidx.compose.runtime.derivedStateOf { viewModel.stitchLength.value } }
    val pullComp by remember { androidx.compose.runtime.derivedStateOf { viewModel.pullComp.value } }

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("كثافة الخيوط (Density Scale):", color = TextSecondary, fontSize = 11.5.sp)
            Text("${density.toInt()}%", color = WilcomGold, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = density,
            onValueChange = { viewModel.setDensityScale(it) },
            valueRange = 50f..200f,
            colors = SliderDefaults.colors(
                thumbColor = WilcomGold,
                activeTrackColor = WilcomGold,
                inactiveTrackColor = WilcomBorder
            ),
            modifier = Modifier.testTag("density_slider")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("طول الغرزة (Stitch Length):", color = TextSecondary, fontSize = 11.5.sp)
            Text("${"%.1f".format(length / 10f)} مم (${length.toInt()})", color = WilcomCyan, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = length,
            onValueChange = { viewModel.setStitchLength(it) },
            valueRange = 10f..50f,
            colors = SliderDefaults.colors(
                thumbColor = WilcomCyan,
                activeTrackColor = WilcomCyan,
                inactiveTrackColor = WilcomBorder
            ),
            modifier = Modifier.testTag("stitch_length_slider")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("تعويض السحب المستقل (Pull Comp):", color = TextSecondary, fontSize = 11.5.sp)
            Text("${pullComp.toInt()} مم", color = WilcomGreen, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = pullComp,
            onValueChange = { viewModel.setPullCompensation(it) },
            valueRange = 0f..10f,
            colors = SliderDefaults.colors(
                thumbColor = WilcomGreen,
                activeTrackColor = WilcomGreen,
                inactiveTrackColor = WilcomBorder
            ),
            modifier = Modifier.testTag("pull_comp_slider")
        )
    }
}

@Composable
private fun ColorSequencePanelContent(viewModel: EmbroideryViewModel, design: EmbroideryDesign) {
    var editingBlockIndex by remember { mutableStateOf<Int?>(null) }

    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(design.colorBlocks) { block ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(WilcomPanelBgLight)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { editingBlockIndex = block.index }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(block.colorHex))
                                .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = block.name,
                                color = TextPrimary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${block.threadCode} • ${"%,d".format(block.stitchCount)} غرزة",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.toggleColorVisibility(block.index) },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                if (block.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "إظهار/إخفاء",
                                tint = if (block.isVisible) WilcomCyan else TextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                if (block.index > 0) viewModel.moveColorBlock(block.index, block.index - 1)
                            },
                            enabled = block.index > 0,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = "تقديم", tint = TextPrimary, modifier = Modifier.size(15.dp))
                        }
                        IconButton(
                            onClick = {
                                if (block.index < design.colorBlocks.size - 1) {
                                    viewModel.moveColorBlock(block.index, block.index + 1)
                                }
                            },
                            enabled = block.index < design.colorBlocks.size - 1,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = "تأخير", tint = TextPrimary, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
    }

    editingBlockIndex?.let { bIdx ->
        val palette = listOf(
            0xFFFFB300L, 0xFF00BCD4L, 0xFFFF3333L, 0xFF4CAF50L,
            0xFF0D47A1L, 0xFFFF5722L, 0xFFFFFFFFL, 0xFF9C27B0L,
            0xFFFFD700L, 0xFF795548L, 0xFF607D8BL, 0xFF000000L
        )

        AlertDialog(
            onDismissRequest = { editingBlockIndex = null },
            title = { Text("اختر لون الخيط للكتلة #${bIdx + 1}", color = TextPrimary) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    palette.take(6).forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .border(2.dp, Color.White, CircleShape)
                                .clickable {
                                    viewModel.updateColorHex(bIdx, hex)
                                    editingBlockIndex = null
                                }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { editingBlockIndex = null }) {
                    Text("إغلاق", color = WilcomGold)
                }
            },
            containerColor = WilcomPanelBg
        )
    }
}

@Composable
private fun SimulatorPanelContent(viewModel: EmbroideryViewModel, design: EmbroideryDesign) {
    val isSimulating by remember { androidx.compose.runtime.derivedStateOf { viewModel.isSimulating.value } }
    val simIndex by remember { androidx.compose.runtime.derivedStateOf { viewModel.simulationIndex.value } }
    val speed by remember { androidx.compose.runtime.derivedStateOf { viewModel.simulationSpeed.value } }
    val total = design.stitches.size.coerceAtLeast(1)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الغرزة الحالية: ${"%,d".format(simIndex)} من إجمالي ${"%,d".format(total)}",
                color = WilcomGold,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${(simIndex * 100 / total)}%",
                color = WilcomCyan,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = simIndex.toFloat(),
            onValueChange = { viewModel.seekSimulation(it.toInt()) },
            valueRange = 0f..total.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = WilcomGold,
                activeTrackColor = WilcomGold,
                inactiveTrackColor = WilcomBorder
            ),
            modifier = Modifier.testTag("simulator_scrubber")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.stepSimulation(-50) }) {
                Icon(Icons.Default.FastRewind, contentDescription = "ترجيع 50 غرزة", tint = TextPrimary)
            }

            Button(
                onClick = { viewModel.toggleSimulation() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSimulating) WilcomCyan else WilcomGold,
                    contentColor = Color.Black
                ),
                modifier = Modifier.testTag("simulator_play_pause_button")
            ) {
                Icon(if (isSimulating) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isSimulating) "إيقاف مؤقت" else "تشغيل المحاكي", fontWeight = FontWeight.Bold)
            }

            IconButton(onClick = { viewModel.stepSimulation(50) }) {
                Icon(Icons.Default.FastForward, contentDescription = "تقديم 50 غرزة", tint = TextPrimary)
            }

            listOf(1, 2, 5, 10).forEach { s ->
                FilterChip(
                    selected = speed == s,
                    onClick = { viewModel.setSimulationSpeed(s) },
                    label = { Text("${s}x") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WilcomCyan,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }
    }
}

/**
 * Auto-Digitizing Selection Modal
 */
@Composable
fun AutoDigitizeDialog(
    onPickImage: () -> Unit,
    onPickPreset: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WilcomGreen)
                Spacer(modifier = Modifier.width(6.dp))
                Text("محرك الرقمنة التلقائية (Auto-Digitizing)", color = WilcomGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "يقوم المحرك بتحليل الصور والرسومات وفصل الألوان وتحويلها فوراً إلى مسارات غرز تطريز حقيقية (Tatami, Satin, Run) مع تعويض السحب:",
                    color = TextPrimary,
                    fontSize = 12.sp
                )

                Button(
                    onClick = onPickImage,
                    colors = ButtonDefaults.buttonColors(containerColor = WilcomGreen, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📷 اختيار صورة أو شعار من الهاتف للرقمنة", fontWeight = FontWeight.Bold)
                }

                Divider(color = WilcomBorder)

                Text("أو اختر نموذج شعار جاهز للرقمنة الفورية:", color = TextSecondary, fontSize = 11.5.sp)

                listOf(
                    "شعار_فيكتور_مرقمن_تلقائياً",
                    "شارة_شنيطة_جامعية_مرقمنة",
                    "شعار_صقر_ملكي_مرقمن"
                ).forEach { preset ->
                    OutlinedButton(
                        onClick = { onPickPreset(preset) },
                        border = androidx.compose.foundation.BorderStroke(1.dp, WilcomBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(preset.replace("_", " "), color = WilcomGold, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        },
        containerColor = WilcomPanelBg
    )
}

/**
 * Intelligent Chunks Loader Dialog
 */
@Composable
fun ChunkLoaderOverlay(
    progress: Float,
    message: String,
    fileName: String = "Design.dst"
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        title = {
            Text(
                text = "جاري فك تشييد وقراءة ملف التطريز الضخم...",
                color = WilcomGold,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "($fileName)",
                    color = TextSecondary,
                    fontSize = 11.5.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF22222A))
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp),
                        color = WilcomGold,
                        trackColor = Color(0xFF22222A)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Text(
                    text = message,
                    color = TextPrimary,
                    fontSize = 11.5.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "نظام الحزم المتطورة - حماية المعالج من التجمد والتهنيج",
                    color = WilcomCyan,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        containerColor = WilcomPanelBg
    )
}

/**
 * Sample Library Selector Dialog
 */
@Composable
fun SamplePickerModal(
    onSelectSample: (EmbroideryDesign) -> Unit,
    onDismiss: () -> Unit
) {
    val samples = remember { EmbroiderySampleLibrary.getSampleDesigns() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("مكتبة تصاميم ويلكم الجاهزة", color = WilcomGold, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(samples) { sample ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = WilcomPanelBgLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WilcomBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSample(sample) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = sample.name.replace("_", " "),
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                                Text(
                                    text = "${"%,d".format(sample.totalStitches)} غرزة",
                                    color = WilcomCyan,
                                    fontSize = 11.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "الأبعاد: ${"%.1f".format(sample.widthMm)} × ${"%.1f".format(sample.heightMm)} مم • ${sample.colorBlocks.size} ألوان",
                                color = TextSecondary,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        },
        containerColor = WilcomPanelBg
    )
}

/**
 * Export DST / EMB Dialog
 */
@Composable
fun ExportDstDialog(
    design: EmbroideryDesign,
    onExport: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var fileName by remember { mutableStateOf(design.name.replace(" ", "_") + ".dst") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("حفظ وتصدير ملف DST / EMB", color = WilcomCyan, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "سيتم توليد ملف Tajima DST و EMB متوافق تماماً مع كافة ماكينات التطريز الصناعية والمنزلية.",
                    color = TextPrimary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("اسم الملف (.dst / .emb)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(WilcomPanelBgLight)
                        .padding(8.dp)
                ) {
                    Text("• عدد الغرز المشفرة: ${"%,d".format(design.totalStitches)} غرزة", color = TextSecondary, fontSize = 11.sp)
                    Text("• عدد كتل الألوان: ${design.colorBlocks.size}", color = TextSecondary, fontSize = 11.sp)
                    Text("• تعويض السحب المدمج: ${design.pullCompensation} مم", color = TextSecondary, fontSize = 11.sp)
                    Text("• نظام التشفير: Tajima Ternary 0.1mm Delta Matrix", color = TextSecondary, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onExport(fileName) },
                colors = ButtonDefaults.buttonColors(containerColor = WilcomCyan, contentColor = Color.Black)
            ) {
                Text("حفظ وتصدير DST / EMB", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        },
        containerColor = WilcomPanelBg
    )
}

/**
 * Production Sheet Dialog
 */
@Composable
fun ProductionSheetDialog(
    design: EmbroideryDesign,
    rpm: Int,
    onDismiss: () -> Unit
) {
    val estMinutes = ceil((design.totalStitches.toFloat()) / rpm.toFloat()).toInt()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("📄 كرت تشغيل الماكينة (Production Sheet)", color = WilcomGold, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text("اسم التصميم: ${design.name}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                Text("إجمالي الغرز: ${"%,d".format(design.totalStitches)} غرزة", color = WilcomCyan, fontSize = 12.5.sp)
                Text("الوقت المقدر: $estMinutes دقيقة (بسرعة $rpm RPM)", color = TextPrimary, fontSize = 12.5.sp)
                Text("الأبعاد الصافية: ${"%.1f".format(design.widthMm)} × ${"%.1f".format(design.heightMm)} مم", color = TextSecondary, fontSize = 11.5.sp)
                Text("تعويض السحب (Pull Comp): ${design.pullCompensation} مم", color = WilcomGreen, fontSize = 11.5.sp)
                Text("عدد الغرز القافزة (Jumps): ${design.jumpCount}", color = TextSecondary, fontSize = 11.5.sp)

                Divider(color = WilcomBorder, modifier = Modifier.padding(vertical = 3.dp))

                Text("تسلسل الألوان وبكرات الخيط:", color = WilcomGold, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(design.colorBlocks) { cb ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(11.dp)
                                        .clip(CircleShape)
                                        .background(Color(cb.colorHex))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("#${cb.index + 1} ${cb.name}", color = TextPrimary, fontSize = 10.5.sp)
                            }
                            Text("${"%,d".format(cb.stitchCount)} غرزة", color = TextSecondary, fontSize = 10.5.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = WilcomGold, contentColor = Color.Black)
            ) {
                Text("تم")
            }
        },
        containerColor = WilcomPanelBg
    )
}

/**
 * Quick Shortcut Sidebar (الأيقونات الجانبية المختصرة)
 */
@Composable
fun WilcomShortcutsBar(
    onOpenFile: () -> Unit,
    onToggle3D: () -> Unit,
    onConvertChenille: () -> Unit,
    onCutSelected: () -> Unit,
    onToggleSidebar: () -> Unit,
    onExportDst: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF141418),
        tonalElevation = 6.dp,
        modifier = modifier
            .width(55.dp)
            .border(1.dp, WilcomBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShortcutIconItem("📁", "فتح ملف جديد", onOpenFile)
            ShortcutIconItem("🌐", "محاكاة 3D", onToggle3D)
            ShortcutIconItem("🧶", "تحويل شنيطة", onConvertChenille)
            ShortcutIconItem("✂️", "قص المحدد", onCutSelected)
            ShortcutIconItem("👁️", "إخفاء/إظهار القوائم", onToggleSidebar)
            ShortcutIconItem("💾", "حفظ بصيغة DST", onExportDst)
        }
    }
}

@Composable
private fun ShortcutIconItem(
    iconText: String,
    tooltip: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF22222A))
            .border(1.dp, WilcomBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = iconText, fontSize = 18.sp)
    }
}

