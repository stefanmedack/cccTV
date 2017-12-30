package de.stefanmedack.ccctv.ui.search

import android.content.Intent
import android.os.Bundle
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.base.BaseInjectableActivity
import de.stefanmedack.ccctv.util.replaceFragmentInTransaction

class SearchActivity : BaseInjectableActivity() {

    private val SEARCH_TAG = "SEARCH_TAG"

    var fragment: SearchFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        fragment = supportFragmentManager.findFragmentByTag(SEARCH_TAG) as? SearchFragment ?: SearchFragment()
        fragment?.let { frag ->
            replaceFragmentInTransaction(frag, R.id.fragment, SEARCH_TAG)
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
