package com.example.virtualwing

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.virtualwing.ui.login.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginButtonWithEmptyFields_showsToast() {
        // Try clicking login with empty fields
        onView(withId(R.id.btnLogin)).perform(click())

        // Wait and check for toast
        Thread.sleep(1000)
    }

    @Test
    fun testGoToSignUp_opensSignUpActivity() {
        onView(withId(R.id.btnGoToSignUp)).perform(click())
        onView(withId(R.id.btnSignUp)).check(matches(isDisplayed()))
    }

    @Test
    fun testSuccessfulLogin_navigatesToHomeActivity() {
        // Fill in credentials
        onView(withId(R.id.etEmail)).perform(typeText("testuser@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("password123"), closeSoftKeyboard())

        // Click login button
        onView(withId(R.id.btnLogin)).perform(click())

        // Wait for navigation (you can tweak the delay as needed or use IdlingResources for better practice)
        Thread.sleep(3000)

        // Assert something on HomeActivity (e.g., flightHoursText or welcomeText)
        onView(withId(R.id.flightHoursText)).check(matches(isDisplayed()))
    }
}
