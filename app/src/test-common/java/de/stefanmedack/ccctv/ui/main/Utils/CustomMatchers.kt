package de.stefanmedack.ccctv.ui.main.Utils

import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class CustomMatchers {

    class RecyclerViewItemMatcher private constructor(private val menuName: String)
        : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("The item text for the RecyclerView equals : $menuName")        }

        override fun matchesSafely(item: View?): Boolean {
            return allOf(isDescendantOfA(isAssignableFrom(RecyclerView::class.java)),
                    withText(menuName))
                    .matches(item)
        }

        companion object {
            fun menuNameMatches(menuName: String): RecyclerViewItemMatcher {
                return RecyclerViewItemMatcher(menuName)
            }
        }
    }



}