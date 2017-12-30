package de.stefanmedack.ccctv.ui.main

import android.os.Bundle
import android.view.KeyEvent
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.base.BaseInjectableActivity
import de.stefanmedack.ccctv.util.replaceFragmentInTransaction

class MainActivity : BaseInjectableActivity() {

    private val MAIN_TAG = "MAIN_TAG"

    var fragment: MainFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        fragment = supportFragmentManager.findFragmentByTag(MAIN_TAG) as? MainFragment ?: MainFragment()
        fragment?.let { frag ->
            replaceFragmentInTransaction(frag, R.id.fragment, MAIN_TAG)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return (fragment?.onKeyDown(keyCode) == true) || super.onKeyDown(keyCode, event)
    }

}
