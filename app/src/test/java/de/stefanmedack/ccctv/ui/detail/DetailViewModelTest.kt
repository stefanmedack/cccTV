package de.stefanmedack.ccctv.ui.detail

import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import de.stefanmedack.ccctv.repository.EventRepository
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.amshove.kluent.When
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
    }

    @Test
    fun `bookmarking a not-bookmarked event should change bookmark state to true`() {
        val testEventId = 3
        val isBookmarked = false
        When calling repository.isBookmarked(testEventId) itReturns Flowable.just(isBookmarked)
        When calling repository.changeBookmarkState(testEventId, isBookmarked) itReturns Observable.just(true)
        detailViewModel.init(testEventId)

        detailViewModel.toggleBookmark()

        verify(repository).changeBookmarkState(testEventId, true)
    }

    @Test
    fun `un-bookmarking an event should change bookmark state to false`() {
        val testEventId = 3
        val isBookmarked = true
        When calling repository.isBookmarked(testEventId) itReturns Flowable.just(isBookmarked)
        When calling repository.changeBookmarkState(testEventId, isBookmarked) itReturns Observable.just(true)
        detailViewModel.init(testEventId)

        detailViewModel.toggleBookmark()

        verify(repository).changeBookmarkState(testEventId, false)
    }

    @Test
    fun `bookmarking an event after disposing the view model is ignored`() {
        val testEventId = 3
        val isBookmarked = true
        When calling repository.isBookmarked(testEventId) itReturns Flowable.just(isBookmarked)
        When calling repository.changeBookmarkState(testEventId, isBookmarked) itReturns Observable.just(true)
        detailViewModel.init(testEventId)
        reset(repository)

        detailViewModel.onCleared()
        detailViewModel.toggleBookmark()

        verifyNoMoreInteractions(repository)
    }

}