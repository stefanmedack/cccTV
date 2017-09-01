package de.stefanmedack.ccctv.util

import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.Row

operator fun ArrayObjectAdapter.plusAssign(items: List<*>?) {
    if (items != null)
        this.addAll(0, items)
}

operator fun ArrayObjectAdapter.plusAssign(row: Row) = this.add(row)