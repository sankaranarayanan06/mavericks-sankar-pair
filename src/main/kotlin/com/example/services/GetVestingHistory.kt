package com.example.services

import com.example.constants.vestingHistory
import com.example.model.VestingResponse

fun getVestingHistory(username: String): Any {
    val list = mutableListOf<VestingResponse>()

    for (vesting in vestingHistory[username]!!) {
        list.add(VestingResponse(vesting))
    }

    return list
}
