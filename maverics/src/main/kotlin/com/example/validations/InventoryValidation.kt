package com.example.validations

import com.example.constants.maxQuantity
import com.example.model.Inventory

class InventoryValidation {

    fun validation(inventoryError: MutableList<String>, performance: Inventory, nonPerformance: Inventory, quantityToAdd: Long, type: String) {
        if (performance.free + performance.locked + nonPerformance.free + nonPerformance.locked > maxQuantity) {
            inventoryError.add("Quantity out of Range. Max: 10 Million, Min: 1")
        }
        if (quantityToAdd !in 1..maxQuantity) {
            inventoryError.add("Amount out of Range. Max: 10 Million, Min: 1")
        }
        if (performance.free + performance.locked + nonPerformance.free + nonPerformance.locked + quantityToAdd > maxQuantity) {
            inventoryError.add("Inventory limit of 10 Million exceeded")
        }
        if (type != "PERFORMANCE" && type != "NON_PERFORMANCE") {
            inventoryError.add("Wrong ESOP type")
        }
    }
}