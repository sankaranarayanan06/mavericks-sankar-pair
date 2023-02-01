package com.example.services

import com.example.constants.inventoryData
import com.example.model.Inventory

class InventoryHandler {
    companion object {
        fun addToNonPerformanceInventory(quantity: Long, userName: String) {
            inventoryData[userName]!![0].free += quantity
        }

        fun addToPerformanceInventory(quantity: Long, userName: String) {
            inventoryData[userName]!![1].free += quantity
        }

        fun lockNoNPerformanceInventory(quantity: Long, userName: String) {
            inventoryData[userName]!![0].locked += quantity
        }

        fun lockPerformanceInventory(quantity: Long, userName: String) {
            inventoryData[userName]!![1].locked += quantity
        }

        fun getInventoryInfo(username: String): MutableList<Inventory> {
            val inventoryInfo: MutableList<Inventory> = mutableListOf(Inventory(type = "PERFORMANCE"), Inventory(type = "NON_PERFORMANCE"))
            inventoryInfo[0] = inventoryData[username]?.get(0)!!
            inventoryInfo[1] = inventoryData[username]?.get(1)!!

            return inventoryInfo
        }

    }
}
