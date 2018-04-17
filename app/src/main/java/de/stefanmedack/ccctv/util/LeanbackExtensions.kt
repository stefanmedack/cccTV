package de.stefanmedack.ccctv.util

import android.support.v17.leanback.widget.ObjectAdapter

fun ObjectAdapter.indexOf(item: Any): Int? {
    for (index in 0 until size()) {
        if (get(index) == item) {
            return index
        }
    }
    return null
}
