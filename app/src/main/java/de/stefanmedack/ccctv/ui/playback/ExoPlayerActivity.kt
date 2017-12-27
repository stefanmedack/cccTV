package de.stefanmedack.ccctv.ui.playback

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.util.STREAM_URL
import info.metadude.java.library.brockman.models.Stream
import info.metadude.java.library.brockman.models.Url.TYPE

class ExoPlayerActivity : FragmentActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_example)

        val ft = supportFragmentManager.beginTransaction()
        ft.add(
                R.id.videoFragment,
                ExoPlayerFragment().apply {
                    arguments = Bundle(1).also {
                        it.putString(STREAM_URL, intent.getStringExtra(STREAM_URL))
                    }
                },
                ExoPlayerFragment.TAG)
        ft.commit()
    }

    override fun onVisibleBehindCanceled() {
        mediaController?.transportControls?.pause()
        super.onVisibleBehindCanceled()
    }

    // TODO workaround for amazon - move to new implementation
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
        fun start(activity: FragmentActivity, item: Stream) {
            val intent = Intent(activity, ExoPlayerActivity::class.java)
            intent.putExtra(STREAM_URL, item.urls.find { it.type == TYPE.WEBM }?.url ?: item.urls[0].url)
            activity.startActivity(intent)
        }
    }

}
