package de.stefanmedack.ccctv.ui.about

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.app.DetailsSupportFragment
import android.support.v17.leanback.app.DetailsSupportFragmentBackgroundController
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.about.AboutDescriptionPresenter.AboutDescription
import de.stefanmedack.ccctv.ui.cards.SpeakerCardPresenter
import de.stefanmedack.ccctv.ui.detail.uiModels.SpeakerUiModel


class AboutFragment : DetailsSupportFragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    var shouldKeyLeftEventTriggerBackAnimation = true
    var shouldKeyUpEventTriggerBackAnimation = false

    private val mMainFragmentAdapter = BrowseSupportFragment.MainFragmentAdapter(this)

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return mMainFragmentAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi(view.context)
        setupEventListeners()
    }

    private fun setupUi(context: Context) {
        DetailsSupportFragmentBackgroundController(this).apply {
            enableParallax()
            coverBitmap = BitmapFactory.decodeResource(resources,
                    R.drawable.about_cover)
        }

        val detailOverviewRowPresenter = FullWidthDetailsOverviewRowPresenter(AboutDescriptionPresenter())
        detailOverviewRowPresenter.actionsBackgroundColor = ContextCompat.getColor(context, R.color.teal_900)
        detailOverviewRowPresenter.backgroundColor = ContextCompat.getColor(context, R.color.teal_900)

        val detailsOverview = DetailsOverviewRow(
                AboutDescription(
                        title = getString(R.string.app_name),
                        description = getString(R.string.about_description)
                )
        )
        detailsOverview.imageDrawable = ContextCompat.getDrawable(context, R.drawable.store_qr)

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

            // add Licenses
            val listRowAdapter = ArrayObjectAdapter(SpeakerCardPresenter())
            listRowAdapter.add(0, SpeakerUiModel(getString(R.string.libraries)))
            listRowAdapter.add(1, SpeakerUiModel(getString(R.string.voctocat)))
            add(ListRow(listRowAdapter))
        }
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            when ((item as SpeakerUiModel).name) {
                getString(R.string.libraries) -> showLicenses()
                getString(R.string.voctocat) -> Toast.makeText(activity, R.string.voctocat_toast, Toast.LENGTH_LONG).show()
            }
        }

        setOnItemViewSelectedListener { _, _, rowViewHolder, _ ->
            if (rowViewHolder is ListRowPresenter.ViewHolder) {
                shouldKeyLeftEventTriggerBackAnimation = rowViewHolder.selectedPosition == 0
            }
        }
    }

    private fun showLicenses() {
        val intent = Intent(activity, OssLicensesMenuActivity::class.java)
        intent.putExtra("title", getString(R.string.libraries))
        startActivity(intent)
    }

    override fun onSetDetailsOverviewRowStatus(presenter: FullWidthDetailsOverviewRowPresenter?, viewHolder: FullWidthDetailsOverviewRowPresenter.ViewHolder?, adapterPosition: Int, selectedPosition: Int, selectedSubPosition: Int) {
        // NOTE: this complicated handling is needed to find out, which state (STATE_HALF/STATE_FULL) the DetailsSupportFragment is in
        // -> any better way of figuring out the current state is very much welcome
        shouldKeyUpEventTriggerBackAnimation = adapterPosition == 0 && selectedPosition == 0 && selectedSubPosition == 0
        super.onSetDetailsOverviewRowStatus(presenter, viewHolder, adapterPosition, selectedPosition, selectedSubPosition)
    }
}