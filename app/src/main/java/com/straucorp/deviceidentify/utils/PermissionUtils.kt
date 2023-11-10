package com.straucorp.deviceidentify.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * @author Andre Straube
 * Created on 31/07/2020.
 */

object PermissionUtils {

    fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }
    fun Activity.requestPermission(vararg permissions: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    /**
     * Checks some permission
     *
     * @return true - If have any of the permissions sent by parameter
     * @return true - If you do not have any of the permissions sent by parameter
     */
    fun Context.hasPermissionSome(vararg permissions: String): Boolean {
        permissions.forEach { permission ->
            if (hasPermission(permission)) {
                return true
            }
        }
        return false
    }

    fun Context.hasPermission(vararg permissions: String): Boolean {
        permissions.forEach { permission ->
            if (!hasPermission(permission)) {
                return false
            }
        }
        return true
    }

    fun Context.hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun Context.checkPermission(
        permission: String,
        actionGranted: () -> Unit = {},
        actionDenied: () -> Unit = {}
    ) {
        if (this.hasPermission(permission)) {
            actionGranted()
        } else {
            actionDenied()
        }
    }


    fun Activity.requestPermissionDialog(
        title: String? = null,
        message: String,
        permission: String,
        requestCode: Int,
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            //this.showConfirmDialog(title, message) {
                this.requestPermission(permission, requestCode)
            //}
        } else {
            // No explanation needed -> request the permission
            this.requestPermission(permission, requestCode)
        }
    }
}