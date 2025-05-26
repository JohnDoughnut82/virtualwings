package com.example.virtualwing

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.virtualwing.ui.flightLog.FlightLogActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule
import com.example.virtualwing.utils.setDateOnDatePicker
import com.example.virtualwing.utils.withDatePickerDate


@RunWith(AndroidJUnit4::class)
class FlightLogActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(FlightLogActivity::class.java)

    @Test
    fun testEnterFlightLogDataAndSave() {
        // Fill aircraft
        onView(withId(R.id.aircraftEditText))
            .perform(typeText("F-16 Falcon"), closeSoftKeyboard())

        // Fill flight hours
        onView(withId(R.id.flightHoursEditText))
            .perform(typeText("5"), closeSoftKeyboard())

        // Fill mission type
        onView(withId(R.id.missionTypeEditText))
            .perform(typeText("Recon"), closeSoftKeyboard())

        // Fill wingmen
        onView(withId(R.id.wingmenEditText))
            .perform(typeText("Wingman1, Wingman2"), closeSoftKeyboard())

        // Set DatePicker date to May 15, 2025
        onView(withId(R.id.missionDateEdit)).perform(setDateOnDatePicker(2025, 5, 15))

        // Verify date is set correctly
        onView(withId(R.id.missionDateEdit)).check(matches(withDatePickerDate(2025, 5, 15)))

        // Click save button
        onView(withId(R.id.saveLogButton)).perform(click())
    }
}

