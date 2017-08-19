package de.stefanmedack.ccctv.ui.details

import android.os.Bundle
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.base.BaseInjectibleActivity

class DetailActivityWithVideoPlayback : BaseInjectibleActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_example)

        if (savedInstanceState == null) {
            val fragment = DetailFragmentWithVideoPlayback()
            supportFragmentManager.beginTransaction().replace(R.id.details_fragment, fragment).commit()
        }
    }
}
