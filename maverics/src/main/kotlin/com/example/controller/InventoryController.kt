package com.example.controller

<<<<<<< HEAD:maverics/src/main/kotlin/com/example/controller/InventoryController.kt
class InventoryController(val username:String){
=======
import com.example.model.Wallet

var walletMap= HashMap<String,Wallet>()

class Wallet(val user_id:Int){
>>>>>>> e977f44 (Update data Structures for controllers):maverics/src/main/kotlin/com/example/controller/Wallet.kt
    var free:Int = 0
        get() = field
        set(value) {field = value}

    var locked:Int = 0
        get() = field
        set(value) {field = value}
}