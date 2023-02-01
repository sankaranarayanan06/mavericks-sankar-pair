package com.example.model


import java.time.LocalDateTime


class VestingData(var quantity: Long, var time: LocalDateTime, var esopType: String) {
    var timeValue = time.toString()

}
