package com.example.validations

import com.example.constants.Amounts
import com.example.model.Inventory

class InventoryValidation {
    fun validation(inventoryError: MutableList<String>, performance: Inventory, nonPerformance: Inventory, quantityToAdd: Long, type: String) {
        if (performance.free + performance.locked + nonPerformance.free + nonPerformance.locked > Amounts.MAX_INVENTORY_QUANTITY) {
            inventoryError.add("ESOP Quantity out of Range. Max: ${Amounts.MAX_INVENTORY_QUANTITY}, Min: 1")
        }
        if (quantityToAdd !in 1..Amounts.MAX_INVENTORY_QUANTITY) {
            inventoryError.add("ESOP Quantity out of Range. Max: ${Amounts.MAX_INVENTORY_QUANTITY}, Min: 1")
        }
        if (performance.free + performance.locked + nonPerformance.free + nonPerformance.locked + quantityToAdd > Amounts.MAX_INVENTORY_QUANTITY) {
            inventoryError.add("Inventory limit of ${Amounts.MAX_INVENTORY_QUANTITY} exceeded")
        }
        if (type != "PERFORMANCE" && type != "NON_PERFORMANCE") {
            inventoryError.add("Wrong ESOP type")
        }
    }
}
