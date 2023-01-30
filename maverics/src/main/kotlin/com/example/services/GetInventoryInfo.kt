package com.example.services

import com.example.constants.inventoryData
import com.example.model.Inventory
import com.example.model.allUsers

fun getInventoryInfo(username: String): MutableList<Inventory> {
    val inventoryInfo: MutableList<Inventory> = mutableListOf(Inventory(type = "PERFORMANCE"), Inventory(type = "NON_PERFORMANCE"))
    inventoryInfo[0] = inventoryData[username]?.get(0)!!
    inventoryInfo[1] = inventoryData[username]?.get(1)!!

    return inventoryInfo
}
