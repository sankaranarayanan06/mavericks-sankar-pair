package com.example.services

import com.example.constants.vestingHistory
import com.example.model.VestingData

fun getVestingHistory(username: String): Any {
    return vestingHistory[username]!!
}
