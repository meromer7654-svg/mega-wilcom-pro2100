package com.example.model

enum class StitchFlag {
    NORMAL,
    JUMP,
    COLOR_CHANGE,
    END
}

enum class StitchType(val titleAr: String, val titleEn: String) {
    TATAMI("غرز التعبئة (Tatami / Fill)", "Tatami Fill"),
    SATIN("غرز الساتان الدقيقة (Satin)", "Satin Stitch"),
    RUN("غرز الركض والخطوط (Run Stitch)", "Run Stitch"),
    ZIGZAG("غرز الزجزاج والتأثيرات (Zigzag)", "Zigzag")
}

data class Stitch(
    val x: Float, // In 0.1 mm coordinate units
    val y: Float,
    val flag: StitchFlag = StitchFlag.NORMAL,
    val colorIndex: Int = 0,
    val isChenille: Boolean = false,
    val chainAngle: Float = 45f,
    val style: StitchType = StitchType.TATAMI,
    val isSelected: Boolean = false
) {
    val isJump: Boolean get() = flag == StitchFlag.JUMP
    val isColorChange: Boolean get() = flag == StitchFlag.COLOR_CHANGE
    val isEnd: Boolean get() = flag == StitchFlag.END
}

data class ColorBlock(
    val index: Int,
    val name: String,
    val colorHex: Long, // 0xFFRRGGBB
    val stitchCount: Int = 0,
    val isVisible: Boolean = true,
    val threadCode: String = "Madeira #1000",
    val defaultStyle: StitchType = StitchType.TATAMI
)

data class HoopConfig(
    val id: String,
    val name: String,
    val widthMm: Float,
    val heightMm: Float,
    val isRound: Boolean = false
) {
    companion object {
        val HOOP_100X100 = HoopConfig("100x100", "طارة 100 × 100 مم (قياسية)", 100f, 100f)
        val HOOP_150X150 = HoopConfig("150x150", "طارة 150 × 150 مم (متوسطة)", 150f, 150f)
        val HOOP_200X200 = HoopConfig("200x200", "طارة 200 × 200 مم (كبيرة)", 200f, 200f)
        val HOOP_300X200 = HoopConfig("300x200", "طارة 300 × 200 مم (جاكيت / ظهر)", 300f, 200f)
        val HOOP_ROUND_150 = HoopConfig("round_150", "طارة دائرية قطر 150 مم", 150f, 150f, isRound = true)

        val ALL = listOf(HOOP_100X100, HOOP_150X150, HOOP_200X200, HOOP_300X200, HOOP_ROUND_150)
    }
}

data class EmbroideryDesign(
    val name: String,
    val stitches: List<Stitch>,
    val colorBlocks: List<ColorBlock>,
    val widthMm: Float,
    val heightMm: Float,
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float,
    val pullCompensation: Float = 3f,
    val totalStitches: Int = stitches.count { it.flag == StitchFlag.NORMAL },
    val jumpCount: Int = stitches.count { it.flag == StitchFlag.JUMP },
    val colorChangeCount: Int = stitches.count { it.flag == StitchFlag.COLOR_CHANGE }
) {
    companion object {
        fun create(
            name: String,
            rawStitches: List<Stitch>,
            initialColors: List<ColorBlock>? = null,
            pullComp: Float = 3f
        ): EmbroideryDesign {
            if (rawStitches.isEmpty()) {
                return EmbroideryDesign(
                    name = name,
                    stitches = emptyList(),
                    colorBlocks = emptyList(),
                    widthMm = 0f,
                    heightMm = 0f,
                    minX = 0f,
                    maxX = 0f,
                    minY = 0f,
                    maxY = 0f,
                    pullCompensation = pullComp
                )
            }

            var minX = Float.MAX_VALUE
            var maxX = Float.MIN_VALUE
            var minY = Float.MAX_VALUE
            var maxY = Float.MIN_VALUE

            var curColor = 0
            val colorStitchCounts = mutableMapOf<Int, Int>()

            for (s in rawStitches) {
                if (s.flag == StitchFlag.COLOR_CHANGE) {
                    curColor++
                }
                if (s.x < minX) minX = s.x
                if (s.x > maxX) maxX = s.x
                if (s.y < minY) minY = s.y
                if (s.y > maxY) maxY = s.y

                if (s.flag == StitchFlag.NORMAL) {
                    val c = s.colorIndex
                    colorStitchCounts[c] = (colorStitchCounts[c] ?: 0) + 1
                }
            }

            if (minX == Float.MAX_VALUE) minX = 0f
            if (maxX == Float.MIN_VALUE) maxX = 0f
            if (minY == Float.MAX_VALUE) minY = 0f
            if (maxY == Float.MIN_VALUE) maxY = 0f

            val widthMm = (maxX - minX) / 10f
            val heightMm = (maxY - minY) / 10f

            val defaultPalette = listOf(
                0xFFFFB300L, // Wilcom Gold
                0xFF00BCD4L, // Cyan
                0xFFFF3333L, // Red
                0xFF4CAF50L, // Green
                0xFF0D47A1L, // Navy
                0xFFFF5722L, // Orange
                0xFFFFFFFFL, // White
                0xFF9C27B0L  // Purple
            )

            val colors = initialColors?.map { block ->
                block.copy(stitchCount = colorStitchCounts[block.index] ?: block.stitchCount)
            } ?: (0..curColor).map { idx ->
                ColorBlock(
                    index = idx,
                    name = "كتلة خيوط #${idx + 1}",
                    colorHex = defaultPalette[idx % defaultPalette.size],
                    stitchCount = colorStitchCounts[idx] ?: 0,
                    isVisible = true,
                    threadCode = "Madeira #${1000 + idx * 12}",
                    defaultStyle = StitchType.TATAMI
                )
            }

            return EmbroideryDesign(
                name = name,
                stitches = rawStitches,
                colorBlocks = colors,
                widthMm = widthMm,
                heightMm = heightMm,
                minX = minX,
                maxX = maxX,
                minY = minY,
                maxY = maxY,
                pullCompensation = pullComp
            )
        }
    }
}
