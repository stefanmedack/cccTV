package de.stefanmedack.ccctv.ui.main

import android.os.Bundle
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.base.BaseInjectibleActivity

class MainActivity : BaseInjectibleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        if (savedInstanceState == null) {
            val fragment = MainFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit()
        }
    }
}
