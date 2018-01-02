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
import de.stefanmedack.ccctv.util.replaceFragmentInTransaction

class EventsActivity : BaseInjectableActivity() {

    private val EVENTS_TAG = "EVENTS_TAG"

    var fragment: EventsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        fragment = EventsFragment()
        fragment?.let { frag ->
            frag.arguments = Bundle(3).apply {
                putInt(CONFERENCE_ID, intent.getIntExtra(CONFERENCE_ID, -1))
                putString(CONFERENCE_TITLE, intent.getStringExtra(CONFERENCE_TITLE))
                putString(CONFERENCE_LOGO_URL, intent.getStringExtra(CONFERENCE_LOGO_URL))
            }
            replaceFragmentInTransaction(frag, R.id.fragment, EVENTS_TAG)
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
