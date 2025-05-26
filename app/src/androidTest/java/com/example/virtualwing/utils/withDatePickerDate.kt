package com.example.virtualwing.utils

import android.view.View
import android.widget.DatePicker
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

fun withDatePickerDate(year: Int, month: Int, day: Int): Matcher<View> {
    return object : BoundedMatcher<View, DatePicker>(DatePicker::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("with date: $year-${month}-$day")
        }

        override fun matchesSafely(datePicker: DatePicker): Boolean {
            return datePicker.year == year &&
                    datePicker.month == (month - 1) &&  // DatePicker months are zero indexed
                    datePicker.dayOfMonth == day
        }
    }
}
