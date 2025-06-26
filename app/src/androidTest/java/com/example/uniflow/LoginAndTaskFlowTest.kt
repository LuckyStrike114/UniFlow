package com.example.uniflow

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.assertion.ViewAssertions.matches


@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginAndTaskFlowTest {

    @Test
    fun login_addTask_checkVisible() {
        // Pokreće LoginActivity
        ActivityScenario.launch(LoginActivity::class.java)

        // Unesi korisničko ime i lozinku
        onView(withHint("Korisničko ime")).perform(typeText("testuser"), closeSoftKeyboard())
        onView(withHint("Lozinka")).perform(typeText("test123"), closeSoftKeyboard())

        // Klikni Prijava
        onView(withText("Prijava")).perform(click())

        // Dodaj novu obavezu
        Thread.sleep(1000) // pričekaj navigaciju
        onView(withText("+")).perform(click())
        onView(withText("Odaberi datum")).perform(click())
        onView(withText("Potvrdi")).perform(click())

        onView(withHint("Vrsta obaveze")).perform(typeText("Zadatak"))
        onView(withHint("Naziv obaveze")).perform(typeText("Test E2E"))
        onView(withText("Spremi")).perform(click())

        // Provjeri prikaz obaveze
        Thread.sleep(1000)
        onView(withText("Zadatak: Test E2E")).check(matches(isDisplayed()))
    }
}
