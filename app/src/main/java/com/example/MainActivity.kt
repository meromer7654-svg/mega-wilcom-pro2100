package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AutoDigitizeDialog
import com.example.ui.components.ChunkLoaderOverlay
import com.example.ui.components.EmbroideryCanvas
import com.example.ui.components.ExportDstDialog
import com.example.ui.components.ProductionSheetDialog
import com.example.ui.components.SamplePickerModal
import com.example.ui.components.WilcomBottomBar
import com.example.ui.components.WilcomBottomFooter
import com.example.ui.components.WilcomBottomToolbar
import com.example.ui.components.WilcomCompactHeader
import com.example.ui.components.WilcomShortcutsBar
import com.example.ui.components.WilcomToolDock
import com.example.ui.components.WilcomTopBar
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WilcomDarkBg
import com.example.viewmodel.EmbroideryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: EmbroideryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                EmbroideryApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun EmbroideryApp(viewModel: EmbroideryViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val design by viewModel.currentDesign.collectAsStateWithLifecycle()
    val is3DView by viewModel.is3DView.collectAsStateWithLifecycle()
    val isChenilleMode by viewModel.isChenilleMode.collectAsStateWithLifecycle()
    val chenilleAngle by viewModel.chenilleLoopDirection.collectAsStateWithLifecycle()
    val chenilleThickness by viewModel.chenilleThickness.collectAsStateWithLifecycle()
    val chenilleDensity by viewModel.chenilleDensity.collectAsStateWithLifecycle()
    val densityScale by viewModel.densityScale.collectAsStateWithLifecycle()
    val pullComp by viewModel.pullComp.collectAsStateWithLifecycle()
    val hoop by viewModel.selectedHoop.collectAsStateWithLifecycle()
    val selectionMode by viewModel.selectionMode.collectAsStateWithLifecycle()
    val selectedIndices by viewModel.selectedStitchIndices.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val simulationIndex by viewModel.simulationIndex.collectAsStateWithLifecycle()
    val isSimulating by viewModel.isSimulating.collectAsStateWithLifecycle()
    val rpm by viewModel.machineRpm.collectAsStateWithLifecycle()

    val isFooterVisible by viewModel.isFooterVisible.collectAsStateWithLifecycle()
    val isSidebarVisible by viewModel.isSidebarVisible.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val loadingProgress by viewModel.loadingProgress.collectAsStateWithLifecycle()
    val loadingMessage by viewModel.loadingMessage.collectAsStateWithLifecycle()

    val showSamplePicker by viewModel.showSamplePicker.collectAsStateWithLifecycle()
    val showExportDialog by viewModel.showExportDialog.collectAsStateWithLifecycle()
    val showProductionSheet by viewModel.showProductionSheet.collectAsStateWithLifecycle()
    val showAutoDigitizeDialog by viewModel.showAutoDigitizeDialog.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    // File picker launcher for DST / PES / JEF / EXP / EMB files
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.loadFileFromUri(context, uri)
        }
    }

    // Photo/Image picker for Auto-Digitizing
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.autoDigitize(context, imageUri = uri)
        }
    }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(WilcomDarkBg)
            .navigationBarsPadding(),
        topBar = {
            WilcomCompactHeader(
                totalStitches = design.totalStitches,
                onOpenSamples = { viewModel.setSamplePicker(true) }
            )
        },
        bottomBar = {
            WilcomBottomFooter()
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = WilcomDarkBg
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isTabletLayout = maxWidth >= 760.dp

            if (isTabletLayout) {
                // Tablet layout: Canvas taking top/center, sidebar if toggled, bottom toolbar fixed at bottom
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            EmbroideryCanvas(
                                design = design,
                                is3DView = is3DView,
                                isChenilleMode = isChenilleMode,
                                chenilleAngle = chenilleAngle,
                                chenilleThickness = chenilleThickness,
                                densityScale = densityScale,
                                pullCompensation = pullComp,
                                hoop = hoop,
                                selectionMode = selectionMode,
                                selectedIndices = selectedIndices,
                                simulationIndex = simulationIndex,
                                isSimulating = isSimulating,
                                onSelectRect = { minX, minY, maxX, maxY ->
                                    viewModel.selectByRect(minX, minY, maxX, maxY)
                                }
                            )
                        }

                        if (isSidebarVisible) {
                            WilcomToolDock(
                                activeTab = activeTab,
                                onTabSelected = { viewModel.setActiveTab(it) },
                                viewModel = viewModel,
                                design = design,
                                modifier = Modifier
                                    .width(320.dp)
                                    .fillMaxHeight()
                            )
                        }
                    }

                    WilcomBottomToolbar(
                        is3DActive = is3DView,
                        isChenilleActive = isChenilleMode,
                        onOpenFilePicker = { filePickerLauncher.launch("*/*") },
                        onExportDst = { viewModel.setExportDialog(true) },
                        onConvertChenille = { viewModel.convertToChenille() },
                        onToggle3D = { viewModel.toggle3DView() },
                        onReset = { viewModel.resetViewAndScales() },
                        isToolDockExpanded = isSidebarVisible,
                        onToggleToolDock = { viewModel.toggleSidebar() }
                    )
                }
            } else {
                // Mobile layout: Canvas at the very top filling available space, collapsible tool dock, and bottom toolbar
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        EmbroideryCanvas(
                            design = design,
                            is3DView = is3DView,
                            isChenilleMode = isChenilleMode,
                            chenilleAngle = chenilleAngle,
                            chenilleThickness = chenilleThickness,
                            densityScale = densityScale,
                            pullCompensation = pullComp,
                            hoop = hoop,
                            selectionMode = selectionMode,
                            selectedIndices = selectedIndices,
                            simulationIndex = simulationIndex,
                            isSimulating = isSimulating,
                            onSelectRect = { minX, minY, maxX, maxY ->
                                viewModel.selectByRect(minX, minY, maxX, maxY)
                            }
                        )
                    }

                    AnimatedVisibility(
                        visible = isSidebarVisible,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        WilcomToolDock(
                            activeTab = activeTab,
                            onTabSelected = { viewModel.setActiveTab(it) },
                            viewModel = viewModel,
                            design = design
                        )
                    }

                    WilcomBottomToolbar(
                        is3DActive = is3DView,
                        isChenilleActive = isChenilleMode,
                        onOpenFilePicker = { filePickerLauncher.launch("*/*") },
                        onExportDst = { viewModel.setExportDialog(true) },
                        onConvertChenille = { viewModel.convertToChenille() },
                        onToggle3D = { viewModel.toggle3DView() },
                        onReset = { viewModel.resetViewAndScales() },
                        isToolDockExpanded = isSidebarVisible,
                        onToggleToolDock = { viewModel.toggleSidebar() }
                    )
                }
            }
        }
    }

    // Overlays & Dialogs
    if (isLoading) {
        ChunkLoaderOverlay(
            progress = loadingProgress,
            message = loadingMessage,
            fileName = design.name
        )
    }

    if (showAutoDigitizeDialog) {
        AutoDigitizeDialog(
            onPickImage = {
                imagePickerLauncher.launch("image/*")
                viewModel.setAutoDigitizeDialog(false)
            },
            onPickPreset = { preset ->
                viewModel.autoDigitize(context, presetName = preset)
            },
            onDismiss = { viewModel.setAutoDigitizeDialog(false) }
        )
    }

    if (showSamplePicker) {
        SamplePickerModal(
            onSelectSample = { sample ->
                viewModel.loadDesign(sample)
                viewModel.setSamplePicker(false)
            },
            onDismiss = { viewModel.setSamplePicker(false) }
        )
    }

    if (showExportDialog) {
        ExportDstDialog(
            design = design,
            onExport = { fileName ->
                viewModel.exportDstFile(context, fileName)
            },
            onDismiss = { viewModel.setExportDialog(false) }
        )
    }

    if (showProductionSheet) {
        ProductionSheetDialog(
            design = design,
            rpm = rpm,
            onDismiss = { viewModel.setProductionSheet(false) }
        )
    }
}
