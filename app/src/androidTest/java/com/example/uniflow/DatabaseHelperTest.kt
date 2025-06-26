// File: DatabaseHelperTest.kt
package com.example.uniflow

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DatabaseHelperTest {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        dbHelper = DatabaseHelper(context)
        dbHelper.registerUser("testuser", "testpass")
    }

    @Test
    fun testAddAndGetTaskForUser() {
        val task = Task(
            vrsta = "Testiranje",
            naziv = "Test obaveze",
            boja = Color.Red,
            datum = LocalDate.of(2025, 6, 30),
            vrijeme = "12:00"
        )

        dbHelper.addTask("testuser", task)
        val tasks = dbHelper.getTasksForUser("testuser")

        assertTrue(tasks.any {
            it.vrsta == "Testiranje" &&
                    it.naziv == "Test obaveze" &&
                    it.datum == LocalDate.of(2025, 6, 30)
        })
    }
}
