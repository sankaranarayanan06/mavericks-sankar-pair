package com.example.services

import com.example.constants.vestings

fun getPendingVestingInfo(username: String): Any {
    return vestings[username]!!
}
