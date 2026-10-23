package com.example.dst

import com.example.model.EmbroideryDesign
import com.example.model.Stitch
import com.example.model.StitchFlag
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.roundToInt

object DstCodec {

    fun parse(inputStream: InputStream, designName: String = "Imported_Design"): EmbroideryDesign {
        val bytes = inputStream.readBytes()
        if (bytes.size < 512) {
            return EmbroideryDesign.create(designName, emptyList())
        }

        val stitches = mutableListOf<Stitch>()
        var curX = 0f
        var curY = 0f
        var colorIdx = 0

        var offset = 512
        while (offset + 2 < bytes.size) {
            val b1 = bytes[offset].toInt() and 0xFF
            val b2 = bytes[offset + 1].toInt() and 0xFF
            val b3 = bytes[offset + 2].toInt() and 0xFF
            offset += 3

            // Check for END command
            if ((b3 and 0xF3) == 0xF3) {
                stitches.add(Stitch(curX, curY, StitchFlag.END, colorIdx))
                break
            }

            var dx = 0
            var dy = 0

            // X coordinates ternary decoding
            if ((b1 and 0x01) != 0) dx += 1
            if ((b1 and 0x02) != 0) dx -= 1
            if ((b1 and 0x04) != 0) dx += 9
            if ((b1 and 0x08) != 0) dx -= 9

            if ((b2 and 0x01) != 0) dx += 3
            if ((b2 and 0x02) != 0) dx -= 3
            if ((b2 and 0x04) != 0) dx += 27
            if ((b2 and 0x08) != 0) dx -= 27

            if ((b3 and 0x04) != 0) dx += 81
            if ((b3 and 0x08) != 0) dx -= 81

            // Y coordinates ternary decoding
            if ((b1 and 0x80) != 0) dy += 1
            if ((b1 and 0x40) != 0) dy -= 1
            if ((b1 and 0x20) != 0) dy += 9
            if ((b1 and 0x10) != 0) dy -= 9

            if ((b2 and 0x80) != 0) dy += 3
            if ((b2 and 0x40) != 0) dy -= 3
            if ((b2 and 0x20) != 0) dy += 27
            if ((b2 and 0x10) != 0) dy -= 27

            if ((b3 and 0x20) != 0) dy += 81
            if ((b3 and 0x10) != 0) dy -= 81

            curX += dx
            curY += dy

            val flag = when {
                (b3 and 0xC0) == 0xC0 -> {
                    colorIdx++
                    StitchFlag.COLOR_CHANGE
                }
                (b3 and 0x40) != 0 -> {
                    colorIdx++
                    StitchFlag.COLOR_CHANGE
                }
                (b3 and 0x80) != 0 -> StitchFlag.JUMP
                else -> StitchFlag.NORMAL
            }

            stitches.add(Stitch(curX, curY, flag, colorIdx))
        }

        return EmbroideryDesign.create(designName, stitches)
    }

    fun encode(design: EmbroideryDesign): ByteArray {
        val out = ByteArrayOutputStream()

        val validStitches = design.stitches
        val stitchCount = validStitches.count { it.flag == StitchFlag.NORMAL }
        val colorCount = design.colorBlocks.size.coerceAtLeast(1) - 1

        val maxX = design.maxX.roundToInt().coerceAtLeast(0)
        val minX = (-design.minX.roundToInt()).coerceAtLeast(0)
        val maxY = design.maxY.roundToInt().coerceAtLeast(0)
        val minY = (-design.minY.roundToInt()).coerceAtLeast(0)

        // Generate standard 512-byte Tajima header
        val headerBuilder = StringBuilder()
        headerBuilder.append(String.format("LA:%-16s\r", design.name.take(16)))
        headerBuilder.append(String.format("ST:%7d\r", stitchCount))
        headerBuilder.append(String.format("CO:%3d\r", colorCount))
        headerBuilder.append(String.format("+X:%5d\r", maxX))
        headerBuilder.append(String.format("-X:%5d\r", minX))
        headerBuilder.append(String.format("+Y:%5d\r", maxY))
        headerBuilder.append(String.format("-Y:%5d\r", minY))
        headerBuilder.append("AX:+      0\r")
        headerBuilder.append("AY:+      0\r")
        headerBuilder.append("MX:+      0\r")
        headerBuilder.append("MY:+      0\r")
        headerBuilder.append("PD:******\r")

        var headerStr = headerBuilder.toString()
        val headerBytes = ByteArray(512) { 0x20 } // Fill with spaces
        val asciiBytes = headerStr.toByteArray(Charsets.US_ASCII)
        System.arraycopy(asciiBytes, 0, headerBytes, 0, asciiBytes.size.coerceAtMost(511))
        headerBytes[511] = 0x1A // EOF char for Tajima header

        out.write(headerBytes)

        var lastX = 0f
        var lastY = 0f

        for (stitch in validStitches) {
            val totalDx = (stitch.x - lastX).roundToInt()
            val totalDy = (stitch.y - lastY).roundToInt()

            // Break large jumps into steps of <= 121 units
            val steps = maxOf(1, (maxOf(abs(totalDx), abs(totalDy)) + 120) / 121)
            var remX = totalDx
            var remY = totalDy

            for (step in 1..steps) {
                val stepDx = remX / (steps - step + 1)
                val stepDy = remY / (steps - step + 1)
                remX -= stepDx
                remY -= stepDy

                val isLastStep = step == steps
                val flag = if (isLastStep) stitch.flag else StitchFlag.JUMP

                val bytes = encodeSingleStitch(stepDx, stepDy, flag)
                out.write(bytes)
            }

            lastX = stitch.x
            lastY = stitch.y
        }

        // End of code marker
        out.write(byteArrayOf(0x00, 0x00, 0xF3.toByte()))

        return out.toByteArray()
    }

    private fun encodeSingleStitch(dx: Int, dy: Int, flag: StitchFlag): ByteArray {
        var b1 = 0
        var b2 = 0
        var b3 = 0x03 // Base bits for Tajima

        when (flag) {
            StitchFlag.JUMP -> b3 = b3 or 0x80
            StitchFlag.COLOR_CHANGE -> b3 = b3 or 0xC0
            StitchFlag.END -> return byteArrayOf(0x00, 0x00, 0xF3.toByte())
            StitchFlag.NORMAL -> {}
        }

        // Encode X ternary
        var x = dx
        if (x > 40) { b3 = b3 or 0x04; x -= 81 }
        else if (x < -40) { b3 = b3 or 0x08; x += 81 }

        if (x > 13) { b2 = b2 or 0x04; x -= 27 }
        else if (x < -13) { b2 = b2 or 0x08; x += 27 }

        if (x > 4) { b1 = b1 or 0x04; x -= 9 }
        else if (x < -4) { b1 = b1 or 0x08; x += 9 }

        if (x > 1) { b2 = b2 or 0x01; x -= 3 }
        else if (x < -1) { b2 = b2 or 0x02; x += 3 }

        if (x > 0) { b1 = b1 or 0x01 }
        else if (x < 0) { b1 = b1 or 0x02 }

        // Encode Y ternary
        var y = dy
        if (y > 40) { b3 = b3 or 0x20; y -= 81 }
        else if (y < -40) { b3 = b3 or 0x10; y += 81 }

        if (y > 13) { b2 = b2 or 0x20; y -= 27 }
        else if (y < -13) { b2 = b2 or 0x10; y += 27 }

        if (y > 4) { b1 = b1 or 0x20; y -= 9 }
        else if (y < -4) { b1 = b1 or 0x10; y += 9 }

        if (y > 1) { b2 = b2 or 0x80; y -= 3 }
        else if (y < -1) { b2 = b2 or 0x40; y += 3 }

        if (y > 0) { b1 = b1 or 0x80 }
        else if (y < 0) { b1 = b1 or 0x40 }

        return byteArrayOf(b1.toByte(), b2.toByte(), b3.toByte())
    }
}
