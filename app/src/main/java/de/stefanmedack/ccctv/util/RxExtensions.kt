package de.stefanmedack.ccctv.util

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.applySchedulers(): Single<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.applySchedulers(): Flowable<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.applySchedulers(): Observable<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun Completable.applySchedulers(): Completable = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> createFlowable(strategy: BackpressureStrategy, onSubscribe: (Emitter<T>) -> Unit): Flowable<T>
        = Flowable.create(onSubscribe, strategy)