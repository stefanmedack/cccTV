package de.stefanmedack.ccctv.repository

import de.stefanmedack.ccctv.persistence.daos.BookmarkDao
import de.stefanmedack.ccctv.persistence.daos.EventDao
import de.stefanmedack.ccctv.persistence.entities.Bookmark
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.persistence.toEntity
import de.stefanmedack.ccctv.util.applySchedulers
import info.metadude.kotlin.library.c3media.RxC3MediaService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton
import info.metadude.kotlin.library.c3media.models.Event as EventRemote

@Singleton
class EventRepository @Inject constructor(
        private val mediaService: RxC3MediaService,
        private val eventDao: EventDao,
        private val bookmarkDao: BookmarkDao
) {
    fun getEvent(id: Int): Flowable<Event> = eventDao.getEventById(id)
            .onErrorResumeNext(
                    mediaService.getEvent(id)
                            .applySchedulers()
                            .map { it.toEntity(-1)!! }
            ).toFlowable()

    fun getEvents(ids: List<Int>): Flowable<List<Event>> = ids.toFlowable()
            .flatMap { getEvent(it) }
            .toList()
            .toFlowable()

    fun getRecentEvents(): Flowable<List<Event>> = eventDao.getRecentEvents()

    fun getPopularEvents(): Flowable<List<Event>> = eventDao.getPopularEvents()

    fun getPromotedEvents(): Flowable<List<Event>> = eventDao.getPromotedEvents()

    fun getTrendingEvents(): Flowable<List<Event>> = eventDao.getPopularEventsYoungerThan(OffsetDateTime.now().minusDays(90))

    // TODO change to Resource<Single<EventRemote>>
    fun getEventWithRecordings(id: Int): Single<EventRemote> = mediaService.getEvent(id)
            .applySchedulers()

    fun getBookmarkedEvents(): Flowable<List<Event>> = bookmarkDao.getBookmarkedEvents()

    fun isBookmarked(eventId: Int): Flowable<Boolean> = bookmarkDao.isBookmarked(eventId)

    fun changeBookmarkState(eventId: Int, shouldBeBookmarked: Boolean): Completable =
            Completable.fromAction {
                if (shouldBeBookmarked) {
                    bookmarkDao.insert(Bookmark(eventId))
                } else {
                    bookmarkDao.delete(Bookmark(eventId))
                }
            }.applySchedulers()

}