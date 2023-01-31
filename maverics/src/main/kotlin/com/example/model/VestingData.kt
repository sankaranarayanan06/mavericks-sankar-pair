package com.example.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class VestingData(quantity: Long, time: LocalDateTime, esopType: String) {
    var quantity: Long = quantity
    var time: LocalDateTime = time
    var esopType: String = esopType
    var timeValue = time.toString()

}
