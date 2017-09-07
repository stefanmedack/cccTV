package de.stefanmedack.ccctv.ui.about

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.app.DetailsSupportFragment
import android.support.v17.leanback.app.DetailsSupportFragmentBackgroundController
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.about.AboutDescriptionPresenter.AboutDescription

class AboutFragment : DetailsSupportFragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    private lateinit var detailsBackground: DetailsSupportFragmentBackgroundController

    private val mMainFragmentAdapter = BrowseSupportFragment.MainFragmentAdapter(this)

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return mMainFragmentAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
    }

    private fun setupUi() {
        detailsBackground = DetailsSupportFragmentBackgroundController(this)
        detailsBackground.enableParallax()
        detailsBackground.coverBitmap = BitmapFactory.decodeResource(resources,
                R.drawable.about_cover)

        // detail overview row - presents the detail, description and actions
        val detailOverviewRowPresenter = FullWidthDetailsOverviewRowPresenter(AboutDescriptionPresenter())
        detailOverviewRowPresenter.actionsBackgroundColor = ContextCompat.getColor(activity, R.color.teal_900)
        detailOverviewRowPresenter.backgroundColor = ContextCompat.getColor(activity, R.color.teal_900)

        // Setup action and detail row.
        val detailsOverview = DetailsOverviewRow(
                AboutDescription(
                        title = getString(R.string.app_name),
                        subtitle = getString(R.string.about_subtitle),
                        description = getString(R.string.about_description)
                )
        )

        detailsOverview.imageDrawable = ContextCompat.getDrawable(activity, R.drawable.store_qr)

        // list row - presents the speaker and the related events
        val listRowPresenter = ListRowPresenter().apply {
            shadowEnabled = false
        }

        adapter = ArrayObjectAdapter(
                // Setup PresenterSelector to distinguish between the different rows.
                ClassPresenterSelector().apply {
                    addClassPresenter(DetailsOverviewRow::class.java, detailOverviewRowPresenter)
                    // Setup ListRow Presenter without shadows, to hide highlighting boxes for SpeakerCards
                    addClassPresenter(ListRow::class.java, listRowPresenter)
                }
        ).apply {
            add(detailsOverview)
        }
    }

}