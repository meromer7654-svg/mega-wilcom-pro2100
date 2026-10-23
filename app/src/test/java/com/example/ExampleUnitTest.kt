package com.example

import com.example.dst.DstCodec
import com.example.model.EmbroideryDesign
import com.example.model.Stitch
import com.example.model.StitchFlag
import com.example.samples.EmbroiderySampleLibrary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class ExampleUnitTest {

    @Test
    fun testSampleLibraryGeneratesDesigns() {
        val samples = EmbroiderySampleLibrary.getSampleDesigns()
        assertTrue("Sample library must not be empty", samples.isNotEmpty())

        val varsity = samples.first { it.name.contains("حرف_M") }
        assertTrue("Varsity design must contain stitches", varsity.stitches.isNotEmpty())
        assertTrue("Varsity design has color blocks", varsity.colorBlocks.isNotEmpty())
    }

    @Test
    fun testDstCodecEncodeAndDecode() {
        val originalStitches = listOf(
            Stitch(0f, 0f, StitchFlag.JUMP),
            Stitch(20f, 30f, StitchFlag.NORMAL),
            Stitch(40f, 50f, StitchFlag.NORMAL),
            Stitch(40f, 50f, StitchFlag.COLOR_CHANGE),
            Stitch(10f, 10f, StitchFlag.NORMAL),
            Stitch(10f, 10f, StitchFlag.END)
        )
        val design = EmbroideryDesign.create("TestPattern", originalStitches)

        val dstBytes = DstCodec.encode(design)
        assertTrue("DST bytes must be at least 512 header bytes + records", dstBytes.size >= 512 + 3)

        val parsedDesign = DstCodec.parse(ByteArrayInputStream(dstBytes), "TestPattern")
        assertTrue("Decoded design must have stitches", parsedDesign.stitches.isNotEmpty())
        assertEquals(3, parsedDesign.totalStitches)
    }

    @Test
    fun testAutoDigitizerVectorPreset() {
        val design = com.example.digitizer.AutoDigitizer.generateVectorPreset("AutoEmblem")
        assertTrue("Digitized design must have stitches", design.stitches.isNotEmpty())
        assertEquals(3, design.colorBlocks.size)
        assertTrue("Total stitches should be positive", design.totalStitches > 100)
    }

    @Test
    fun testSmartStitchScalingEngine() {
        val vm = com.example.viewmodel.EmbroideryViewModel()
        val initialCount = vm.currentDesign.value.totalStitches
        assertTrue("Initial design must have stitches", initialCount > 0)

        // Scaling up to 150% triggers stitch densification/interpolation
        vm.applyGlobalScale(150f)
        val scaledUpCount = vm.currentDesign.value.totalStitches
        assertTrue("Scaling up should interpolate filler stitches", scaledUpCount >= initialCount)

        // Scaling down to 60% triggers stitch thinning
        vm.applyGlobalScale(60f)
        val scaledDownCount = vm.currentDesign.value.totalStitches
        assertTrue("Scaling down below 80% should thin clustered stitches", scaledDownCount < initialCount)

        // Reset returns to 100% and baseline stitch count
        vm.resetScales()
        assertEquals(100f, vm.globalScale.value)
        assertEquals(100f, vm.selectionScale.value)
        assertEquals(initialCount, vm.currentDesign.value.totalStitches)
    }
}
