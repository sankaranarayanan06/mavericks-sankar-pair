package com.example.controller

import com.example.services.getPlatformFees
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import java.math.BigInteger

@Controller("/")

class PlatformFees {
    @Get("/platformFee")
    fun totalPlatformCharge(): HttpResponse<*> {
        var response = mutableMapOf<String, BigInteger>()
        response["platformFee"] = getPlatformFees()
        return HttpResponse.ok(response)
    }
}