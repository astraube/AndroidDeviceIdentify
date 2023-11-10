package com.straucorp.deviceidentify.extensions

/**
 * Created by Andre Straube on 09/12/2022
 *
 * @author Straube
 */
fun String?.isNotNullOrEmpty() = !this.isNullOrEmpty()
fun String?.isNotNullOrBlank() = !this.isNullOrBlank()