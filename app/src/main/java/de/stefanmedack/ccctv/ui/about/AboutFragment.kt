package de.stefanmedack.ccctv.ui.about

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.app.DetailsSupportFragment
import android.support.v17.leanback.app.DetailsSupportFragmentBackgroundController
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.about.AboutDescriptionPresenter.AboutDescription
import de.stefanmedack.ccctv.ui.cards.SpeakerCardPresenter
import de.stefanmedack.ccctv.ui.detail.uiModels.SpeakerUiModel


class AboutFragment : DetailsSupportFragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    private val mMainFragmentAdapter = BrowseSupportFragment.MainFragmentAdapter(this)

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return mMainFragmentAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
        setupEventListeners()
    }

    private fun setupUi() {
        DetailsSupportFragmentBackgroundController(this).apply {
            enableParallax()
            coverBitmap = BitmapFactory.decodeResource(resources,
                    R.drawable.about_cover)
        }

        val detailOverviewRowPresenter = FullWidthDetailsOverviewRowPresenter(AboutDescriptionPresenter())
        detailOverviewRowPresenter.actionsBackgroundColor = ContextCompat.getColor(activity, R.color.teal_900)
        detailOverviewRowPresenter.backgroundColor = ContextCompat.getColor(activity, R.color.teal_900)
        // Setup action and detail row.
        val detailsOverview = DetailsOverviewRow(
                AboutDescription(
                        title = getString(R.string.app_name),
                        description = getString(R.string.about_description)
                )
        )
        detailsOverview.imageDrawable = ContextCompat.getDrawable(activity, R.drawable.store_qr)

        adapter = ArrayObjectAdapter(
                // Setup PresenterSelector to distinguish between the different rows.
                ClassPresenterSelector().apply {
                    addClassPresenter(DetailsOverviewRow::class.java, detailOverviewRowPresenter)
                    // Setup ListRow Presenter without shadows, to hide highlighting boxes
                    addClassPresenter(ListRow::class.java, ListRowPresenter().apply {
                        shadowEnabled = false
                    })
                }
        ).apply {
            add(detailsOverview)

            // add Licenses screen
            val listRowAdapter = ArrayObjectAdapter(SpeakerCardPresenter())
            listRowAdapter.add(SpeakerUiModel(getString(R.string.libraries)))
            add(ListRow(listRowAdapter))

        }
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is SpeakerUiModel) {
                showLicenses()
            }
        }
    }

    private fun showLicenses() {
        val intent = Intent(activity, OssLicensesMenuActivity::class.java)
        intent.putExtra("title", getString(R.string.libraries))
        startActivity(intent)
    }

}