package com.example.uniflow

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import android.content.Intent


@RunWith(AndroidJUnit4::class)
class LoginAndAddTaskTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Before
    fun setupUser() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbHelper = DatabaseHelper(context)
        dbHelper.registerUser("123", "123")
    }

    @Test
    fun testFullFlow_addEditAndResetTask() {
        // Prijava
        composeTestRule.onNodeWithText("Korisničko ime").performTextInput("123")
        composeTestRule.onNodeWithText("Lozinka").performTextInput("123")
        composeTestRule.onNodeWithText("Prijava").performClick()

        composeTestRule.waitUntil(5_000) {
            composeTestRule.onAllNodesWithText("UniFlow").fetchSemanticsNodes().isNotEmpty()
        }

        // Dodaj obavezu
        composeTestRule.onNodeWithTag("AddTaskButton").performClick()
        composeTestRule.onNodeWithText("Odaberi datum").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Potvrdi").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Potvrdi").performClick()
        composeTestRule.onNodeWithText("Vrsta obaveze").performTextInput("Kolokvij")
        composeTestRule.onNodeWithText("Naziv obaveze").performTextInput("Matematika")
        composeTestRule.onNodeWithText("Vrijeme (opcionalno)").performTextInput("10:00")
        composeTestRule.onNodeWithText("Spremi").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Dodaj obavezu").fetchSemanticsNodes().isEmpty()
        }

        // Klikni današnji dan
        val today = LocalDate.now().dayOfMonth.toString()
        composeTestRule.onNodeWithText(today).performClick()

        // Provjeri obavezu
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodes(hasText("Kolokvij: Matematika", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Otvori izbornik i idi na Obaveze
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Obaveze").performClick()
        Thread.sleep(1000)

        // Vrati se na MainActivity
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("EXTRA_USERNAME", "123")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)


        // Otvori izbornik i idi na Postavke
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Postavke").performClick()

        // Klikni na Reset
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Resetiraj sve obaveze").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Resetiraj sve obaveze").performClick()

        // Potvrdi brisanje
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Obriši").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Obriši").performClick()

        // Vrati se na MainActivity
        context.startActivity(intent)


        // Provjeri da više nema obaveza za današnji dan
        composeTestRule.onNodeWithText(today).performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Kolokvij: Matematika").fetchSemanticsNodes().isEmpty()
        }
    }
    }
