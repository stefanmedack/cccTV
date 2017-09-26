package de.stefanmedack.ccctv.util

import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.Row

fun Activity.hasPermission(permission: String) : Boolean =
        PERMISSION_GRANTED == this.packageManager.checkPermission(permission, this.packageName)

operator fun ArrayObjectAdapter.plusAssign(items: List<*>?) {
    items?.forEach { this.add(it) }
}

operator fun ArrayObjectAdapter.plusAssign(row: Row) = this.add(row)