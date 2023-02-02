package com.example.constants

import java.math.BigInteger

class Limits {
    companion object {
        var MAX_ORDER_QUANTITY: BigInteger = BigInteger.valueOf(100_00_000)
        var MAX_ORDER_PRICE: BigInteger = BigInteger.valueOf(100_00_000)
        var MAX_INVENTORY_QUANTITY: BigInteger = BigInteger.valueOf(100_00_000)
        var MAX_WALLET_AMOUNT: BigInteger = BigInteger.valueOf(100_00_000)

        fun setMaxOrderQuantity(quantity: BigInteger) {
            MAX_ORDER_QUANTITY = quantity
        }

        fun setMaxOrderPrice(amount: BigInteger) {
            MAX_ORDER_PRICE = amount
        }

        fun setMaxInventoryQuantity(quantity: BigInteger) {
            MAX_INVENTORY_QUANTITY = quantity
        }

        fun setMaxWalletAmount(amount: BigInteger) {
            MAX_WALLET_AMOUNT = amount
        }
    }


}
