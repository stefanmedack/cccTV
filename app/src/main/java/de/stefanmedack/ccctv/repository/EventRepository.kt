package de.stefanmedack.ccctv.repository

import de.stefanmedack.ccctv.persistence.daos.BookmarkDao
import de.stefanmedack.ccctv.persistence.daos.EventDao
import de.stefanmedack.ccctv.persistence.entities.Bookmark
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.persistence.toEntity
import de.stefanmedack.ccctv.util.applySchedulers
import info.metadude.kotlin.library.c3media.RxC3MediaService
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject
import javax.inject.Singleton
import info.metadude.kotlin.library.c3media.models.Event as EventRemote

@Singleton
class EventRepository @Inject constructor(
        private val mediaService: RxC3MediaService,
        private val bookmarkDao: BookmarkDao,
        private val eventDao: EventDao
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

    // TODO change to Resource<Single<EventRemote>>
    fun getEventWithRecordings(id: Int): Single<EventRemote> = mediaService.getEvent(id)
            .applySchedulers()

    fun isBookmarked(eventId: Int): Flowable<Boolean> = bookmarkDao.isBookmarked(eventId)

    fun changeBookmarkState(eventId: Int, isBookmarked: Boolean): Observable<Boolean> =
            Observable.create<Boolean> { emitter ->
                if (isBookmarked) {
                    bookmarkDao.delete(Bookmark(eventId))
                    emitter.onNext(true)
                } else {
                    bookmarkDao.insert(Bookmark(eventId))
                    emitter.onNext(true)
                }
            }.applySchedulers()
}