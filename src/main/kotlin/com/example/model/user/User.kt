package com.example.model.user

import com.example.model.inventory.Inventory
import com.example.model.wallet.Wallet

data class User(
    private var firstName: String,
    private var lastName: String,
    private var phoneNumber: String,
    private var email: String,
    private var userName: String
) {
    private var wallet = Wallet()
    private var inventory = Inventory()
    private var orderList = mutableListOf<String>()

    fun getFirstName(): String {
        return firstName
    }

    fun getLastName(): String {
        return lastName
    }

    fun getUserName(): String {
        return userName
    }

    fun getPhoneNumber(): String {
        return phoneNumber
    }

    fun getEmail(): String {
        return email
    }

    fun getWalletDetails(): Wallet {
        return wallet
    }

    fun getInventoryDetails(): Inventory {
        return inventory
    }

    fun getListOfOrders(): MutableList<String> {
        return orderList
    }
}
