package com.example.validations

import com.example.constants.maxInventoryQuantity
import com.example.model.Inventory

class InventoryValidation {
    fun validation(inventoryError: MutableList<String>, performance: Inventory, nonPerformance: Inventory, quantityToAdd: Long, type: String) {
        if (performance.free + performance.locked + nonPerformance.free + nonPerformance.locked > maxInventoryQuantity) {
            inventoryError.add("Quantity out of Range. Max: 100 Crore, Min: 1")
        }
        if (quantityToAdd !in 1..maxInventoryQuantity) {
            inventoryError.add("Amount out of Range. Max: 100 Crore, Min: 1")
        }
        if (performance.free + performance.locked + nonPerformance.free + nonPerformance.locked + quantityToAdd > maxInventoryQuantity) {
            inventoryError.add("Inventory limit of 100 crores exceeded")
        }
        if (type != "PERFORMANCE" && type != "NON_PERFORMANCE") {
            inventoryError.add("Wrong ESOP type")
        }
    }
}
