package de.stefanmedack.ccctv.ui.streaming

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.WindowManager
import androidx.os.bundleOf
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.util.STREAM_URL
import de.stefanmedack.ccctv.util.addFragmentInTransaction
import info.metadude.java.library.brockman.models.Stream
import info.metadude.java.library.brockman.models.Url.TYPE

class StreamingPlayerActivity : FragmentActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_example)

        // prevent stand-by while playing videos
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val fragment = StreamingPlayerFragment().apply {
            arguments = bundleOf(STREAM_URL to intent.getStringExtra(STREAM_URL))
        }
        addFragmentInTransaction(fragment, R.id.videoFragment, StreamingPlayerFragment.TAG)
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
        fun start(activity: FragmentActivity, item: Stream) {
            val intent = Intent(activity, StreamingPlayerActivity::class.java)
            intent.putExtra(STREAM_URL, item.urls.find { it.type == TYPE.WEBM }?.url ?: item.urls[0].url)
            activity.startActivity(intent)
        }
    }

}
