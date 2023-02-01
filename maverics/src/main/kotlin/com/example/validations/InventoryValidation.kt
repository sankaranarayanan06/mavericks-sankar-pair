package com.example.validations

import com.example.constants.Limits
import com.example.model.Inventory
import java.math.BigInteger

class InventoryValidation {
    fun validation(
        inventoryError: MutableList<String>,
        performance: Inventory,
        nonPerformance: Inventory,
        quantityToAdd: BigInteger,
        type: String
    ) {
        if (performance.free + performance.locked + nonPerformance.free + nonPerformance.locked > Limits.MAX_INVENTORY_QUANTITY) {
            inventoryError.add("ESOPs Quantity out of Range. Max: ${Limits.MAX_INVENTORY_QUANTITY}, Min: 1")
        }
        if (quantityToAdd !in BigInteger.ONE..Limits.MAX_INVENTORY_QUANTITY) {
            inventoryError.add("ESOPs Quantity out of Range. Max: ${Limits.MAX_INVENTORY_QUANTITY}, Min: 1")
        }
        if (performance.free + performance.locked + nonPerformance.free + nonPerformance.locked + quantityToAdd > Limits.MAX_INVENTORY_QUANTITY) {
            inventoryError.add("Inventory limit of ${Limits.MAX_INVENTORY_QUANTITY} exceeded")
        }
        if (type != "PERFORMANCE" && type != "NON_PERFORMANCE") {
            inventoryError.add("Unknown ESOP type")
        }
    }
}
