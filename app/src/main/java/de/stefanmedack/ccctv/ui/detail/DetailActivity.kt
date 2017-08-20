package de.stefanmedack.ccctv.ui.detail

import android.os.Bundle
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.base.BaseInjectibleActivity

class DetailActivity : BaseInjectibleActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        if (savedInstanceState == null) {
            val fragment = DetailFragment()
            supportFragmentManager.beginTransaction().replace(R.id.details_fragment, fragment).commit()
        }
    }
}
