package com.straucorp.deviceidentify.extensions

import android.os.Build

/**
 * Created by Andre Straube on 09/12/2022
 *
 * @author Straube
 *
 * Check Version API Android
 *
 * Para obter a versão do Android, você pode usar:
 * adb shell getprop ro.build.version.release
 *
 * Para obter o nível de API:
 * adb shell getprop ro.build.version.sdk
 */
fun isAtLeastApi(versionCode: Int) = Build.VERSION.SDK_INT >= versionCode
fun isAtLeastApi21() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
fun isAtLeastApi23() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun isAtLeastApi24() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun isAtLeastApi26() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun isAtLeastApi27() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
fun isAtLeastApi28() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
fun isAtLeastApi29() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
fun isAtLeastApi30() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
fun isAtLeastApi31() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

fun isAtEqualOrMinusApi25() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1