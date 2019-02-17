package de.stefanmedack.ccctv.ui.main.Screens

import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.click
import de.stefanmedack.ccctv.ui.main.Utils.CustomMatchers.RecyclerViewItemMatcher
import android.support.test.espresso.Espresso.onView

class MenuScreen {
    val menuItem: (String) -> ViewInteraction = { menuName ->
        onView(RecyclerViewItemMatcher.menuNameMatches(menuName))
    }

    fun tapMenuItem(menuName: String) {
        menuItem(menuName).perform(click())
    }
}