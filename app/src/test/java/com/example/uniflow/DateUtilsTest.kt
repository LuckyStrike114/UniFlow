package com.example.uniflow

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateUtilsTest {
    @Test
    fun testDateFormatting() {
        val date = LocalDate.of(2025, 5, 15)
        val formatted = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        assertEquals("15.05.2025", formatted)
    }
}
