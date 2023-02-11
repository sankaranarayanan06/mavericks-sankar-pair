package com.example.services

import com.example.constants.Limits
import com.example.constants.inventoryData
import com.example.model.inventory.Inventory
import java.math.BigInteger

class InventoryHandler {
    companion object {
        fun addToNonPerformanceInventory(quantity: BigInteger, userName: String): Boolean {
            if(inventoryData[userName]!![1].free + quantity + quantity > Limits.MAX_INVENTORY_QUANTITY){
                return false
            }
            inventoryData[userName]!![1].free += quantity
            return true
        }

        fun addToPerformanceInventory(quantity: BigInteger, userName: String): Boolean {
            if(inventoryData[userName]!![0].free + quantity + quantity > Limits.MAX_INVENTORY_QUANTITY){
                return false
            }
            inventoryData[userName]!![0].free += quantity
            return true
        }

        fun lockNonPerformanceInventory(quantity: BigInteger, userName: String) {
            inventoryData[userName]!![1].locked += quantity
        }

        fun lockPerformanceInventory(quantity: BigInteger, userName: String) {
            inventoryData[userName]!![0].locked += quantity

        }

        fun getFreePerformanceInventory(userName: String): BigInteger {
            return inventoryData[userName]!![0].free
        }

        fun getFreeNonPerformanceInventory(userName: String): BigInteger {
            return inventoryData[userName]!![1].free
        }

        fun getLockedPerformanceInventory(userName: String): BigInteger {
            return inventoryData[userName]!![0].locked
        }

        fun getLockedNonPerformanceInventory(userName: String): BigInteger {
            return inventoryData[userName]!![1].locked
        }
        fun getInventoryInfo(username: String): MutableList<Inventory> {
            val inventoryInfo: MutableList<Inventory> = mutableListOf(Inventory(type = "PERFORMANCE"), Inventory(type = "NON_PERFORMANCE"))
            inventoryInfo[0] = inventoryData[username]?.get(0)!!
            inventoryInfo[1] = inventoryData[username]?.get(1)!!

            return inventoryInfo
        }

    }
}
