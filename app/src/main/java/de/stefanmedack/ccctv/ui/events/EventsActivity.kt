package de.stefanmedack.ccctv.ui.events

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.persistence.entities.Conference
import de.stefanmedack.ccctv.ui.base.BaseInjectableActivity
import de.stefanmedack.ccctv.util.CONFERENCE_ID
import de.stefanmedack.ccctv.util.CONFERENCE_LOGO_URL
import de.stefanmedack.ccctv.util.CONFERENCE_TITLE
import de.stefanmedack.ccctv.util.addFragmentInTransaction

class EventsActivity : BaseInjectableActivity() {

    private val EVENTS_TAG = "EVENTS_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        if (savedInstanceState == null) {
            addFragmentInTransaction(
                    fragment = EventsFragment.create(
                            conferenceId = intent.getIntExtra(CONFERENCE_ID, -1),
                            conferenceTitle = intent.getStringExtra(CONFERENCE_TITLE),
                            conferenceLogoUrl = intent.getStringExtra(CONFERENCE_LOGO_URL)),
                    containerId = R.id.fragment,
                    tag = EVENTS_TAG)
        }
    }

    companion object {
        fun start(activity: Activity, conference: Conference) {
            val intent = Intent(activity.baseContext, EventsActivity::class.java)
            intent.putExtra(CONFERENCE_ID, conference.id)
            intent.putExtra(CONFERENCE_TITLE, conference.title)
            intent.putExtra(CONFERENCE_LOGO_URL, conference.logoUrl)

            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle())
        }
    }

}
