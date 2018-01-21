package de.stefanmedack.ccctv.ui.events

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.persistence.entities.Conference
import de.stefanmedack.ccctv.ui.base.BaseInjectableActivity
import de.stefanmedack.ccctv.util.*

class EventsActivity : BaseInjectableActivity() {

    private val EVENTS_TAG = "EVENTS_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        val fragment = if (intent.getStringExtra(SEARCH_QUERY) != null)
            EventsFragment.create(
                    intent.getStringExtra(SEARCH_QUERY),
                    intent.getStringExtra(EVENTS_VIEW_TITLE)
            )
         else
            EventsFragment.create(
                conferenceId = intent.getIntExtra(CONFERENCE_ID, -1),
                title = intent.getStringExtra(EVENTS_VIEW_TITLE),
                conferenceLogoUrl = intent.getStringExtra(CONFERENCE_LOGO_URL))


        if (savedInstanceState == null) {
            addFragmentInTransaction(
                    fragment,
                    containerId = R.id.fragment,
                    tag = EVENTS_TAG)
        }
    }

    companion object {
        fun startForConference(activity: Activity, conference: Conference) {
            val intent = Intent(activity.baseContext, EventsActivity::class.java)
            intent.putExtra(CONFERENCE_ID, conference.id)
            intent.putExtra(EVENTS_VIEW_TITLE, conference.title)
            intent.putExtra(CONFERENCE_LOGO_URL, conference.logoUrl)

            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle())
        }

        fun startWithSearch(activity: Activity, searchQuery: String) {
            val intent = Intent(activity.baseContext, EventsActivity::class.java)
            intent.putExtra(EVENTS_VIEW_TITLE, activity.getString(R.string.events_view_search_result_header, searchQuery))
            intent.putExtra(SEARCH_QUERY, searchQuery)

            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle())
        }
    }

}
