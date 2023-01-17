package com.example.controller
import com.example.model.Wallet

class InventoryController(val username:String){
    var free:Int = 0
        get() = field
        set(value) {field = value}

    var locked:Int = 0
        get() = field
        set(value) {field = value}
}