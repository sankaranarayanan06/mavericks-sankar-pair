package com.example.services

import com.example.constants.vestings
import com.example.model.VestingResponse

fun getPendingVestingInfo(username: String): Any {
    val list = mutableListOf<VestingResponse>()

    for(vesting in vestings[username]!!){
        list.add(VestingResponse(vesting))
    }
    return list
}
