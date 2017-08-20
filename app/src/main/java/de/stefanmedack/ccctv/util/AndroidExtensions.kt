package de.stefanmedack.ccctv.util

import android.support.v17.leanback.widget.ArrayObjectAdapter

operator fun ArrayObjectAdapter.plusAssign(items: List<*>?) {
    if (items != null)
        this.addAll(0, items)
}