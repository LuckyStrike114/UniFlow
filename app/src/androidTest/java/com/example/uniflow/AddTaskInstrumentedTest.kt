package com.example.uniflow

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddTaskInstrumentedTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun addTaskFlow() {
        // login
        composeRule.onNodeWithText("Prijava").performClick()
        // open dialog
        composeRule.onNodeWithText("+").performClick()
        // select date
        composeRule.onNodeWithText("Odaberi datum").performClick()
        composeRule.onNodeWithText("Potvrdi").performClick()
        // input data
        composeRule.onNodeWithText("Vrsta obaveze").performTextInput("Predavanje")
        composeRule.onNodeWithText("Naziv obaveze").performTextInput("Kotlin")
        composeRule.onNodeWithText("Spremi").performClick()
        composeRule.onNodeWithText("Predavanje: Kotlin").assertExists()
    }
}
