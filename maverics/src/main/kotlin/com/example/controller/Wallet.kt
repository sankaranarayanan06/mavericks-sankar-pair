package com.example.controller

import com.example.model.Wallet

var walletMap= HashMap<String,Wallet>()

class Wallet(val user_id:Int){
    var free:Int = 0
        get() = field
        set(value) {field = value}

    var locked:Int = 0
        get() = field
        set(value) {field = value}
}