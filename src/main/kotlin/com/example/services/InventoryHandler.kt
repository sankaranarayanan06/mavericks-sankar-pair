package com.example.services

import com.example.constants.Limits
import com.example.constants.inventoryData
import com.example.model.inventory.EsopType
import com.example.model.inventory.Inventory
import java.math.BigInteger

class InventoryHandler {
    companion object {
        fun addToNonPerformanceInventory(quantity: BigInteger, userName: String): Boolean {
            if(inventoryData[userName]!![1].getFreeEsop() + quantity + quantity > Limits.MAX_INVENTORY_QUANTITY){
                return false
            }
            inventoryData[userName]!![1].addFreeEsops(quantity)
            return true
        }

        fun addToPerformanceInventory(quantity: BigInteger, userName: String): Boolean {
            if(inventoryData[userName]!![0].getFreeEsop() + quantity + quantity > Limits.MAX_INVENTORY_QUANTITY){
                return false
            }
            inventoryData[userName]!![0].addFreeEsops(quantity)
            return true
        }

        fun lockNonPerformanceInventory(quantity: BigInteger, userName: String) {
            inventoryData[userName]!![1].addLockedEsops(quantity)
        }

        fun lockPerformanceInventory(quantity: BigInteger, userName: String) {
            inventoryData[userName]!![0].addLockedEsops(quantity)
        }

        fun getFreePerformanceInventory(userName: String): BigInteger {
            return inventoryData[userName]!![0].getFreeEsop()
        }

        fun getFreeNonPerformanceInventory(userName: String): BigInteger {
            return inventoryData[userName]!![1].getFreeEsop()
        }

        fun getLockedPerformanceInventory(userName: String): BigInteger {
            return inventoryData[userName]!![0].getLockedEsop()
        }

        fun getLockedNonPerformanceInventory(userName: String): BigInteger {
            return inventoryData[userName]!![1].getLockedEsop()
        }
        fun getInventoryInfo(username: String): MutableList<Inventory> {
            val inventoryInfo: MutableList<Inventory> = mutableListOf(Inventory(type = EsopType.PERFORMANCE), Inventory(type = EsopType.NON_PERFORMANCE))
            inventoryInfo[0] = inventoryData[username]?.get(0)!!
            inventoryInfo[1] = inventoryData[username]?.get(1)!!

            return inventoryInfo
        }

    }
}
