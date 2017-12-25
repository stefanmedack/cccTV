package de.stefanmedack.ccctv.repository

import de.stefanmedack.ccctv.BuildConfig
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.model.Resource.Success
import de.stefanmedack.ccctv.util.createFlowable
import info.metadude.java.library.brockman.StreamsService
import info.metadude.java.library.brockman.models.Offer
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamingRepository @Inject constructor(
        private val streamsService: StreamsService
) {
    var cachedStreams: List<Offer> = listOf()

    val streams: Flowable<Resource<List<Offer>>>
        get() =
            createFlowable(BackpressureStrategy.LATEST) { emitter ->
                emitter.onNext(Resource.Loading())
                streamsService.getOffers(BuildConfig.STREAMING_API_OFFERS_PATH).enqueue(object : Callback<List<Offer>> {
                    override fun onResponse(call: Call<List<Offer>>?, response: Response<List<Offer>>?) {
                        response?.body()?.let {
                            cachedStreams = it
                            emitter.onNext(Success(it))
                        }
                    }

                    override fun onFailure(call: Call<List<Offer>>?, t: Throwable?) {
                        emitter.onNext(Resource.Error("Could not load streams"))
                    }
                })
            }
}
