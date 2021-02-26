package com.example.myapplication

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat.checkSelfPermission

object PermissionChecker {

    fun isSdkVersionEnough(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return if (isSdkVersionEnough()) {
            checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
    }
}