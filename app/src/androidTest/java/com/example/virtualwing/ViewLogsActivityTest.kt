package com.example.virtualwing

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.virtualwing.ui.flightLog.ViewLogsActivity
import org.hamcrest.CoreMatchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewLogsActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ViewLogsActivity::class.java)

    @Test
    fun testViewLogs_andFilter() {
        // Check if RecyclerView is displayed
        onView(withId(R.id.logs_recycler_view))
            .check(matches(isDisplayed()))

        // Type a query in the search input to filter logs
        onView(withId(R.id.search_input))
            .perform(typeText("test aircraft"), closeSoftKeyboard())

        // Check RecyclerView is still displayed after filtering
        onView(withId(R.id.logs_recycler_view))
            .check(matches(isDisplayed()))

        // Optionally test sorting spinner interaction
        onView(withId(R.id.sort_spinner))
            .perform(click())
        // Select second item in spinner (index 1)
        onData(anything()).atPosition(1).perform(click())

        // Click the sort direction toggle button
        onView(withId(R.id.sort_direction_toggle))
            .perform(click())
    }
}
