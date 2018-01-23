package de.stefanmedack.ccctv.ui.events

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.persistence.entities.Conference
import de.stefanmedack.ccctv.ui.base.BaseInjectableActivity
import de.stefanmedack.ccctv.util.FRAGMENT_ARGUMENTS
import de.stefanmedack.ccctv.util.addFragmentInTransaction

class EventsActivity : BaseInjectableActivity() {

    private val EVENTS_TAG = "EVENTS_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        if (savedInstanceState == null) {
            addFragmentInTransaction(
                    fragment = EventsFragment().apply { arguments = intent.getBundleExtra(FRAGMENT_ARGUMENTS) },
                    containerId = R.id.fragment,
                    tag = EVENTS_TAG)
        }
    }

    companion object {
        fun startForConference(activity: Activity, conference: Conference) {
            val intent = Intent(activity.baseContext, EventsActivity::class.java)
            intent.putExtra(FRAGMENT_ARGUMENTS, EventsFragment.getBundleForConference(
                    conferenceId = conference.id,
                    title = conference.title,
                    conferenceLogoUrl = conference.logoUrl
            ))
            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle())
        }

        fun startWithSearch(activity: Activity, searchQuery: String) {
            val intent = Intent(activity.baseContext, EventsActivity::class.java)
            intent.putExtra(FRAGMENT_ARGUMENTS, EventsFragment.getBundleForSearch(
                    searchQuery = searchQuery,
                    title = activity.getString(R.string.events_view_search_result_header, searchQuery)
            ))

            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle())
        }
    }

}
