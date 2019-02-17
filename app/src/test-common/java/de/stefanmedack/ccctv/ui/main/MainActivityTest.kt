package de.stefanmedack.ccctv.ui.main

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import de.stefanmedack.ccctv.ui.main.Screens.MenuScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {

        Thread.sleep(5000)
        val menuScreen = MenuScreen()
        menuScreen.tapMenuItem("Congress")
    }
}
