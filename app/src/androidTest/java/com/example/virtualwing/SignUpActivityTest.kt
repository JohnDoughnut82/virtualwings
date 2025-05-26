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
import com.example.virtualwing.ui.signup.SignUpActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SignUpActivity::class.java)

    @Test
    fun testSuccessfulSignUp_navigatesToHomeActivity() {
        // Fill in signup fields
        onView(withId(R.id.etName)).perform(typeText("Test User"), closeSoftKeyboard())
        onView(withId(R.id.etEmail)).perform(typeText("testuser@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.etBio)).perform(typeText("I love flying"), closeSoftKeyboard())
        onView(withId(R.id.etAircraft)).perform(typeText("Cessna, Piper"), closeSoftKeyboard())

        // Click sign up button
        onView(withId(R.id.btnSignUp)).perform(click())

        // Wait for navigation
        Thread.sleep(3000)

        // Check HomeActivity is displayed by looking for unique view
        onView(withId(R.id.flightHoursText)).check(matches(isDisplayed()))
    }
}
