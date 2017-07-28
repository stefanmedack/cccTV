package de.stefanmedack.ccctv.util

import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.Row

operator fun ArrayObjectAdapter.plusAssign(items: Collection<Row>) {
    this.addAll(0, items)
}