package com.example.virtualwing

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.virtualwing.ui.squadron.SquadronCreateActivity
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SquadronCreateActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SquadronCreateActivity::class.java)

    @Test
    fun testCreateSquadron_withValidInput_clicksCreateButton() {
        // Enter squadron name
        onView(withId(R.id.et_squadron_name))
            .perform(typeText("Test Squadron"), closeSoftKeyboard())

        // Enter squadron description
        onView(withId(R.id.et_squadron_description))
            .perform(typeText("A test squadron description"), closeSoftKeyboard())

        // Select a region from spinner
        onView(withId(R.id.spinner_region)).perform(click())
        onView(withText("North America")).perform(click())

        // Select a timezone from spinner
        onView(withId(R.id.spinner_timezone)).perform(click())
        onView(withText("ACT")).perform(click())

        // Click create button
        onView(withId(R.id.btn_create)).perform(click())
    }
}