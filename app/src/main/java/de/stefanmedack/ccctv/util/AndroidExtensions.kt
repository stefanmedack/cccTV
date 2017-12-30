package de.stefanmedack.ccctv.util

import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.Row
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

fun Activity.hasPermission(permission: String) : Boolean =
        PERMISSION_GRANTED == this.packageManager.checkPermission(permission, this.packageName)

operator fun ArrayObjectAdapter.plusAssign(items: List<*>?) {
    items?.forEach { this.add(it) }
}

operator fun ArrayObjectAdapter.plusAssign(row: Row) = this.add(row)

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun FragmentActivity.addFragmentInTransaction(
        fragment: Fragment,
        containerId: Int,
        tag: String? = null,
        addToBackStack: Boolean = false
) {
    supportFragmentManager.inTransaction {
        add(containerId, fragment, tag).also {
            if (addToBackStack) addToBackStack(tag)
        }
    }
}

fun FragmentActivity.replaceFragmentInTransaction(
        fragment: Fragment,
        containerId: Int,
        tag: String? = null,
        addToBackStack: Boolean = false
) {
    supportFragmentManager.inTransaction {
        replace(containerId, fragment, tag).also {
            if (addToBackStack) addToBackStack(tag)
        }
    }
}