package de.stefanmedack.ccctv.ui.events

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.persistence.entities.Conference
import de.stefanmedack.ccctv.ui.base.BaseInjectableActivity
import de.stefanmedack.ccctv.util.CONFERENCE_ID
import de.stefanmedack.ccctv.util.replaceFragmentInTransaction

class EventsActivity : BaseInjectableActivity() {

    private val EVENTS_TAG = "EVENTS_TAG"

    var fragment: EventsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        fragment = EventsFragment()
        fragment?.let { frag ->
            frag.arguments = Bundle(1).apply {
                putInt(CONFERENCE_ID, intent.getIntExtra(CONFERENCE_ID, -1))
            }
            replaceFragmentInTransaction(frag, R.id.fragment, EVENTS_TAG)
        }
    }

    companion object {
        fun start(activity: Activity, conference: Conference) {
            val intent = Intent(activity, EventsActivity::class.java)
            intent.putExtra(CONFERENCE_ID, conference.id)

            activity.startActivity(intent)
        }
    }

}
