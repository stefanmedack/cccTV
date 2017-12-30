package de.stefanmedack.ccctv.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.ImageView
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.ui.base.BaseInjectableActivity
import de.stefanmedack.ccctv.util.EVENT_ID
import de.stefanmedack.ccctv.util.EVENT_PICTURE
import de.stefanmedack.ccctv.util.SHARED_DETAIL_TRANSITION
import de.stefanmedack.ccctv.util.replaceFragmentInTransaction

class DetailActivity : BaseInjectableActivity() {

    private val DETAIL_TAG = "DETAIL_TAG"

    var fragment: DetailFragment? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        // prevent stand-by while playing videos
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        fragment = DetailFragment()
        fragment?.let { frag ->
            frag.arguments = Bundle(2).apply {
                putInt(EVENT_ID, intent.getIntExtra(EVENT_ID, -1))
                putString(EVENT_PICTURE, intent.getStringExtra(EVENT_PICTURE))
            }
            replaceFragmentInTransaction(frag, R.id.fragment, DETAIL_TAG)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return (fragment?.onKeyDown(keyCode) == true) || super.onKeyDown(keyCode, event)
    }

    // TODO workaround for amazon - move to new implementation
    override fun onVisibleBehindCanceled() {
        mediaController?.transportControls?.pause()
        super.onVisibleBehindCanceled()
    }

    companion object {
        fun start(activity: Activity, event: Event, sharedImage: ImageView? = null) {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(EVENT_ID, event.id)
            intent.putExtra(EVENT_PICTURE, event.thumbUrl)

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
