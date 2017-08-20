package de.stefanmedack.ccctv.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.widget.ImageView
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.model.MiniEvent
import de.stefanmedack.ccctv.ui.base.BaseInjectibleActivity
import de.stefanmedack.ccctv.util.EVENT
import de.stefanmedack.ccctv.util.SHARED_DETAIL_TRANSITION
import info.metadude.kotlin.library.c3media.models.Event

class DetailActivity : BaseInjectibleActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        if (savedInstanceState == null) {
            val fragment = DetailFragment()
            supportFragmentManager.beginTransaction().replace(R.id.details_fragment, fragment).commit()
        }
    }

    companion object {
        fun start(activity: Activity, event: Event, sharedImage: ImageView? = null) {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(EVENT, MiniEvent.ModelMapper.from(event))

            if (sharedImage != null) {
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        sharedImage,
                        SHARED_DETAIL_TRANSITION).toBundle()
                activity.startActivity(intent, bundle)
            } else {
                activity.startActivity(intent)
            }
        }
    }
}
