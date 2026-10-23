package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.EmbroideryDesign
import com.example.model.HoopConfig
import com.example.model.StitchFlag
import com.example.model.StitchType
import com.example.ui.theme.WilcomCanvasBg
import com.example.ui.theme.WilcomCanvasCardBg
import com.example.ui.theme.WilcomCyan
import com.example.ui.theme.WilcomGold
import com.example.ui.theme.WilcomGrid
import com.example.viewmodel.SelectionMode
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun EmbroideryCanvas(
    design: EmbroideryDesign,
    is3DView: Boolean,
    isChenilleMode: Boolean,
    chenilleAngle: Float,
    chenilleThickness: Float = 5.0f,
    densityScale: Float,
    pullCompensation: Float,
    hoop: HoopConfig,
    selectionMode: SelectionMode,
    selectedIndices: Set<Int>,
    simulationIndex: Int,
    isSimulating: Boolean,
    onSelectRect: (minX: Float, minY: Float, maxX: Float, maxY: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1.2f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    // Auto-fit design into available canvas space whenever a new design is opened
    androidx.compose.runtime.LaunchedEffect(design.name, design.stitches.size, canvasSize) {
        if (canvasSize.width > 60f && canvasSize.height > 60f && design.stitches.isNotEmpty()) {
            val designWidth = (design.maxX - design.minX).coerceAtLeast(80f)
            val designHeight = (design.maxY - design.minY).coerceAtLeast(80f)
            val fitX = (canvasSize.width * 0.82f) / designWidth
            val fitY = (canvasSize.height * 0.82f) / designHeight
            scale = min(fitX, fitY).coerceIn(0.35f, 10f)
            panOffset = Offset.Zero
        }
    }

    // Selection drag state
    var selectionStart by remember { mutableStateOf<Offset?>(null) }
    var selectionCurrent by remember { mutableStateOf<Offset?>(null) }
    val lassoPoints = remember { mutableListOf<Offset>() }

    // Pinch-to-zoom & two-finger pan
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        if (selectionMode == SelectionMode.NONE) {
            scale = (scale * zoomChange).coerceIn(0.15f, 25f)
            panOffset += panChange
        }
    }

    val designCenterX = if (design.stitches.isNotEmpty()) (design.minX + design.maxX) / 2f else 0f
    val designCenterY = if (design.stitches.isNotEmpty()) (design.minY + design.maxY) / 2f else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WilcomCanvasBg)
            .testTag("embroidery_canvas_container")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coords ->
                    canvasSize = Size(coords.size.width.toFloat(), coords.size.height.toFloat())
                }
                .transformable(state = transformState)
                .pointerInput(selectionMode, scale, panOffset, designCenterX, designCenterY) {
                    if (selectionMode == SelectionMode.RECTANGLE || selectionMode == SelectionMode.MERGED) {
                        detectDragGestures(
                            onDragStart = { start ->
                                selectionStart = start
                                selectionCurrent = start
                            },
                            onDrag = { change, _ ->
                                selectionCurrent = change.position
                            },
                            onDragEnd = {
                                val s = selectionStart
                                val e = selectionCurrent
                                if (s != null && e != null) {
                                    val leftScreen = min(s.x, e.x)
                                    val rightScreen = max(s.x, e.x)
                                    val topScreen = min(s.y, e.y)
                                    val bottomScreen = max(s.y, e.y)

                                    val center = Offset(size.width / 2f, size.height / 2f) + panOffset
                                    val minDesignX = (leftScreen - center.x) / scale + designCenterX
                                    val maxDesignX = (rightScreen - center.x) / scale + designCenterX
                                    val minDesignY = (topScreen - center.y) / scale + designCenterY
                                    val maxDesignY = (bottomScreen - center.y) / scale + designCenterY

                                    onSelectRect(minDesignX, minDesignY, maxDesignX, maxDesignY)
                                }
                                selectionStart = null
                                selectionCurrent = null
                            },
                            onDragCancel = {
                                selectionStart = null
                                selectionCurrent = null
                            }
                        )
                    } else if (selectionMode == SelectionMode.LASSO) {
                        detectDragGestures(
                            onDragStart = { start ->
                                lassoPoints.clear()
                                lassoPoints.add(start)
                            },
                            onDrag = { change, _ ->
                                lassoPoints.add(change.position)
                            },
                            onDragEnd = {
                                if (lassoPoints.size > 2) {
                                    val center = Offset(size.width / 2f, size.height / 2f) + panOffset
                                    var minX = Float.MAX_VALUE
                                    var maxX = Float.MIN_VALUE
                                    var minY = Float.MAX_VALUE
                                    var maxY = Float.MIN_VALUE
                                    for (pt in lassoPoints) {
                                        val dx = (pt.x - center.x) / scale + designCenterX
                                        val dy = (pt.y - center.y) / scale + designCenterY
                                        if (dx < minX) minX = dx
                                        if (dx > maxX) maxX = dx
                                        if (dy < minY) minY = dy
                                        if (dy > maxY) maxY = dy
                                    }
                                    onSelectRect(minX, minY, maxX, maxY)
                                }
                                lassoPoints.clear()
                            },
                            onDragCancel = {
                                lassoPoints.clear()
                            }
                        )
                    } else {
                        // Pan with one finger when in NONE mode
                        detectDragGestures { _, dragAmount ->
                            panOffset += dragAmount
                        }
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f) + panOffset

            // 0. Draw CAD Worktable Canvas Card with Drop Shadow
            val hoopW = hoop.widthMm * 10f * scale
            val hoopH = hoop.heightMm * 10f * scale
            val cardPadding = 40f * scale.coerceIn(0.8f, 2f)
            val cardSize = Size(hoopW + cardPadding * 2f, hoopH + cardPadding * 2f)
            val cardTopLeft = Offset(center.x - cardSize.width / 2f, center.y - cardSize.height / 2f)

            drawRoundRect(
                color = Color(0x99000000),
                topLeft = cardTopLeft + Offset(3f, 5f),
                size = cardSize,
                cornerRadius = CornerRadius(14f, 14f)
            )
            drawRoundRect(
                color = WilcomCanvasCardBg,
                topLeft = cardTopLeft,
                size = cardSize,
                cornerRadius = CornerRadius(14f, 14f)
            )

            // 1. Draw Grid lines
            drawCadGrid(center, scale)

            // 2. Draw Hoop boundary
            drawHoopGuide(hoop, center, scale)

            // 3. Draw Stitches
            drawStitchDesign(
                design = design,
                center = center,
                scale = scale,
                is3D = is3DView,
                isChenille = isChenilleMode,
                chenilleAngle = chenilleAngle,
                chenilleThickness = chenilleThickness,
                densityMultiplier = densityScale / 100f,
                pullComp = pullCompensation,
                selectedIndices = selectedIndices,
                simulationIndex = simulationIndex
            )

            // 4. Draw Needle Position during simulation
            if (simulationIndex > 0 && simulationIndex <= design.stitches.size) {
                val currentStitch = design.stitches[min(simulationIndex - 1, design.stitches.size - 1)]
                val needlePos = Offset(
                    center.x + (currentStitch.x - designCenterX) * scale,
                    center.y + (currentStitch.y - designCenterY) * scale
                )
                drawNeedleIndicator(needlePos, isSimulating)
            }

            // 5. Draw active selection overlay
            if (selectionStart != null && selectionCurrent != null) {
                val s = selectionStart!!
                val e = selectionCurrent!!
                val rect = Rect(
                    left = min(s.x, e.x),
                    top = min(s.y, e.y),
                    right = max(s.x, e.x),
                    bottom = max(s.y, e.y)
                )
                drawRect(
                    color = WilcomGold.copy(alpha = 0.15f),
                    topLeft = rect.topLeft,
                    size = rect.size
                )
                drawRect(
                    color = WilcomGold,
                    topLeft = rect.topLeft,
                    size = rect.size,
                    style = Stroke(
                        width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }
        }

        // Floating Quick CAD Actions (Zoom in, Zoom out, Fit)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { scale = (scale * 1.3f).coerceAtMost(25f) },
                    modifier = Modifier.testTag("zoom_in_button")
                ) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "تكبير", tint = Color.White)
                }
                IconButton(
                    onClick = { scale = (scale / 1.3f).coerceAtLeast(0.15f) },
                    modifier = Modifier.testTag("zoom_out_button")
                ) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "تصغير", tint = Color.White)
                }
                IconButton(
                    onClick = {
                        scale = 1.2f
                        panOffset = Offset.Zero
                    },
                    modifier = Modifier.testTag("reset_view_button")
                ) {
                    Icon(Icons.Default.CropFree, contentDescription = "ملاءمة الشاشة", tint = WilcomGold)
                }
            }
        }
    }
}

private fun DrawScope.drawCadGrid(center: Offset, scale: Float) {
    val step = 100f * scale
    val width = size.width
    val height = size.height

    if (step > 15f) {
        var x = center.x % step
        while (x < width) {
            drawLine(
                color = WilcomGrid,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += step
        }

        var y = center.y % step
        while (y < height) {
            drawLine(
                color = WilcomGrid,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += step
        }
    }

    // Machine Origin Center Crosshair
    drawLine(
        color = Color(0x77FF5252),
        start = Offset(center.x - 45f, center.y),
        end = Offset(center.x + 45f, center.y),
        strokeWidth = 2f
    )
    drawLine(
        color = Color(0x77FF5252),
        start = Offset(center.x, center.y - 45f),
        end = Offset(center.x, center.y + 45f),
        strokeWidth = 2f
    )
    drawCircle(
        color = Color(0x99FF5252),
        radius = 6f,
        center = center,
        style = Stroke(width = 1.5f)
    )
}

private fun DrawScope.drawHoopGuide(hoop: HoopConfig, center: Offset, scale: Float) {
    val hoopWidthPx = hoop.widthMm * 10f * scale
    val hoopHeightPx = hoop.heightMm * 10f * scale

    val topLeft = Offset(center.x - hoopWidthPx / 2f, center.y - hoopHeightPx / 2f)

    if (hoop.isRound) {
        val radius = hoopWidthPx / 2f
        drawCircle(
            color = Color(0xFF383844),
            radius = radius,
            center = center,
            style = Stroke(
                width = 2.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
            )
        )
    } else {
        drawRoundRect(
            color = Color(0xFF383844),
            topLeft = topLeft,
            size = Size(hoopWidthPx, hoopHeightPx),
            cornerRadius = CornerRadius(24f * scale, 24f * scale),
            style = Stroke(
                width = 2.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
            )
        )
    }
}

private fun DrawScope.drawStitchDesign(
    design: EmbroideryDesign,
    center: Offset,
    scale: Float,
    is3D: Boolean,
    isChenille: Boolean,
    chenilleAngle: Float,
    chenilleThickness: Float,
    densityMultiplier: Float,
    pullComp: Float,
    selectedIndices: Set<Int>,
    simulationIndex: Int
) {
    val stitches = design.stitches
    if (stitches.isEmpty()) return

    val designCenterX = (design.minX + design.maxX) / 2f
    val designCenterY = (design.minY + design.maxY) / 2f

    val colorMap = mutableMapOf<Int, Color>()
    design.colorBlocks.forEach { block ->
        colorMap[block.index] = Color(block.colorHex)
    }

    val maxIdx = min(simulationIndex, stitches.size)
    var lastPt = center

    for (i in 0 until maxIdx) {
        val stitch = stitches[i]
        val block = design.colorBlocks.getOrNull(stitch.colorIndex)
        if (block?.isVisible == false) continue

        // Apply Pull Compensation outward expansion
        val pullOffsetFactor = (pullComp / 10f) * 0.8f
        val pt = Offset(
            center.x + ((stitch.x - designCenterX) * densityMultiplier + pullOffsetFactor) * scale,
            center.y + ((stitch.y - designCenterY) * densityMultiplier + pullOffsetFactor) * scale
        )

        val isSelected = selectedIndices.contains(i)
        val baseColor = if (isSelected) WilcomGold else (colorMap[stitch.colorIndex] ?: WilcomGold)
        val effectiveChenille = isChenille || stitch.isChenille

        if (i > 0 && stitch.flag == StitchFlag.NORMAL) {
            // Determine line width by StitchType or Chenille Thickness
            val strokeWidth = when {
                effectiveChenille -> (chenilleThickness * 0.85f) * scale.coerceIn(0.8f, 2.5f)
                stitch.style == StitchType.SATIN -> 3.0f * scale.coerceIn(0.8f, 2.5f)
                stitch.style == StitchType.RUN -> 1.0f * scale.coerceIn(0.6f, 1.8f)
                stitch.style == StitchType.ZIGZAG -> 2.4f * scale.coerceIn(0.8f, 2.2f)
                else -> if (is3D) 2.2f * scale.coerceIn(0.8f, 2.2f) else 1.2f // TATAMI / default
            }

            // 3D Drop Shadow / Chenille Velvet Shadow
            if (effectiveChenille) {
                drawLine(
                    color = Color(0x77FF5722), // Coral/Orange glowing chenille shadow
                    start = lastPt + Offset(1.5f, 1.5f),
                    end = pt + Offset(1.5f, 1.5f),
                    strokeWidth = strokeWidth * 1.2f,
                    cap = StrokeCap.Round
                )
            } else if (is3D && stitch.style != StitchType.RUN) {
                drawLine(
                    color = Color(0x88000000),
                    start = lastPt + Offset(2f, 2f),
                    end = pt + Offset(2f, 2f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            // Main Thread Stroke
            drawLine(
                color = baseColor,
                start = lastPt,
                end = pt,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Thread Specular Highlight for 3D realism
            if (is3D && !effectiveChenille && stitch.style != StitchType.RUN) {
                drawLine(
                    color = Color.White.copy(alpha = if (stitch.style == StitchType.SATIN) 0.38f else 0.25f),
                    start = lastPt - Offset(0.5f, 0.7f),
                    end = pt - Offset(0.5f, 0.7f),
                    strokeWidth = strokeWidth * 0.42f,
                    cap = StrokeCap.Round
                )
            }

            // Chenille Moss Loop & Direction Effect
            if (effectiveChenille) {
                val rad = (chenilleAngle * PI.toFloat()) / 180f
                val loopDist = (chenilleThickness * 0.7f) * scale
                val loopCenter = pt + Offset(cos(rad) * loopDist, sin(rad) * loopDist)

                drawCircle(
                    color = baseColor.copy(alpha = 0.90f),
                    radius = (chenilleThickness * 0.75f) * scale.coerceIn(0.8f, 2.5f),
                    center = loopCenter,
                    style = Stroke(width = 2.4f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.38f),
                    radius = (chenilleThickness * 0.3f) * scale.coerceIn(0.8f, 2.5f),
                    center = loopCenter
                )
            }

            // Needle Punch Hole Dot
            if (is3D) {
                drawCircle(
                    color = Color(0x66000000),
                    radius = 1.0f * scale.coerceIn(0.8f, 2.0f),
                    center = pt
                )
            }
        } else if (i > 0 && stitch.flag == StitchFlag.JUMP) {
            drawLine(
                color = Color(0x44AAAAAA),
                start = lastPt,
                end = pt,
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
            )
        }

        lastPt = pt
    }
}

private fun DrawScope.drawNeedleIndicator(needlePos: Offset, isSimulating: Boolean) {
    drawCircle(
        color = WilcomCyan,
        radius = 8f,
        center = needlePos,
        style = Stroke(width = 2f)
    )
    drawCircle(
        color = if (isSimulating) WilcomGold else WilcomCyan,
        radius = 3.5f,
        center = needlePos
    )
    drawLine(
        color = WilcomCyan,
        start = needlePos - Offset(14f, 0f),
        end = needlePos + Offset(14f, 0f),
        strokeWidth = 1.5f
    )
    drawLine(
        color = WilcomCyan,
        start = needlePos - Offset(0f, 14f),
        end = needlePos + Offset(0f, 14f),
        strokeWidth = 1.5f
    )
}
