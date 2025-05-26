package com.example.virtualwing.utils

import android.view.View
import android.widget.DatePicker
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.Matcher
import org.hamcrest.Matchers.instanceOf


fun setDateOnDatePicker(year: Int, month: Int, day: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return instanceOf(DatePicker::class.java)
        }


        override fun getDescription(): String {
            return "Set the date on the DatePicker"
        }

        override fun perform(uiController: UiController?, view: View?) {
            val datePicker = view as DatePicker
            // Month is 0-based in DatePicker (Jan=0)
            datePicker.updateDate(year, month - 1, day)
        }
    }
}
