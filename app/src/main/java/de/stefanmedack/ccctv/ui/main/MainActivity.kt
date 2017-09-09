package de.stefanmedack.ccctv.ui.main

import android.os.Bundle
import android.view.KeyEvent
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.base.BaseInjectibleActivity

class MainActivity : BaseInjectibleActivity() {

    private val MAIN_TAG = "MAIN_TAG"

    var fragment: MainFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        if (savedInstanceState == null) {
            fragment = MainFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(MAIN_TAG) as MainFragment
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return (fragment?.onKeyDown(keyCode) == true) || super.onKeyDown(keyCode, event)
    }

}
