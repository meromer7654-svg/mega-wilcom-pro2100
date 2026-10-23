package com.example.samples

import com.example.model.ColorBlock
import com.example.model.EmbroideryDesign
import com.example.model.Stitch
import com.example.model.StitchFlag
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object EmbroiderySampleLibrary {

    fun getSampleDesigns(): List<EmbroideryDesign> {
        return listOf(
            createVarsityChenilleM(),
            createArabicCalligraphySalam(),
            createRoyalEagleCrest(),
            createLotusFlower(),
            createSportsPatch()
        )
    }

    /**
     * Varsity Chenille "M" - Professional Chenille moss-loop design with satin border
     */
    fun createVarsityChenilleM(): EmbroideryDesign {
        val stitches = mutableListOf<Stitch>()
        val colorBlocks = listOf(
            ColorBlock(0, "أرضية الشنيطة (Chenille Moss Loop)", 0xFFFFC107L, 0, true, "Madeira Frosted #12"),
            ColorBlock(1, "إطار الساتان الخارجي (Satin Border)", 0xFF00BCD4L, 0, true, "Isacord Poly #40")
        )

        // Chenille fill inside Letter M
        // The M has 2 vertical legs and a central V
        val width = 240f
        val height = 300f
        val leftX = -width / 2
        val topY = -height / 2

        // Block 0: Chenille Loop stitches in M
        // Dense zigzag and moss loops
        val rows = 35
        for (r in 0 until rows) {
            val y = topY + (r.toFloat() / rows) * height
            val spanWidth = if (r < rows / 2) {
                // top part of M has center dip
                80f
            } else {
                100f
            }

            // Left column of M
            for (col in 0..12) {
                val x = leftX + (col * 8f)
                val loopJitterX = (sin(r * 0.8f + col) * 6f)
                val loopJitterY = (cos(r * 0.8f + col) * 6f)
                val flag = if (r == 0 && col == 0) StitchFlag.JUMP else StitchFlag.NORMAL
                stitches.add(Stitch(x + loopJitterX, y + loopJitterY, flag, 0, isChenille = true, chainAngle = 45f))
            }

            // Right column of M
            for (col in 0..12) {
                val x = (width / 2) - (col * 8f)
                val loopJitterX = (sin(r * 0.7f + col) * 6f)
                val loopJitterY = (cos(r * 0.7f + col) * 6f)
                stitches.add(Stitch(x + loopJitterX, y + loopJitterY, StitchFlag.NORMAL, 0, isChenille = true, chainAngle = 45f))
            }

            // Center diagonal strokes
            if (r < rows * 0.75f) {
                val vY = y
                val progress = r.toFloat() / (rows * 0.75f)
                val vXLeft = leftX + 96f + (progress * 24f)
                val vXRight = -leftX - 96f - (progress * 24f)
                for (step in 0..8) {
                    val cx = vXLeft + (step * 3f)
                    stitches.add(Stitch(cx, vY, StitchFlag.NORMAL, 0, isChenille = true, chainAngle = 45f))
                }
                for (step in 0..8) {
                    val cx = vXRight - (step * 3f)
                    stitches.add(Stitch(cx, vY, StitchFlag.NORMAL, 0, isChenille = true, chainAngle = 45f))
                }
            }
        }

        // Color change to Satin Border
        stitches.add(Stitch(leftX, topY, StitchFlag.COLOR_CHANGE, 1))

        // Outer contour satin stitch
        val outlinePoints = listOf(
            Pair(-130f, -160f), Pair(-60f, -160f), Pair(0f, -40f), Pair(60f, -160f),
            Pair(130f, -160f), Pair(130f, 160f), Pair(70f, 160f), Pair(70f, 30f),
            Pair(0f, 110f), Pair(-70f, 30f), Pair(-70f, 160f), Pair(-130f, 160f), Pair(-130f, -160f)
        )

        for (i in 0 until outlinePoints.size - 1) {
            val p1 = outlinePoints[i]
            val p2 = outlinePoints[i + 1]
            val segments = 25
            for (s in 0..segments) {
                val t = s.toFloat() / segments
                val cx = p1.first + (p2.first - p1.first) * t
                val cy = p1.second + (p2.second - p1.second) * t
                // satin stitch width perpendicular
                val dx = (p2.second - p1.second)
                val dy = -(p2.first - p1.first)
                val len = kotlin.math.hypot(dx, dy).coerceAtLeast(1f)
                val nx = (dx / len) * 8f
                val ny = (dy / len) * 8f

                stitches.add(Stitch(cx - nx, cy - ny, StitchFlag.NORMAL, 1, isChenille = false))
                stitches.add(Stitch(cx + nx, cy + ny, StitchFlag.NORMAL, 1, isChenille = false))
            }
        }

        return EmbroideryDesign.create("شعار_شنيطة_حرف_M", stitches, colorBlocks)
    }

    /**
     * Arabic Calligraphy "سلام" (Peace) - Flowing metallic gold satin script
     */
    fun createArabicCalligraphySalam(): EmbroideryDesign {
        val stitches = mutableListOf<Stitch>()
        val colorBlocks = listOf(
            ColorBlock(0, "خيوط ذهبية ميتاليك (Metallic Gold)", 0xFFFFD700L, 0, true, "Madeira Gold #4"),
            ColorBlock(1, "خيوط الظل الأسود (Shadow Accent)", 0xFF00BCD4L, 0, true, "Isacord Silk #2")
        )

        // Generate curved calligraphy baseline and letters
        // Seen (س), Lam-Alif (لا), Meem (م)
        // Main flourish curve
        for (i in 0..450) {
            val t = i.toFloat() / 450f
            val angle = t * 3.5f * PI.toFloat()
            // Calligraphic swoosh
            val x = (t - 0.5f) * 360f
            val y = (sin(angle * 0.8f) * 45f) - (t * 20f) + (cos(t * PI.toFloat()) * 30f)

            // Thuluth calligraphy thickness modulation
            val thickness = (12f * (0.3f + 0.7f * sin(t * PI.toFloat()))).coerceIn(4f, 16f)

            val perpX = -sin(angle * 0.8f) * thickness
            val perpY = cos(angle * 0.8f) * thickness

            val flag = if (i == 0) StitchFlag.JUMP else StitchFlag.NORMAL
            stitches.add(Stitch(x + perpX, y + perpY, flag, 0))
            stitches.add(Stitch(x - perpX, y - perpY, StitchFlag.NORMAL, 0))
        }

        // Seen teeth (أسنان السين)
        for (tooth in 0..2) {
            val tx = -140f + (tooth * 30f)
            val ty = -30f
            for (step in 0..20) {
                val frac = step.toFloat() / 20f
                val h = sin(frac * PI.toFloat()) * 35f
                stitches.add(Stitch(tx + (frac * 15f), ty - h, StitchFlag.NORMAL, 0))
                stitches.add(Stitch(tx + (frac * 15f) + 4f, ty - h + 2f, StitchFlag.NORMAL, 0))
            }
        }

        // Color change to Cyan / Shadow flourish
        stitches.add(Stitch(80f, 60f, StitchFlag.COLOR_CHANGE, 1))
        // Meem loop and tail (دائرة الميم وذيلها)
        val meemCx = 120f
        val meemCy = 40f
        for (deg in 0..360 step 6) {
            val rad = deg * PI.toFloat() / 180f
            val r1 = 20f
            val r2 = 28f
            val x1 = meemCx + cos(rad) * r1
            val y1 = meemCy + sin(rad) * r1
            val x2 = meemCx + cos(rad) * r2
            val y2 = meemCy + sin(rad) * r2
            stitches.add(Stitch(x1, y1, StitchFlag.NORMAL, 1))
            stitches.add(Stitch(x2, y2, StitchFlag.NORMAL, 1))
        }

        // Meem tail going down left
        for (step in 0..60) {
            val frac = step.toFloat() / 60f
            val x = meemCx + 20f - (frac * 70f)
            val y = meemCy + (frac * 110f)
            stitches.add(Stitch(x, y, StitchFlag.NORMAL, 1))
            stitches.add(Stitch(x + 5f, y - 2f, StitchFlag.NORMAL, 1))
        }

        return EmbroideryDesign.create("مخطوطة_سلام_عربية", stitches, colorBlocks)
    }

    /**
     * Royal Eagle Crest - Multicolored (Gold, Royal Blue, Ruby Red, White)
     */
    fun createRoyalEagleCrest(): EmbroideryDesign {
        val stitches = mutableListOf<Stitch>()
        val colorBlocks = listOf(
            ColorBlock(0, "الدرع الملكي (Royal Blue Shield)", 0xFF0D47A1L, 0, true, "Sulky Blue #11"),
            ColorBlock(1, "أجنحة النسر الذهبية (Gold Eagle Wings)", 0xFFFFC107L, 0, true, "Madeira #10"),
            ColorBlock(2, "التاج والقرمزي (Ruby Red Crown)", 0xFFD32F2FL, 0, true, "Isacord Red #20"),
            ColorBlock(3, "نجوم الشرف البيضاء (White Stars)", 0xFFFFFFFFL, 0, true, "Polyneon White")
        )

        // 1. Central Shield (Royal Blue) - Tatami fill
        val shieldLines = 30
        for (i in 0..shieldLines) {
            val y = -60f + (i * 5f)
            val width = if (y < 40f) 80f else (80f - (y - 40f) * 1.5f).coerceAtLeast(10f)
            val xStart = -width / 2
            val xEnd = width / 2

            val flag = if (i == 0) StitchFlag.JUMP else StitchFlag.NORMAL
            stitches.add(Stitch(xStart, y, flag, 0))
            // Fill with small tatami steps
            val steps = 14
            for (s in 1..steps) {
                val stepX = xStart + (xEnd - xStart) * (s.toFloat() / steps)
                stitches.add(Stitch(stepX, y, StitchFlag.NORMAL, 0))
            }
        }

        // 2. Color change to Golden Wings
        stitches.add(Stitch(-80f, -80f, StitchFlag.COLOR_CHANGE, 1))

        // Left Wing & Right Wing feathers
        for (wingSide in listOf(-1f, 1f)) {
            for (feather in 0..7) {
                val featherBaseX = wingSide * (30f + feather * 10f)
                val featherBaseY = -20f + (feather * 12f)
                val tipX = wingSide * (100f + feather * 15f)
                val tipY = -90f + (feather * 22f)

                for (fStep in 0..16) {
                    val t = fStep.toFloat() / 16f
                    val fx = featherBaseX + (tipX - featherBaseX) * t
                    val fy = featherBaseY + (tipY - featherBaseY) * t
                    val perpX = -wingSide * sin(t * PI.toFloat()) * 8f
                    val perpY = cos(t * PI.toFloat()) * 8f
                    stitches.add(Stitch(fx + perpX, fy + perpY, StitchFlag.NORMAL, 1))
                    stitches.add(Stitch(fx - perpX, fy - perpY, StitchFlag.NORMAL, 1))
                }
            }
        }

        // Eagle Head in center
        for (h in 0..30) {
            val hy = -100f + (h * 1.5f)
            val hx = (sin(h * 0.2f) * 18f)
            stitches.add(Stitch(hx - 10f, hy, StitchFlag.NORMAL, 1))
            stitches.add(Stitch(hx + 10f, hy, StitchFlag.NORMAL, 1))
        }

        // 3. Color change to Ruby Red Crown
        stitches.add(Stitch(-30f, -125f, StitchFlag.COLOR_CHANGE, 2))
        // Royal Crown arches
        for (crownStep in 0..60) {
            val t = crownStep.toFloat() / 60f
            val cx = (t - 0.5f) * 70f
            val cy = -125f + sin(t * 3f * PI.toFloat()) * 18f - 6f
            stitches.add(Stitch(cx, cy, StitchFlag.NORMAL, 2))
            stitches.add(Stitch(cx + 3f, cy + 8f, StitchFlag.NORMAL, 2))
        }

        // 4. Color change to White Stars
        stitches.add(Stitch(-25f, 0f, StitchFlag.COLOR_CHANGE, 3))
        for (starIdx in listOf(-25f, 0f, 25f)) {
            for (pt in 0..10) {
                val ang = pt * (2 * PI.toFloat() / 5) * 2
                val r = if (pt % 2 == 0) 10f else 4f
                val sx = starIdx + cos(ang) * r
                val sy = 10f + sin(ang) * r
                stitches.add(Stitch(sx, sy, StitchFlag.NORMAL, 3))
            }
        }

        return EmbroideryDesign.create("شعار_النسر_الملكي", stitches, colorBlocks)
    }

    /**
     * Lotus Flower - 3 Colors (Emerald, Lotus Rose, Gold core)
     */
    fun createLotusFlower(): EmbroideryDesign {
        val stitches = mutableListOf<Stitch>()
        val colorBlocks = listOf(
            ColorBlock(0, "الأوراق الخضراء (Emerald Base)", 0xFF4CAF50L, 0, true, "Isacord Green"),
            ColorBlock(1, "بتلات اللوتس الوردية (Lotus Petals)", 0xFFE91E63L, 0, true, "Madeira Rose"),
            ColorBlock(2, "قلب الزهرة الذهبي (Golden Core)", 0xFFFFC107L, 0, true, "Polyester Gold")
        )

        // Green leaves at base
        for (leaf in 0..2) {
            val angle = (leaf - 1) * 0.5f
            for (s in 0..40) {
                val t = s.toFloat() / 40f
                val lx = sin(angle) * (t * 90f) + cos(angle) * (sin(t * PI.toFloat()) * 30f)
                val ly = 50f + (t * 40f) + (cos(t * PI.toFloat()) * 15f)
                val flag = if (leaf == 0 && s == 0) StitchFlag.JUMP else StitchFlag.NORMAL
                stitches.add(Stitch(lx, ly, flag, 0))
                stitches.add(Stitch(lx + 6f, ly - 3f, StitchFlag.NORMAL, 0))
            }
        }

        // Color change to Petals
        stitches.add(Stitch(0f, 30f, StitchFlag.COLOR_CHANGE, 1))

        // 7 Radiating petals
        for (petal in -3..3) {
            val pAngle = petal * 0.28f
            for (step in 0..50) {
                val t = step.toFloat() / 50f
                val dist = t * 110f
                val px = sin(pAngle) * dist + cos(pAngle) * sin(t * PI.toFloat()) * 25f
                val py = 20f - cos(pAngle) * dist
                stitches.add(Stitch(px, py, StitchFlag.NORMAL, 1))
                stitches.add(Stitch(px + 4f, py + 4f, StitchFlag.NORMAL, 1))
            }
        }

        // Color change to Golden core
        stitches.add(Stitch(0f, 15f, StitchFlag.COLOR_CHANGE, 2))
        for (ring in 0..25) {
            val rad = ring * 0.9f
            for (step in 0..12) {
                val a = step * (2f * PI.toFloat() / 12f)
                val kx = cos(a) * rad
                val ky = 15f + sin(a) * rad
                stitches.add(Stitch(kx, ky, StitchFlag.NORMAL, 2))
            }
        }

        return EmbroideryDesign.create("وردة_اللوتس_الشرقية", stitches, colorBlocks)
    }

    /**
     * Sports Club Patch - Chenille center with satin ring
     */
    fun createSportsPatch(): EmbroideryDesign {
        val stitches = mutableListOf<Stitch>()
        val colorBlocks = listOf(
            ColorBlock(0, "حلقة الساتان الزرقاء (Blue Outer Ring)", 0xFF00BCD4L, 0, true, "Isacord 01"),
            ColorBlock(1, "أرضية شنيطة صوفية (Chenille Wool Center)", 0xFFFF5722L, 0, true, "Madeira Wool #9"),
            ColorBlock(2, "نجمة البطولة الذهبية (Champion Star)", 0xFFFFC107L, 0, true, "Sulky Gold #3")
        )

        // Outer satin ring (radius 100 to 118)
        for (deg in 0..360 step 3) {
            val rad = deg * PI.toFloat() / 180f
            val x1 = cos(rad) * 98f
            val y1 = sin(rad) * 98f
            val x2 = cos(rad) * 118f
            val y2 = sin(rad) * 118f
            val flag = if (deg == 0) StitchFlag.JUMP else StitchFlag.NORMAL
            stitches.add(Stitch(x1, y1, flag, 0))
            stitches.add(Stitch(x2, y2, StitchFlag.NORMAL, 0))
        }

        // Color change to Chenille Center
        stitches.add(Stitch(0f, 0f, StitchFlag.COLOR_CHANGE, 1))

        // Concentric Chenille spiral inside patch
        var currentRadius = 88f
        while (currentRadius > 15f) {
            val steps = (currentRadius * 0.6f).toInt().coerceAtLeast(16)
            for (s in 0..steps) {
                val a = s.toFloat() / steps * 2 * PI.toFloat()
                val cx = cos(a) * currentRadius
                val cy = sin(a) * currentRadius
                stitches.add(Stitch(cx, cy, StitchFlag.NORMAL, 1, isChenille = true, chainAngle = 90f))
            }
            currentRadius -= 6f
        }

        // Color change to Gold Star in middle
        stitches.add(Stitch(0f, 0f, StitchFlag.COLOR_CHANGE, 2))
        for (p in 0..25) {
            val a = p * (4 * PI.toFloat() / 5)
            val r = if (p % 2 == 0) 32f else 12f
            val sx = cos(a) * r
            val sy = sin(a) * r
            stitches.add(Stitch(sx, sy, StitchFlag.NORMAL, 2))
        }

        return EmbroideryDesign.create("شارة_رياضية_شنيطة", stitches, colorBlocks)
    }
}
