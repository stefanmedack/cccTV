package de.stefanmedack.ccctv.repository

import android.arch.persistence.room.EmptyResultSetException
import de.stefanmedack.ccctv.persistence.daos.BookmarkDao
import de.stefanmedack.ccctv.persistence.daos.EventDao
import de.stefanmedack.ccctv.persistence.daos.PlayPositionDao
import de.stefanmedack.ccctv.persistence.entities.Bookmark
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.persistence.entities.PlayPosition
import de.stefanmedack.ccctv.persistence.toEntity
import de.stefanmedack.ccctv.util.EMPTY_STRING
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
        private val bookmarkDao: BookmarkDao,
        private val playPositionDao: PlayPositionDao
) {
    fun getEvent(id: String): Flowable<Event> = eventDao.getEventById(id)
            .onErrorResumeNext(
                    mediaService.getEvent(id)
                            .applySchedulers()
                            .map { it.toEntity(EMPTY_STRING)!! }
            ).toFlowable()

    fun getEvents(ids: List<String>): Flowable<List<Event>> = ids.toFlowable()
            .flatMap { getEvent(it) }
            .toList()
            .toFlowable()

    fun getRecentEvents(): Flowable<List<Event>> = eventDao.getRecentEvents()

    fun getPopularEvents(): Flowable<List<Event>> = eventDao.getPopularEvents()

    fun getPromotedEvents(): Flowable<List<Event>> = eventDao.getPromotedEvents()

    fun getTrendingEvents(): Flowable<List<Event>> = eventDao.getPopularEventsYoungerThan(OffsetDateTime.now().minusDays(90))

    // TODO change to Resource<Single<EventRemote>>
    fun getEventWithRecordings(id: String): Single<EventRemote> = mediaService.getEvent(id)
            .applySchedulers()

    fun getBookmarkedEvents(): Flowable<List<Event>> = bookmarkDao.getBookmarkedEvents()

    fun isBookmarked(eventId: String): Flowable<Boolean> = bookmarkDao.isBookmarked(eventId)

    fun changeBookmarkState(eventId: String, shouldBeBookmarked: Boolean): Completable =
            Completable.fromAction {
                if (shouldBeBookmarked) {
                    bookmarkDao.insert(Bookmark(eventId))
                } else {
                    bookmarkDao.delete(Bookmark(eventId))
                }
            }.applySchedulers()

    fun getPlayedEvents(): Flowable<List<Event>> = playPositionDao.getPlayedEvents()

    fun getPlayedSeconds(eventId: String): Single<Int> = playPositionDao.getPlaybackSeconds(eventId)
            .applySchedulers() // TODO fix tests when applySchedulers is added
            .onErrorReturn { if (it is EmptyResultSetException) 0 else throw it }

    fun savePlayedSeconds(eventId: String, seconds: Int): Completable =
            Completable.fromAction {
                if (seconds > 0) {
                    playPositionDao.insert(PlayPosition(eventId, seconds))
                } else {
                    playPositionDao.delete(PlayPosition(eventId))
                }
            }.applySchedulers()

    fun deletePlayedSeconds(eventId: String): Completable =
            Completable.fromAction {
                playPositionDao.delete(PlayPosition(eventId))
            }.applySchedulers()

}