package com.example.virtualwing

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.virtualwing.ui.home.HomeActivity
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(HomeActivity::class.java)

    @Test
    fun testHomeActivityDisplaysUserInfoAndLogout() {
        // Check that flight hours TextView is displayed
        onView(withId(R.id.flightHoursText))
            .check(matches(isDisplayed()))

        // Check that welcome TextView is displayed
        onView(withId(R.id.welcomeText))
            .check(matches(isDisplayed()))

        // Click the logout button
        onView(withId(R.id.logoutButton))
            .perform(click())
    }
}