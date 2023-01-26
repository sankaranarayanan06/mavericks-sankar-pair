package com.example.model

import java.time.LocalDateTime
import java.time.LocalTime

class VestingData(quantity: Long, time: LocalDateTime, esopType: String) {
    var quantity: Long = 0
    var time: LocalDateTime = time
    var esopType: String = esopType

}
