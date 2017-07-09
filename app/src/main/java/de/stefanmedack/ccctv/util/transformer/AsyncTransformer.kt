package de.stefanmedack.ccctv.util.transformer

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher

class AsyncTransformer<T> : ObservableTransformer<T, T>, SingleTransformer<T, T>, CompletableTransformer, MaybeTransformer<T, T>, FlowableTransformer<T, T> {

    override fun apply(@NonNull upstream: Observable<T>): ObservableSource<T> {
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun apply(@NonNull upstream: Completable): CompletableSource {
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun apply(@NonNull upstream: Flowable<T>): Publisher<T> {
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun apply(@NonNull upstream: Maybe<T>): MaybeSource<T> {
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun apply(@NonNull upstream: Single<T>): SingleSource<T> {
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
