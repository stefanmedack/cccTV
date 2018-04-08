package de.stefanmedack.ccctv.ui.detail

import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.repository.EventRepository
import de.stefanmedack.ccctv.ui.base.BaseDisposableViewModel
import de.stefanmedack.ccctv.ui.detail.uiModels.DetailUiModel
import de.stefanmedack.ccctv.ui.detail.uiModels.SpeakerUiModel
import de.stefanmedack.ccctv.ui.detail.uiModels.VideoPlaybackUiModel
import de.stefanmedack.ccctv.util.getRelatedEventIdsWeighted
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject
import info.metadude.kotlin.library.c3media.models.Event as EventRemote

class DetailViewModel @Inject constructor(
        private val repository: EventRepository
) : BaseDisposableViewModel(), Inputs, Outputs {

    internal val inputs: Inputs = this
    internal val outputs: Outputs = this

    private var eventId: Int = -1

    fun init(eventId: Int) {
        this.eventId = eventId

        disposables.addAll(
                doToggleBookmark.subscribeBy(onError = { Timber.w("DetailViewModel - doToggleBookmark - onError $it") }),
                doSavePlayedSeconds.subscribeBy(onError = { Timber.w("DetailViewModel - doSavePlayedSeconds - onError $it") })
        )
    }

    //<editor-fold desc="Inputs">

    override fun toggleBookmark() {
        bookmarkClickStream.onNext(0)
    }

    override fun savePlaybackPosition(seconds: Int) {
        savePlayPositionStream.onNext(seconds)
    }

    //</editor-fold>

    //<editor-fold desc="Outputs">

    override val detailData: Flowable<DetailUiModel>
        get() = repository.getEvent(eventId).flatMap { event ->
            getRelatedEvents(event).map { DetailUiModel(event = event, speaker = event.persons.map { SpeakerUiModel(it) }, related = it) }
        }

    override val videoPlaybackData: Single<VideoPlaybackUiModel>
        get() = Singles.zip(
                repository.getEventWithRecordings(eventId),
                repository.getPlayedSeconds(eventId),
                ::VideoPlaybackUiModel)

    override val isBookmarked: Flowable<Boolean>
        get() = repository.isBookmarked(eventId)

    //</editor-fold>

    private val bookmarkClickStream = PublishSubject.create<Int>()
    private val savePlayPositionStream = PublishSubject.create<Int>()

    private val doToggleBookmark
        get() = bookmarkClickStream
                .withLatestFrom(isBookmarked.toObservable(), { _, t2 -> t2 })
                .flatMapCompletable { updateBookmarkState(it) }

    private val doSavePlayedSeconds
        get() = savePlayPositionStream.flatMapCompletable { repository.savePlayedSeconds(eventId, it) }

    private fun getRelatedEvents(event: Event): Flowable<List<Event>> = repository.getEvents(event.getRelatedEventIdsWeighted())

    private fun updateBookmarkState(isBookmarked: Boolean): Completable = repository.changeBookmarkState(eventId, !isBookmarked)

}