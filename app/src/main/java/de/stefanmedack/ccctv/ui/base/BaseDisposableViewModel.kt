package de.stefanmedack.ccctv.ui.base

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseDisposableViewModel : ViewModel() {

    val disposables = CompositeDisposable()

    public override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}