package com.straucorp.deviceidentify.utils

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Build.*
import android.provider.Settings
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import androidx.annotation.CheckResult
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.collection.ArrayMap
import com.straucorp.deviceidentify.extensions.*
import com.straucorp.deviceidentify.utils.PermissionUtils.hasPermission
import com.straucorp.deviceidentify.utils.PermissionUtils.hasPermissionSome
import java.security.MessageDigest
import java.util.*

/**
 * @author andre straube
 * @version 2.2 - 29/03/2021
 *
 * converter todas as propriedades em json string:
    val arr = mutableMapOf<String, Any>()
    DeviceInfos::class.memberProperties.forEach { property ->
        try {
            if (property.isFieldAccessible()) {
                arr[property.name] = property.get(DeviceInfos) as Any
            }
        } catch (e: Exception) { }
    }
    println(arr.toJsonString()")
 *
 */
@SuppressLint("StaticFieldLeak")
object DeviceManagement {

    private lateinit var context: Context

    private lateinit var telMgr: TelephonyManager

    //@Volatile
    //private lateinit var deviceUid: DeviceUuidFactory // TODO continuar implementação...

    @Synchronized
    operator fun invoke(context: Context) {
//        if (!::telMgr.isInitialized && !::deviceUid.isInitialized) {
//            build(context)
//        }
//
        if (!DeviceManagement::telMgr.isInitialized) {
            build(context)
        }
    }

    @Synchronized
    private fun build(context: Context) {
        DeviceManagement.context = context
        telMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // this.deviceUid = DeviceUuidFactory(context) // TODO continuar implementação...
    }

    /**
     * Identificador unico do DEVICE
     * Utilizado de acordo com regra
     * Pode ser configurado para uma das opcoes:
     *  deviceId
     *  deviceUuid
     *  imei
     */
    val identify: String
        get() {
            return getDeviceUniqueId()
        }

//    val deviceUuid: UUID
//        get() = deviceUid.deviceUuid!!

    val pseudoId: String
        @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
        get () = listOf(ID, BOARD, BRAND, MANUFACTURER, MODEL, CPU_ABI, HARDWARE, HOST, TIME).joinToString("|")

    val deviceBuildId: String
        get() = Build.ID

    val deviceBuildName: String
        get() = Build.DEVICE

    val deviceManufacturer: String
        get() = Build.MANUFACTURER

    val deviceBrand: String
        get() = Build.BRAND

    val deviceModel: String
        get() = Build.MODEL

    val androidId: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
        get() {
            var dId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            if (validateValue(dId))
                return dId

            if (context.hasPermission(Manifest.permission.READ_PHONE_STATE))
                dId = telMgr.deviceId

            return dId
        }

    val serialNumber: String
        @SuppressLint("HardwareIds", "PrivateApi", "StaticFieldLeak")
        @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
        get() {
            var serial: String = serialNumberByBuild() ?: ""

            if (!validateValue(serial)) {
                serial = serialNumberBySysProp() ?: ""
            }

            return serial
        }

    /**
     * Returns the IMEI (International Mobile Equipment Identity). Return null if IMEI is not
     * available.
     *
     * if API < 26 - Returns the unique device ID, for example, the IMEI for GSM and the MEID
     * or ESN for CDMA phones. Return null if device ID is not available.
     */
    val imei: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
        get() {
            try {
                if (isSIMAvailable && hasTelephony()) {
                    if (isAtLeastApi26()) {
                        return telMgr.imei
                    }
                    return telMgr.deviceId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }


    /**
     * Returns the MEID (Mobile Equipment Identifier). Return null if MEID is not available.
     *
     * if API < 26 - Returns the unique device ID, for example, the IMEI for GSM and the MEID
     * or ESN for CDMA phones. Return null if device ID is not available.
     */
    val meid: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
        get() {
            try {
                if (isSIMAvailable && hasTelephony()) {
                    if (isAtLeastApi26()) {
                        return telMgr.meid
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

    /**
     * Returns the phone number string for line 1, for example, the MSISDN
     * for a GSM phone. Return null if it is unavailable.
     */
    val phoneNumber: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
        get() {
            try {
                if (isSIMAvailable && hasTelephony()) {
                    return telMgr.line1Number
                }
            } catch (e: Exception) { }
            return ""
        }

    /**
     * Returns the serial number of the SIM, if applicable. Return null if it is
     * unavailable.
     */
    val simSerialNumber: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
        get() {
            try {
                if (isSIMAvailable && hasTelephony()) {
                    return telMgr.simSerialNumber
                }
            } catch (e: Exception) { }
            return ""
        }

    /**
     * Returns the unique subscriber ID, for example, the IMSI for a GSM phone.
     * Return null if it is unavailable.
     */
    val subscriberId: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
        get() {
            try {
                if (isSIMAvailable && hasTelephony()) {
                    return telMgr.subscriberId
                }
            } catch (e: Exception) { }
            return ""
        }

    // Error checking that probably isn't needed but I added just in case.
    val batteryLevel: Float
        get() {
            val batteryIntent = context.registerReceiver(
                    null,
                    IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            val level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            // Error checking that probably isn't needed but I added just in case.
            return if (level == -1 || scale == -1) {
                50.0f
            } else level.toFloat() / scale.toFloat() * 100.0f
        }

    val isSIMAvailable: Boolean
        @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
        get() {
            if (!context.hasPermission(Manifest.permission.READ_PHONE_STATE))
                return false

            return when (telMgr.simState) {
                TelephonyManager.SIM_STATE_ABSENT -> false
                TelephonyManager.SIM_STATE_NETWORK_LOCKED -> false
                TelephonyManager.SIM_STATE_PIN_REQUIRED -> false
                TelephonyManager.SIM_STATE_PUK_REQUIRED -> false
                TelephonyManager.SIM_STATE_READY -> true
                TelephonyManager.SIM_STATE_UNKNOWN -> false
                else -> false
            }
        }

    val allSimInfo: ArrayMap<String, String>?
        @SuppressLint("HardwareIds")
        @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
        get() {
            if (!isSIMAvailable || !hasTelephony()) {
                return null
            }

            val buildInfo = arrayMapOf<String, String>(
                "phoneNumber" to telMgr.line1Number,
                "simCountryIso" to telMgr.simCountryIso,
                "simOperator" to telMgr.simOperator,
                "simState" to telMgr.simState.toString(),
                "simSerialNumber" to telMgr.simSerialNumber,
                "getSimState" to telMgr.simState.toString()
            )
            if (isAtLeastApi28()) {
                buildInfo["simCarrierId"] = telMgr.simCarrierId.toString()
                buildInfo["simCarrierIdName"] = telMgr.simCarrierIdName as String?
            }
            if (isAtLeastApi29()) {
                buildInfo["simSpecificCarrierId"] = telMgr.simSpecificCarrierId.toString()
                buildInfo["simSpecificCarrierIdName"] = telMgr.simSpecificCarrierIdName.toString()
                buildInfo["isMultiSimSupported"] = telMgr.isMultiSimSupported.toString()
            }
            return buildInfo
        }

    val allCellInfo: List<CellInfo>?
        @SuppressLint("HardwareIds")
        @RequiresApi(VERSION_CODES.JELLY_BEAN_MR1)
        @RequiresPermission(allOf = [Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
        get() {
            try {
                if (isSIMAvailable && hasTelephony()) {
                    return telMgr.allCellInfo
                }
            } catch (e: Exception) { }
            return null
        }

    @CheckResult
    fun validateValue(value: String?, valueToCompare: String? = null): Boolean {
        if (!value.isNullOrEmpty() && value != UNKNOWN && value != valueToCompare)
            return true

        return false
    }

    /**
     * @see: https://developer.android.com/about/versions/10/privacy/changes#data-ids
     *
     * Retrieve SerialNumber with SystemProperties commands:
     *   adb shell getprop ro.serialno
     *   adb shell getprop ro.boot.serialno
     *   adb shell getprop ril.serialnumber
     *
     * @return device Serial Number obtained with SystemProperties
     */
    @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
    fun serialNumberBySysProp(): String? {
        var sn: String? = null

        val readPhonePermissions = arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                "android.permission.READ_PRIVILEGED_PHONE_STATE"
        )

        if (!context.hasPermissionSome(*readPhonePermissions))
            return sn

        try {
            // 1ª tentativa
            sn = SystemPropertiesProxy["ril.serialnumber"]
            if (validateValue(sn)) {
                return sn
            }

            // 2ª tentativa
            sn = SystemPropertiesProxy["ro.serialno"]
            if (validateValue(sn)) {
                return sn
            }

            // 3ª tentativa
            sn = SystemPropertiesProxy["ro.boot.serialno"]
            if (validateValue(sn)) {
                return sn
            }

            // 4ª tentativa
            sn = SystemPropertiesProxy["sys.serialnumber"]
            if (validateValue(sn)) {
                return sn
            }

        } catch (e: Exception) { }

        return if (validateValue(sn))
            sn
        else
            null
    }

    /**
     * @return device Serial Number obtained with Build Class
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
    fun serialNumberByBuild(): String? {
        var sn: String? = null

        if (!context.hasPermissionSome(
                        Manifest.permission.READ_PHONE_STATE,
                        "android.permission.READ_PRIVILEGED_PHONE_STATE"
                )
        ) {
            return sn
        }

        if (isAtLeastApi26()) {
            sn = getSerial()
        } else if (isAtEqualOrMinusApi25()) {
            sn = SERIAL
        }

        return if (validateValue(sn))
            sn
        else
            null
    }

    @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
    fun hasTelephony(): Boolean {
        return try {
            //devices below are phones only
            val pm = context.packageManager ?: return false
            val parameters: Array<Class<*>?> = arrayOfNulls(1)
            parameters[0] = String::class.java
            val method = pm.javaClass.getMethod("hasSystemFeature", *parameters)
            val parm = arrayOfNulls<Any>(1)
            parm[0] = "android.hardware.telephony"
            val retValue = method.invoke(pm, *parm)

            if (retValue is Boolean) retValue else false

        } catch (e: Exception) {
            false
        }
    }

    @SuppressLint("HardwareIds")
    @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE"])
    fun getAllBuildInfo(prefix: String = ""): Map<String, String> {
        return try {
            val map = arrayMapOf(
                "${prefix}androidId" to androidId,
                "${prefix}Build.ID" to ID,
                "${prefix}Build.SERIAL" to SERIAL, // @deprecated
                "${prefix}Build.DISPLAY" to DISPLAY,
                "${prefix}Build.PRODUCT" to PRODUCT,
                "${prefix}Build.DEVICE" to DEVICE,
                "${prefix}Build.BOARD" to BOARD,
                "${prefix}Build.MANUFACTURER" to MANUFACTURER,
                "${prefix}Build.BRAND" to BRAND,
                "${prefix}Build.MODEL" to MODEL,
                "${prefix}Build.BOOTLOADER" to BOOTLOADER,
                "${prefix}Build.HARDWARE" to HARDWARE,
                "${prefix}Build.CPU_ABI" to CPU_ABI,
                "${prefix}Build.CPU_ABI2" to CPU_ABI2,
                "${prefix}Build.TAGS" to TAGS,
                "${prefix}Build.TIME" to TIME.toString(),
                "${prefix}Build.TYPE" to TYPE,
                "${prefix}Build.FINGERPRINT" to FINGERPRINT,
                "${prefix}Build.USER" to USER,
                "${prefix}Build.HOST" to HOST,
                "${prefix}Build.RADIO" to RADIO,
                "${prefix}Build.radioVersion" to getRadioVersion()
            )

            if (isAtLeastApi21()) {
                map["${prefix}Build.SUPPORTED_ABIS"] = SUPPORTED_ABIS.joinToString(", ")
                map["${prefix}Build.SUPPORTED_32_BIT_ABIS"] = SUPPORTED_32_BIT_ABIS.joinToString(", ")
                map["${prefix}Build.SUPPORTED_64_BIT_ABIS"] = SUPPORTED_64_BIT_ABIS.joinToString(", ")
            }

            map.keys.forEach {
                if (it == null || map[it] == null || map[it].toString().isBlank())
                    map.remove(it)
            }

            map.toImmutableMap()

        } catch (e: Exception) {
            e.printStackTrace()

            emptyMap()
        }
    }


    /**
     * Return pseudo unique ID
     * @return ID
     */
    fun getUniquePseudoID(): String {
        if (!context.hasPermissionSome(Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE")) {
            return ""
        }
        val deviceUuid = UUID.nameUUIDFromBytes(pseudoId.toByteArray(Charsets.UTF_8))
        return deviceUuid.toString()
    }

    @SuppressLint("HardwareIds")
    fun getDeviceUniqueId(): String {
        try {
            var identifier = ""

            if (!context.hasPermissionSome(Manifest.permission.READ_PHONE_STATE, "android.permission.READ_PRIVILEGED_PHONE_STATE")) {
                return identifier
            }

            val longId = pseudoId
            val md: MessageDigest = MessageDigest.getInstance("MD5").apply {
                update(longId.toByteArray(), 0, longId.length)
            }
            val md5Bytes: ByteArray = md.digest()

            // creating a hex string
            for (md5Byte in md5Bytes) {
                val b = 0xFF and md5Byte.toInt()
                // if it is a single digit, make sure it have 0 in front (proper padding)
                if (b <= 0xF) {
                    identifier += "0"
                }
                identifier += Integer.toHexString(b)
            }
            return identifier
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }
}