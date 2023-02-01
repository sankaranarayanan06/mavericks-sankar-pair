package com.example.controller

import com.example.services.getPlatformFees
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import com.example.services.getPlatformFees

import java.math.BigInteger

@Controller("/")

class PlatformFees {
    @Get("/platformFee")
    fun totalPlatformCharge(): HttpResponse<*>{
        val response = mutableMapOf<String, BigInteger>()
        response["platformFee"] = getPlatformFees()
        return HttpResponse.ok(response)
    }
}
