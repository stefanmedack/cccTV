package de.stefanmedack.ccctv.ui.search

import android.content.Intent
import android.os.Bundle
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.base.BaseInjectableActivity

class SearchActivity : BaseInjectableActivity() {

    private val SEARCH_TAG = "SEARCH_TAG"

    var fragment: SearchFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        if (savedInstanceState == null) {
            fragment = SearchFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(SEARCH_TAG) as SearchFragment
        }
    }

    override fun onSearchRequested(): Boolean {
        if (fragment?.hasResults() == true) {
            startActivity(Intent(this, SearchActivity::class.java))
        } else {
            fragment?.startRecognition()
        }
        return true
    }
}
