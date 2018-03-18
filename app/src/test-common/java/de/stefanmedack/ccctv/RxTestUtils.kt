package de.stefanmedack.ccctv

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

fun <T> Observable<T>.getSingleTestResult(waitUntilCompletion: Boolean = false): T =
        this.test().apply { if (waitUntilCompletion) await() }.values()[0]

fun <T> Flowable<T>.getSingleTestResult(waitUntilCompletion: Boolean = false): T =
        this.test().apply { if (waitUntilCompletion) await() }.values()[0]

fun <T> Single<T>.getSingleTestResult(): T =
        this.test().values()[0]

