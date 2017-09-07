package de.stefanmedack.ccctv.util

import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.Row

operator fun ArrayObjectAdapter.plusAssign(items: List<*>?) {
    items?.forEach { this.add(it) }
}

operator fun ArrayObjectAdapter.plusAssign(row: Row) = this.add(row)