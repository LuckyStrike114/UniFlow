package com.example.uniflow

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAddTaskDialogAddsTaskToList() {
        composeTestRule.setContent {
            MainScreen(username = "TestUser")
        }

        // Otvori AddTaskDialog klikom na FAB
        composeTestRule.onNodeWithText("+").performClick()

        // Odaberi datum (pretpostavljamo da je već neki default odabran)
        composeTestRule.onNodeWithText("Odaberi datum").performClick()
        composeTestRule.onNodeWithText("Potvrdi").performClick()

        // Unesi podatke
        composeTestRule.onNodeWithText("Vrsta obaveze").performTextInput("TestVrsta")
        composeTestRule.onNodeWithText("Naziv obaveze").performTextInput("TestNaziv")

        // Klikni Spremi
        composeTestRule.onNodeWithText("Spremi").performClick()

        // Provjeri da se obaveza prikazuje u listi
        composeTestRule.onNodeWithText("TestVrsta: TestNaziv").assertIsDisplayed()
    }
}
