package com.solutionwin.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class StopwatchFormatTest {
    @Test
    fun formatsMinutesSecondsAndHundredths() {
        assertEquals("01:01.23", formatStopwatch(61_230))
    }

    @Test
    fun formatsZero() {
        assertEquals("00:00.00", formatStopwatch(0))
    }
}
