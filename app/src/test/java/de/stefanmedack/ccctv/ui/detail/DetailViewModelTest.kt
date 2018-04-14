package de.stefanmedack.ccctv.ui.detail

import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import de.stefanmedack.ccctv.repository.EventRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.amshove.kluent.When
import org.amshove.kluent.any
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DetailViewModelTest {

    @Mock
    private lateinit var repository: EventRepository

    @InjectMocks
    private lateinit var detailViewModel: DetailViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }

        When calling repository.isBookmarked(any()) itReturns Flowable.just(true)
    }

    @Test
    fun `bookmarking a not-bookmarked event should change bookmark state to true`() {
        val testEventId = 3
        val isBookmarked = false
        When calling repository.isBookmarked(testEventId) itReturns Flowable.just(isBookmarked)
        When calling repository.changeBookmarkState(testEventId, isBookmarked) itReturns Completable.complete()
        detailViewModel.init(testEventId)

        detailViewModel.toggleBookmark()

        verify(repository).changeBookmarkState(testEventId, true)
    }

    @Test
    fun `un-bookmarking an event should change bookmark state to false`() {
        val testEventId = 3
        val isBookmarked = true
        When calling repository.isBookmarked(testEventId) itReturns Flowable.just(isBookmarked)
        When calling repository.changeBookmarkState(testEventId, isBookmarked) itReturns Completable.complete()
        detailViewModel.init(testEventId)

        detailViewModel.toggleBookmark()

        verify(repository).changeBookmarkState(testEventId, false)
    }

    @Test
    fun `bookmarking an event after disposing the view model is ignored`() {
        val testEventId = 3
        val isBookmarked = true
        When calling repository.isBookmarked(testEventId) itReturns Flowable.just(isBookmarked)
        When calling repository.changeBookmarkState(testEventId, isBookmarked) itReturns Completable.complete()
        detailViewModel.init(testEventId)
        reset(repository)

        detailViewModel.onCleared()
        detailViewModel.toggleBookmark()

        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `saving playback position after a minimum playback time should be saved in repository`() {
        val testEventId = 3
        detailViewModel.init(testEventId)

        detailViewModel.inputs.savePlaybackPosition(playedSeconds = 180, totalDurationSeconds = 2300)

        verify(repository).savePlayedSeconds(eventId = testEventId, seconds = 180)
    }

    @Test
    fun `saving playback position before the minimum playback time should delete saved playback position`() {
        val testEventId = 3
        detailViewModel.init(testEventId)

        detailViewModel.inputs.savePlaybackPosition(playedSeconds = 30, totalDurationSeconds = 2300)

        verify(repository).deletePlayedSeconds(3)
    }

    @Test
    fun `saving playback position when video is almost finished should delete saved playback position`() {
        val testEventId = 3
        detailViewModel.init(testEventId)

        detailViewModel.inputs.savePlaybackPosition(playedSeconds = 2250, totalDurationSeconds = 2300)

        verify(repository).deletePlayedSeconds(3)
    }

}