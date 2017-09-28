package de.stefanmedack.ccctv.repository

import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.util.applySchedulers
import io.reactivex.Flowable
import io.reactivex.Single

abstract class NetworkBoundResource<LocalType, NetworkType> internal constructor() {

    val resource: Flowable<Resource<LocalType>>
        get() = Flowable.concat(
                fetchLocal()
                        .take(1)
                        .map<Resource<LocalType>> { Resource.Success(it) }
                        .applySchedulers()
                        .filter { !isStale(it) }, // TODO maybe return local even if local data is stale
                fetchNetwork()
                        .toFlowable()
                        .map { mapNetworkToLocal(it) }
                        .flatMap {
                            Flowable.fromCallable {
                                saveLocal(it)
                                it
                            }.applySchedulers()
                        }
                        .map<Resource<LocalType>> { Resource.Success(it) }
                        //                        .onErrorReturn { Resource.Error("Error") } // TODO better msg
                        .applySchedulers()
        ).firstOrError()
                .toFlowable()
                .startWith(Resource.Loading())

    protected abstract fun fetchLocal(): Flowable<LocalType>

    protected abstract fun saveLocal(data: LocalType)

    protected abstract fun isStale(localResource: Resource<LocalType>): Boolean

    protected abstract fun fetchNetwork(): Single<NetworkType>

    protected abstract fun mapNetworkToLocal(data: NetworkType): LocalType

}
