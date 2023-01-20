package com.example.model

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable


class Wallet(var freeAmount:Long = 0, var lockedAmount:Long = 0) {
}

//max : 1000000000