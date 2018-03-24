package de.stefanmedack.ccctv.ui.main

import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.widget.Row
import android.support.v4.app.Fragment
import de.stefanmedack.ccctv.model.ConferenceGroup
import de.stefanmedack.ccctv.ui.about.AboutFragment
import de.stefanmedack.ccctv.ui.main.conferences.ConferencesFragment
import de.stefanmedack.ccctv.ui.main.home.HomeFragment
import de.stefanmedack.ccctv.ui.main.streaming.LiveStreamingFragment
import de.stefanmedack.ccctv.util.CONFERENCE_GROUP_TRANSLATIONS

class MainFragmentFactory : BrowseSupportFragment.FragmentFactory<Fragment>() {
    override fun createFragment(rowObj: Any): Fragment {
        return when ((rowObj as Row).headerItem.id) {
            KEY_ABOUT_PAGE -> AboutFragment()
            KEY_STREAMING_PAGE -> LiveStreamingFragment.create(rowObj.headerItem.name)
            KEY_HOME_PAGE -> HomeFragment()
            else -> ConferencesFragment.create(
                    // reverse lookup of ConferenceGroup by StringResourceId
                    CONFERENCE_GROUP_TRANSLATIONS.filterValues { it == rowObj.id.toInt() }.keys.firstOrNull() ?: ConferenceGroup.OTHER
            )
        }
    }

    companion object {
        const val KEY_HOME_SECTION = 1L
        const val KEY_HOME_PAGE = 2L
        const val KEY_STREAMING_SECTION = 3L
        const val KEY_STREAMING_PAGE = 4L
        const val KEY_LIBRARY_SECTION = 5L
        const val KEY_LIBRARY_PAGE = 6L
        const val KEY_ABOUT_SECTION = 7L
        const val KEY_ABOUT_PAGE = 8L
    }
}