package com.straucorp.deviceidentify.extensions

import androidx.collection.ArrayMap
import java.util.*

/**
 * Created by Andre Straube on 09/12/2022
 *
 * @author Straube
 */

fun <T> Collection<T>?.isNotNullOrEmpty() = !this.isNullOrEmpty()
fun <T, Y> Map<out T, Y>?.isNotNullOrEmpty() = !this.isNullOrEmpty()
fun <T> Array<T>?.isNotNullOrEmpty() = !this.isNullOrEmpty()

fun <K, V> arrayMapOf(): ArrayMap<K, V> = ArrayMap()

fun <K, V> arrayMapOf(vararg pairs: Pair<K, V>): ArrayMap<K, V> {
    val map = ArrayMap<K, V>(pairs.size)
    for (pair in pairs) {
        map[pair.first] = pair.second
    }
    return map
}

fun <K, V> Map<K, V>.toImmutableMap(): Map<K, V> {
    return if (isEmpty()) {
        emptyMap()
    } else {
        Collections.unmodifiableMap(LinkedHashMap(this))
    }
}