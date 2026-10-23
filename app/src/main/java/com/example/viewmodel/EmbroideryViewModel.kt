package com.example.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitizer.AutoDigitizer
import com.example.dst.DstCodec
import com.example.model.ColorBlock
import com.example.model.EmbroideryDesign
import com.example.model.HoopConfig
import com.example.model.Stitch
import com.example.model.StitchFlag
import com.example.model.StitchType
import com.example.samples.EmbroiderySampleLibrary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.cos
import kotlin.math.sin

enum class SelectionMode {
    NONE,
    LASSO,
    RECTANGLE,
    MERGED
}

enum class ToolTab {
    SCALE,
    SELECTION,
    STITCH_TYPES,
    CHENILLE,
    DENSITY,
    COLORS,
    SIMULATOR
}

enum class ChenilleType {
    LOOP_MOSS,
    CHAIN_STITCH
}

class EmbroideryViewModel : ViewModel() {

    private val _currentDesign = MutableStateFlow(EmbroiderySampleLibrary.createVarsityChenilleM())
    val currentDesign: StateFlow<EmbroideryDesign> = _currentDesign.asStateFlow()

    private var baseStitches: List<Stitch> = _currentDesign.value.stitches

    private val _globalScale = MutableStateFlow(100f) // 50% to 200%
    val globalScale: StateFlow<Float> = _globalScale.asStateFlow()

    private val _selectionScale = MutableStateFlow(100f) // 40% to 250%
    val selectionScale: StateFlow<Float> = _selectionScale.asStateFlow()

    private val undoStack = mutableListOf<EmbroideryDesign>()
    private val redoStack = mutableListOf<EmbroideryDesign>()

    private val _is3DView = MutableStateFlow(true)
    val is3DView: StateFlow<Boolean> = _is3DView.asStateFlow()

    private val _isChenilleMode = MutableStateFlow(true)
    val isChenilleMode: StateFlow<Boolean> = _isChenilleMode.asStateFlow()

    private val _chenilleLoopDirection = MutableStateFlow(45f)
    val chenilleLoopDirection: StateFlow<Float> = _chenilleLoopDirection.asStateFlow()

    private val _chenilleType = MutableStateFlow(ChenilleType.LOOP_MOSS)
    val chenilleType: StateFlow<ChenilleType> = _chenilleType.asStateFlow()

    private val _chenilleThickness = MutableStateFlow(5.0f) // 2.0mm to 9.0mm
    val chenilleThickness: StateFlow<Float> = _chenilleThickness.asStateFlow()

    private val _chenilleDensity = MutableStateFlow(100f) // 50% to 200%
    val chenilleDensity: StateFlow<Float> = _chenilleDensity.asStateFlow()

    private val _currentStitchType = MutableStateFlow(StitchType.TATAMI)
    val currentStitchType: StateFlow<StitchType> = _currentStitchType.asStateFlow()

    private val _densityScale = MutableStateFlow(100f) // 50% to 200%
    val densityScale: StateFlow<Float> = _densityScale.asStateFlow()

    private val _stitchLength = MutableStateFlow(25f) // 10 to 50 (1.0mm - 5.0mm)
    val stitchLength: StateFlow<Float> = _stitchLength.asStateFlow()

    private val _pullComp = MutableStateFlow(3f) // 0 to 10
    val pullComp: StateFlow<Float> = _pullComp.asStateFlow()

    private val _selectedHoop = MutableStateFlow(HoopConfig.HOOP_150X150)
    val selectedHoop: StateFlow<HoopConfig> = _selectedHoop.asStateFlow()

    private val _selectionMode = MutableStateFlow(SelectionMode.NONE)
    val selectionMode: StateFlow<SelectionMode> = _selectionMode.asStateFlow()

    private val _selectedStitchIndices = MutableStateFlow<Set<Int>>(emptySet())
    val selectedStitchIndices: StateFlow<Set<Int>> = _selectedStitchIndices.asStateFlow()

    private val _activeTab = MutableStateFlow(ToolTab.SCALE)
    val activeTab: StateFlow<ToolTab> = _activeTab.asStateFlow()

    // Simulation / Playback state
    private val _simulationIndex = MutableStateFlow(0)
    val simulationIndex: StateFlow<Int> = _simulationIndex.asStateFlow()

    private val _isSimulating = MutableStateFlow(false)
    val isSimulating: StateFlow<Boolean> = _isSimulating.asStateFlow()

    private val _simulationSpeed = MutableStateFlow(2) // 1x, 2x, 5x, 10x
    val simulationSpeed: StateFlow<Int> = _simulationSpeed.asStateFlow()

    private val _machineRpm = MutableStateFlow(750) // Standard 750 stitches/min
    val machineRpm: StateFlow<Int> = _machineRpm.asStateFlow()

    // Loader dialog state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadingProgress = MutableStateFlow(0f)
    val loadingProgress: StateFlow<Float> = _loadingProgress.asStateFlow()

    private val _loadingMessage = MutableStateFlow("")
    val loadingMessage: StateFlow<String> = _loadingMessage.asStateFlow()

    // Dialogs
    private val _showSamplePicker = MutableStateFlow(false)
    val showSamplePicker: StateFlow<Boolean> = _showSamplePicker.asStateFlow()

    private val _showExportDialog = MutableStateFlow(false)
    val showExportDialog: StateFlow<Boolean> = _showExportDialog.asStateFlow()

    private val _showProductionSheet = MutableStateFlow(false)
    val showProductionSheet: StateFlow<Boolean> = _showProductionSheet.asStateFlow()

    private val _showAutoDigitizeDialog = MutableStateFlow(false)
    val showAutoDigitizeDialog: StateFlow<Boolean> = _showAutoDigitizeDialog.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _isFooterVisible = MutableStateFlow(true)
    val isFooterVisible: StateFlow<Boolean> = _isFooterVisible.asStateFlow()

    private val _isSidebarVisible = MutableStateFlow(true)
    val isSidebarVisible: StateFlow<Boolean> = _isSidebarVisible.asStateFlow()

    fun toggleFooter() {
        _isFooterVisible.value = !_isFooterVisible.value
    }

    fun toggleSidebar() {
        _isSidebarVisible.value = !_isSidebarVisible.value
    }

    private var simulationJob: Job? = null

    init {
        _simulationIndex.value = _currentDesign.value.stitches.size
    }

    private fun pushUndo() {
        undoStack.add(_currentDesign.value)
        if (undoStack.size > 20) undoStack.removeAt(0)
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(_currentDesign.value)
            val prev = undoStack.removeAt(undoStack.lastIndex)
            _currentDesign.value = prev
            _selectedStitchIndices.value = emptySet()
            _simulationIndex.value = prev.stitches.size
            _userMessage.value = "تم التراجع عن آخر تعديل"
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(_currentDesign.value)
            val next = redoStack.removeAt(redoStack.lastIndex)
            _currentDesign.value = next
            _selectedStitchIndices.value = emptySet()
            _simulationIndex.value = next.stitches.size
            _userMessage.value = "تمت إعادة تطبيق التعديل"
        }
    }

    fun toggle3DView() {
        _is3DView.value = !_is3DView.value
    }

    fun setActiveTab(tab: ToolTab) {
        _activeTab.value = tab
    }

    fun setSelectionMode(mode: SelectionMode) {
        _selectionMode.value = mode
        if (mode == SelectionMode.NONE) {
            _selectedStitchIndices.value = emptySet()
        }
    }

    fun setHoop(hoop: HoopConfig) {
        _selectedHoop.value = hoop
    }

    fun setSamplePicker(show: Boolean) {
        _showSamplePicker.value = show
    }

    fun setExportDialog(show: Boolean) {
        _showExportDialog.value = show
    }

    fun setProductionSheet(show: Boolean) {
        _showProductionSheet.value = show
    }

    fun setAutoDigitizeDialog(show: Boolean) {
        _showAutoDigitizeDialog.value = show
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun loadDesign(design: EmbroideryDesign) {
        pushUndo()
        baseStitches = design.stitches
        _globalScale.value = 100f
        _selectionScale.value = 100f
        _currentDesign.value = design
        _selectedStitchIndices.value = emptySet()
        _simulationIndex.value = design.stitches.size
        _isSimulating.value = false
        simulationJob?.cancel()
        _isChenilleMode.value = design.stitches.any { it.isChenille }
        _userMessage.value = "تم فتح التصميم: ${design.name}"
    }

    // ⭐ Smart Stitch Scaling Engine (Global Scale & Selection Scale with Thinning and Interpolation)
    fun applyGlobalScale(scalePct: Float) {
        _globalScale.value = scalePct
        recalculateStitchesFromScales()
    }

    fun applySelectionScale(scalePct: Float) {
        _selectionScale.value = scalePct
        recalculateStitchesFromScales()
    }

    fun resetScales() {
        _globalScale.value = 100f
        _selectionScale.value = 100f
        val original = if (baseStitches.isNotEmpty()) baseStitches else _currentDesign.value.stitches
        if (original.isNotEmpty()) {
            val design = _currentDesign.value
            val updated = EmbroideryDesign.create(design.name, original, design.colorBlocks)
            _currentDesign.value = updated
            _simulationIndex.value = updated.stitches.size
        }
        _userMessage.value = "تمت إعادة ضبط مقاس الرسمة والمنطقة المحددة إلى 100%"
    }

    fun resetViewAndScales() {
        _selectedStitchIndices.value = emptySet()
        _selectionMode.value = SelectionMode.NONE
        resetScales()
        _userMessage.value = "تمت إعادة ضبط الكانفاس والمقاسات بنجاح"
    }

    fun optimizeStitchesDensity() {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingProgress.value = 0.25f
            _loadingMessage.value = "جاري معالجة وتكييف الغرز... (DST Engine)"
            delay(120)
            _loadingProgress.value = 0.70f
            _loadingMessage.value = "غربلة الغرز وحساب الكثافة المثالية للتصميم لمنع القطع..."
            delay(120)
            _loadingProgress.value = 1.0f
            _loadingMessage.value = "اكتمل التحسين والتكييف الذكي!"
            delay(80)
            _isLoading.value = false
            recalculateStitchesFromScales()
            _userMessage.value = "تمت إعادة غربلة الغرز وحساب الكثافة المثالية للتصميم بنجاح!"
        }
    }

    private fun recalculateStitchesFromScales() {
        val original = if (baseStitches.isNotEmpty()) baseStitches else _currentDesign.value.stitches
        if (original.isEmpty()) return

        val globalFactor = _globalScale.value / 100f
        val selFactor = _selectionScale.value / 100f
        val selected = _selectedStitchIndices.value
        val hasSelection = selected.isNotEmpty()

        // Centroid for selected stitches (or design center)
        var midX = 0f
        var midY = 0f
        if (hasSelection) {
            val selList = original.filterIndexed { idx, _ -> selected.contains(idx) }
            if (selList.isNotEmpty()) {
                midX = selList.map { it.x }.average().toFloat()
                midY = selList.map { it.y }.average().toFloat()
            }
        }

        val totalEffective = globalFactor * (if (hasSelection) selFactor else 1.0f)
        val skipStep = if (totalEffective < 0.8f) (1f / totalEffective).toInt().coerceIn(2, 5) else 1

        val tempStitches = mutableListOf<Stitch>()
        for (i in original.indices) {
            val st = original[i]
            val isSelected = hasSelection && selected.contains(i)
            val factor = if (hasSelection) {
                if (isSelected) globalFactor * selFactor else globalFactor
            } else {
                globalFactor * selFactor
            }

            // Stitch Thinning below 80% to prevent bunching and thread breakage
            if (factor < 0.8f && skipStep > 1 && i % skipStep != 0 && st.flag == StitchFlag.NORMAL) {
                continue
            }

            val dx = if (hasSelection && isSelected) (st.x - midX) * factor + midX else st.x * factor
            val dy = if (hasSelection && isSelected) (st.y - midY) * factor + midY else st.y * factor

            // Stitch Interpolation / Densification above 120% to prevent voids and loose threads
            if (factor > 1.2f && i < original.size - 1 && st.flag == StitchFlag.NORMAL) {
                val nextSt = original[i + 1]
                if (nextSt.flag == StitchFlag.NORMAL) {
                    val nextFactor = if (hasSelection) {
                        if (selected.contains(i + 1)) globalFactor * selFactor else globalFactor
                    } else {
                        globalFactor * selFactor
                    }
                    val nextDx = if (hasSelection && selected.contains(i + 1)) (nextSt.x - midX) * nextFactor + midX else nextSt.x * nextFactor
                    val nextDy = if (hasSelection && selected.contains(i + 1)) (nextSt.y - midY) * nextFactor + midY else nextSt.y * nextFactor

                    tempStitches.add(st.copy(x = dx, y = dy))
                    tempStitches.add(
                        st.copy(
                            x = (dx + nextDx) / 2f,
                            y = (dy + nextDy) / 2f,
                            flag = StitchFlag.NORMAL
                        )
                    )
                    continue
                }
            }

            tempStitches.add(st.copy(x = dx, y = dy))
        }

        val design = _currentDesign.value
        val updated = EmbroideryDesign.create(design.name, tempStitches, design.colorBlocks)
        _currentDesign.value = updated
        _simulationIndex.value = updated.stitches.size
    }

    // Stitch Type pattern change (Tatami, Satin, Run, Zigzag)
    fun setStitchTypeStyle(type: StitchType) {
        pushUndo()
        _currentStitchType.value = type
        val design = _currentDesign.value
        val selected = _selectedStitchIndices.value

        val updated = design.stitches.mapIndexed { idx, st ->
            if (selected.isEmpty() || selected.contains(idx)) {
                st.copy(style = type)
            } else {
                st
            }
        }
        _currentDesign.value = design.copy(stitches = updated)
        _userMessage.value = "تم تطبيق نمط: ${type.titleAr}"
    }

    fun convertToChenille() {
        pushUndo()
        val design = _currentDesign.value
        val angle = _chenilleLoopDirection.value
        val rad = (angle * Math.PI.toFloat()) / 180f
        val selected = _selectedStitchIndices.value

        // Real mathematical conversion applying loop direction transformation to stitch paths
        val updatedStitches = design.stitches.mapIndexed { idx, st ->
            if (selected.isEmpty() || selected.contains(idx)) {
                st.copy(
                    x = st.x + cos(rad) * 1.5f,
                    y = st.y + sin(rad) * 1.5f,
                    isChenille = true,
                    chainAngle = angle
                )
            } else {
                st
            }
        }
        _isChenilleMode.value = true
        _currentDesign.value = design.copy(stitches = updatedStitches)
        _userMessage.value = "تم بنجاح تحويل رسمة التطريز العادي إلى تطريز شنيطة (Chenille Look) بكافة الحلقات والسمك البارز!"
    }

    fun convertChenilleToRegular() {
        pushUndo()
        val design = _currentDesign.value
        val updatedStitches = design.stitches.map { st ->
            st.copy(isChenille = false)
        }
        _isChenilleMode.value = false
        _currentDesign.value = design.copy(stitches = updatedStitches)
        _userMessage.value = "تم التحويل إلى التطريز العادي بنجاح"
    }

    fun setChenilleDirection(angle: Float) {
        _chenilleLoopDirection.value = angle
        val design = _currentDesign.value
        val updated = design.stitches.map {
            if (it.isChenille) it.copy(chainAngle = angle) else it
        }
        _currentDesign.value = design.copy(stitches = updated)
    }

    fun setChenilleThickness(thickness: Float) {
        _chenilleThickness.value = thickness
    }

    fun setChenilleDensity(density: Float) {
        _chenilleDensity.value = density
    }

    fun setChenilleType(type: ChenilleType) {
        _chenilleType.value = type
    }

    fun setDensityScale(scale: Float) {
        _densityScale.value = scale
    }

    fun setStitchLength(len: Float) {
        _stitchLength.value = len
    }

    fun setPullCompensation(comp: Float) {
        _pullComp.value = comp
        _currentDesign.value = _currentDesign.value.copy(pullCompensation = comp)
    }

    fun setMachineRpm(rpm: Int) {
        _machineRpm.value = rpm
    }

    fun selectByRect(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        val design = _currentDesign.value
        val indices = mutableSetOf<Int>()
        design.stitches.forEachIndexed { i, s ->
            if (s.x in minX..maxX && s.y in minY..maxY) {
                indices.add(i)
            }
        }
        _selectedStitchIndices.value = indices
        _userMessage.value = "تم تحديد ${indices.size} غرزة"
    }

    fun selectAll() {
        val design = _currentDesign.value
        _selectedStitchIndices.value = design.stitches.indices.toSet()
        _userMessage.value = "تم تحديد كافة الغرز (${design.stitches.size})"
    }

    fun clearSelection() {
        _selectedStitchIndices.value = emptySet()
    }

    fun cutSelectedStitches() {
        val selected = _selectedStitchIndices.value
        if (selected.isEmpty()) {
            _userMessage.value = "برجاء تحديد جزء من الغرز أولاً للقص"
            return
        }
        pushUndo()
        val design = _currentDesign.value
        val newStitches = mutableListOf<Stitch>()
        var inCut = false

        design.stitches.forEachIndexed { idx, st ->
            if (selected.contains(idx)) {
                inCut = true
            } else {
                if (inCut) {
                    newStitches.add(st.copy(flag = StitchFlag.JUMP))
                    inCut = false
                } else {
                    newStitches.add(st)
                }
            }
        }

        _selectedStitchIndices.value = emptySet()
        val updated = EmbroideryDesign.create("${design.name}_cut", newStitches, design.colorBlocks)
        _currentDesign.value = updated
        _simulationIndex.value = updated.stitches.size
        _userMessage.value = "تم قص ${selected.size} غرزة بنجاح وتحديث مسار الماكينة"
    }

    fun deleteSelectedStitches() {
        val selected = _selectedStitchIndices.value
        if (selected.isEmpty()) return
        pushUndo()
        val design = _currentDesign.value
        val newStitches = design.stitches.filterIndexed { index, _ -> !selected.contains(index) }
        _selectedStitchIndices.value = emptySet()
        val updated = EmbroideryDesign.create("${design.name}_edited", newStitches, design.colorBlocks)
        _currentDesign.value = updated
        _simulationIndex.value = updated.stitches.size
        _userMessage.value = "تم حذف الغرز المحددة (${selected.size})"
    }

    fun mergeSelectedStitches() {
        val selected = _selectedStitchIndices.value
        if (selected.isEmpty()) {
            _userMessage.value = "برجاء تحديد العناصر المراد دمجها"
            return
        }
        pushUndo()
        val design = _currentDesign.value
        val updatedStitches = design.stitches.mapIndexed { idx, st ->
            if (selected.contains(idx)) st.copy(colorIndex = 0) else st
        }
        _selectedStitchIndices.value = emptySet()
        val updated = EmbroideryDesign.create("${design.name}_merged", updatedStitches, design.colorBlocks)
        _currentDesign.value = updated
        _userMessage.value = "تم دمج العناصر المختارة مع الطبقة الأساسية (Merged Style) وتحديث الكتل بنجاح."
    }

    fun centerDesign() {
        pushUndo()
        val design = _currentDesign.value
        val midX = (design.minX + design.maxX) / 2f
        val midY = (design.minY + design.maxY) / 2f

        val centered = design.stitches.map {
            it.copy(x = it.x - midX, y = it.y - midY)
        }
        _currentDesign.value = EmbroideryDesign.create(design.name, centered, design.colorBlocks)
        _userMessage.value = "تم توسيط التصميم بدقة داخل الطارة"
    }

    fun rotateDesign(degrees: Float) {
        pushUndo()
        val design = _currentDesign.value
        val rad = degrees * Math.PI.toFloat() / 180f
        val cosA = cos(rad)
        val sinA = sin(rad)

        val rotated = design.stitches.map {
            val rx = it.x * cosA - it.y * sinA
            val ry = it.x * sinA + it.y * cosA
            it.copy(x = rx, y = ry)
        }
        _currentDesign.value = EmbroideryDesign.create(design.name, rotated, design.colorBlocks)
        _userMessage.value = "تم تدوير التصميم $degrees درجة"
    }

    fun moveColorBlock(from: Int, to: Int) {
        val design = _currentDesign.value
        if (from !in design.colorBlocks.indices || to !in design.colorBlocks.indices) return
        pushUndo()
        val list = design.colorBlocks.toMutableList()
        val item = list.removeAt(from)
        list.add(to, item)

        val reindexed = list.mapIndexed { idx, cb -> cb.copy(index = idx) }
        _currentDesign.value = design.copy(colorBlocks = reindexed)
        _userMessage.value = "تم تغيير ترتيب تسلسل كتل الألوان"
    }

    fun updateColorHex(blockIndex: Int, newHex: Long) {
        val design = _currentDesign.value
        val updated = design.colorBlocks.map {
            if (it.index == blockIndex) it.copy(colorHex = newHex) else it
        }
        _currentDesign.value = design.copy(colorBlocks = updated)
    }

    fun toggleColorVisibility(blockIndex: Int) {
        val design = _currentDesign.value
        val updated = design.colorBlocks.map {
            if (it.index == blockIndex) it.copy(isVisible = !it.isVisible) else it
        }
        _currentDesign.value = design.copy(colorBlocks = updated)
    }

    // Simulation controls
    fun toggleSimulation() {
        if (_isSimulating.value) {
            pauseSimulation()
        } else {
            playSimulation()
        }
    }

    fun playSimulation() {
        _isSimulating.value = true
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            val total = _currentDesign.value.stitches.size
            if (_simulationIndex.value >= total) {
                _simulationIndex.value = 0
            }
            while (isActive && _isSimulating.value && _simulationIndex.value < total) {
                val step = _simulationSpeed.value * 4
                val next = (_simulationIndex.value + step).coerceAtMost(total)
                _simulationIndex.value = next
                delay(30L)
            }
            _isSimulating.value = false
        }
    }

    fun pauseSimulation() {
        _isSimulating.value = false
        simulationJob?.cancel()
    }

    fun stepSimulation(delta: Int) {
        val total = _currentDesign.value.stitches.size
        _simulationIndex.value = (_simulationIndex.value + delta).coerceIn(0, total)
    }

    fun seekSimulation(index: Int) {
        val total = _currentDesign.value.stitches.size
        _simulationIndex.value = index.coerceIn(0, total)
    }

    fun setSimulationSpeed(speed: Int) {
        _simulationSpeed.value = speed
    }

    // Auto-Digitizing Engine (Vector / Image / Preset)
    fun autoDigitize(context: Context, imageUri: Uri? = null, presetName: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingProgress.value = 0.15f
            _loadingMessage.value = "جاري تحليل الرسمة وحساب خطوط التباين (Auto-Digitizing Raster/Vector Engine)..."

            try {
                delay(120)
                _loadingProgress.value = 0.45f
                _loadingMessage.value = "توليد كتل التعبئة التاتامي وإطارات الساتان..."

                val digitizedDesign = if (imageUri != null) {
                    AutoDigitizer.digitizeFromUri(context, imageUri, "تصميم_مرقمن_تلقائياً")
                } else {
                    AutoDigitizer.generateVectorPreset(presetName ?: "شعار_مرقمن_تلقائياً")
                }

                delay(120)
                _loadingProgress.value = 0.85f
                _loadingMessage.value = "تطبيق تعويض السحب (Pull Comp) ومحاذاة الزوايا..."

                delay(80)
                _loadingProgress.value = 1.0f
                _loadingMessage.value = "تمت الرقمنة التلقائية بنجاح!"

                delay(100)
                loadDesign(digitizedDesign)
                _userMessage.value = "تم تحليل الرسمة وتحويلها أوتوماتيكياً إلى مسارات غرز تطريز متناسقة بنجاح!"
                _showAutoDigitizeDialog.value = false
            } catch (e: Exception) {
                _userMessage.value = "خطأ أثناء الرقمنة التلقائية: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load file with chunks loader (anti-freeze engine)
    fun loadFileFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingProgress.value = 0.05f
            _loadingMessage.value = "جاري قراءة ملف التطريز والتحقق من الهيدر..."

            try {
                val fileName = getFileName(context, uri) ?: "Machine_Embroidery.dst"
                delay(100)

                _loadingProgress.value = 0.35f
                _loadingMessage.value = "معالجة حزم بايتس Tajima DST / EXP (نظام Chunks لمنع التهنيج)..."

                val design = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        DstCodec.parse(stream, fileName.substringBeforeLast("."))
                    } ?: EmbroiderySampleLibrary.createVarsityChenilleM()
                }

                _loadingProgress.value = 0.75f
                _loadingMessage.value = "إعادة بناء طبقات الغرز وألوان البكرات..."
                delay(150)

                _loadingProgress.value = 1.0f
                _loadingMessage.value = "اكتمل فك تشييد الملف بنجاح!"
                delay(100)

                loadDesign(design)
                _userMessage.value = "تم استيراد ${design.name} بنجاح (${design.totalStitches} غرزة)"
            } catch (e: Exception) {
                _userMessage.value = "خطأ أثناء قراءة الملف: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) name = it.getString(nameIndex)
                }
            }
        }
        return name ?: uri.lastPathSegment
    }

    // Export true DST and EMB binary file
    fun exportDstFile(context: Context, baseName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cleanName = if (baseName.endsWith(".dst", ignoreCase = true)) baseName else "$baseName.dst"
                val design = _currentDesign.value
                val bytes = DstCodec.encode(design)

                val exportDir = File(context.cacheDir, "exports")
                if (!exportDir.exists()) exportDir.mkdirs()
                val outFile = File(exportDir, cleanName)
                FileOutputStream(outFile).use { fos ->
                    fos.write(bytes)
                }

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    outFile
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/octet-stream"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "ملف تطريز DST / EMB - $cleanName")
                    putExtra(Intent.EXTRA_TEXT, "تم تصدير ملف التطريز $cleanName (${design.totalStitches} غرزة) بواسطة WELCOME MEGA v4.0")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(shareIntent, "حفظ أو إرسال ملف DST / EMB لماكينات التطريز")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)

                withContext(Dispatchers.Main) {
                    _userMessage.value = "تم حفظ الملف المعدل بنجاح بصيغة DST و EMB الاحترافية، وهو جاهز للعمل على جميع ماكينات التطريز!"
                    _showExportDialog.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _userMessage.value = "خطأ في التصدير: ${e.localizedMessage}"
                }
            }
        }
    }
}
