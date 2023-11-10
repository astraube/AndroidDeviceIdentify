package com.straucorp.deviceidentify.utils

import android.annotation.SuppressLint
import android.content.Context

/**
 * @author Andre Straube
 * Created on 22/07/2020.
 */
object SystemPropertiesProxy {

    private const val classNameSysProps = "android.os.SystemProperties"

    /**
     * Get the value for the given key.
     * @usage
     * SystemPropertiesProxy.get("ro.serialno")
     * SystemPropertiesProxy.get("ril.serialnumber")
     *
     * @return an empty string if the key isn't found
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    operator fun get(prop: String?): String? {
        var ret: String? = null
        try {
            val systemProps = Class.forName(classNameSysProps)

            //Parameters Types
            val getProp = systemProps.getMethod("get", String::class.java)

            //Parameters
            ret = getProp(prop) as String?

        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ret
    }

    /**
     * Get the value for the given key.
     * @usage
     * SystemPropertiesProxy.get(this, "ro.serialno")
     * SystemPropertiesProxy.get(this, "ril.serialnumber")
     *
     * @return an empty string if the key isn't found
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    operator fun get(context: Context, prop: String?): String? {
        var ret: String? = null
        try {
            val cl = context.classLoader
            val systemProps = cl.loadClass(classNameSysProps)

            //Parameters Types
            val paramTypes: Array<Class<*>?> = arrayOfNulls(1)
            paramTypes[0] = String::class.java
            val get = systemProps.getMethod("get", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(1)
            params[0] = (prop as String)
            ret = get.invoke(systemProps, *params) as String
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ret
    }

    /**
     * Get the value for the given key.
     * @usage
     * SystemPropertiesProxy.get(this, "ro.serialno")
     * SystemPropertiesProxy.get(this, "ril.serialnumber")
     *
     * @return if the key isn't found, return def if it isn't null, or an empty string otherwise
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    operator fun get(context: Context, prop: String?, def: String?): String? {
        var ret: String? = null
        try {
            val cl = context.classLoader
            val systemProps = cl.loadClass(classNameSysProps)

            //Parameters Types
            val paramTypes: Array<Class<*>?> = arrayOfNulls(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = String::class.java
            val get = systemProps.getMethod("get", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(2)
            params[0] = (prop as String)
            params[1] = (def as String)
            ret = get.invoke(systemProps, *params) as String
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ret
    }

    /**
     * Get the value for the given key, and return as an integer.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as an integer, or def if the key isn't found or
     * cannot be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    fun getInt(context: Context, prop: String?, def: Int): Int? {
        var ret = def
        try {
            val cl = context.classLoader
            val systemProps = cl.loadClass(classNameSysProps)

            //Parameters Types
            val paramTypes = arrayOfNulls<Class<*>?>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = Int::class.javaPrimitiveType
            val getInt = systemProps.getMethod("getInt", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(2)
            params[0] = (prop as String)
            params[1] = def
            ret = getInt.invoke(systemProps, *params) as Int
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ret
    }

    /**
     * Get the value for the given key, and return as a long.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a long, or def if the key isn't found or
     * cannot be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    fun getLong(context: Context, prop: String?, def: Long): Long? {
        var ret: Long? = null
        try {
            val cl = context.classLoader
            val systemProps = cl.loadClass(classNameSysProps)

            //Parameters Types
            val paramTypes = arrayOfNulls<Class<*>?>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = Long::class.javaPrimitiveType
            val getLong = systemProps.getMethod("getLong", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(2)
            params[0] = (prop as String)
            params[1] = def
            ret = getLong.invoke(systemProps, *params) as Long
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ret
    }

    /**
     * Get the value for the given key, returned as a boolean.
     * Values 'n', 'no', '0', 'false' or 'off' are considered false.
     * Values 'y', 'yes', '1', 'true' or 'on' are considered true.
     * (case insensitive).
     * If the key does not exist, or has any other value, then the default
     * result is returned.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a boolean, or def if the key isn't found or is
     * not able to be parsed as a boolean.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    fun getBoolean(context: Context, prop: String?, def: Boolean): Boolean? {
        var ret: Boolean? = null
        try {
            val cl = context.classLoader
            val systemProps = cl.loadClass(classNameSysProps)

            //Parameters Types
            val paramTypes = arrayOfNulls<Class<*>?>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = Boolean::class.javaPrimitiveType
            val getBoolean = systemProps.getMethod("getBoolean", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(2)
            params[0] = (prop as String)
            params[1] = def
            ret = getBoolean.invoke(systemProps, *params) as Boolean
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ret
    }

    /**
     * Set the value for the given key.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     * @throws IllegalArgumentException if the value exceeds 92 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    operator fun set(prop: String?, value: String?) {
        try {
            val systemProps = Class.forName(classNameSysProps)

            //Parameters Types
            val paramTypes: Array<Class<*>?> = arrayOfNulls(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = String::class.java
            val set = systemProps.getMethod("set", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(2)
            params[0] = (prop as String)
            params[1] = (value as String)
            set.invoke(systemProps, *params)
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}