package com.example.controller

<<<<<<< HEAD:maverics/src/main/kotlin/com/example/controller/WalletController.kt
class WalletController(val username:String){
=======
import com.example.model.Inventory

var inventoryMap= HashMap<String, Inventory>()
class Inventory(val user_id:Int){
>>>>>>> e977f44 (Update data Structures for controllers):maverics/src/main/kotlin/com/example/controller/Inventory.kt
    var free:Int = 0
        get() = field
        set(value) {field = value}

    var locked:Int = 0
        get() = field
        set(value) {field = value}
}