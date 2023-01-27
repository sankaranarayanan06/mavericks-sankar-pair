package com.example.services

import com.example.constants.vestingHistory

fun getVestingHistory(username: String): Any {
    return vestingHistory[username]!!
}
