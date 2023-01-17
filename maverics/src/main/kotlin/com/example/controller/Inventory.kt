package com.example.controller

import com.example.model.Inventory

var inventoryMap= HashMap<String, Inventory>()
class Inventory(val user_id:Int){
    var free:Int = 0
        get() = field
        set(value) {field = value}

    var locked:Int = 0
        get() = field
        set(value) {field = value}
}