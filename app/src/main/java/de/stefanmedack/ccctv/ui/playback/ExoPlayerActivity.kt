package de.stefanmedack.ccctv.ui.playback

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.os.BuildCompat
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.util.STREAM_URL
import info.metadude.java.library.brockman.models.Stream
import info.metadude.java.library.brockman.models.Url.TYPE

class ExoPlayerActivity : FragmentActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_example)

        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.videoFragment, ExoPlayerFragment(), ExoPlayerFragment.TAG)
        ft.commit()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // This part is necessary to ensure that getIntent returns the latest intent when
        // VideoExampleActivity is started. By default, getIntent() returns the initial intent
        // that was set from another activity that started VideoExampleActivity. However, we need
        // to update this intent when for example, user clicks on another video when the currently
        // playing video is in PIP mode, and a new video needs to be started.
        setIntent(intent)
    }

    companion object {
        fun supportsPictureInPicture(context: Context): Boolean {
            return BuildCompat.isAtLeastN() && context.packageManager.hasSystemFeature(
                    PackageManager.FEATURE_PICTURE_IN_PICTURE)
        }

        fun start(activity: FragmentActivity, item: Stream) {
            val intent = Intent(activity, ExoPlayerActivity::class.java)
            val url = item.urls.find { it.type == TYPE.HLS }?.url ?: item.urls[0].url
//            Timber.d(url)
            intent.putExtra(STREAM_URL, url)
            activity.startActivity(intent)
        }
    }

}
