package de.stefanmedack.ccctv.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.view.KeyEvent
import android.widget.ImageView
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.model.MiniEvent
import de.stefanmedack.ccctv.ui.base.BaseInjectibleActivity
import de.stefanmedack.ccctv.util.EVENT
import de.stefanmedack.ccctv.util.SHARED_DETAIL_TRANSITION
import info.metadude.kotlin.library.c3media.models.Event

class DetailActivity : BaseInjectibleActivity() {

    private val DETAIL_TAG = "DETAIL_TAG"

    var fragment: DetailFragment? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        if (savedInstanceState == null) {
            fragment = DetailFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment, DETAIL_TAG).commit()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(DETAIL_TAG) as DetailFragment
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return (fragment?.onKeyDown(keyCode) == true) || super.onKeyDown(keyCode, event)
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
