package com.example.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.json.tree.JsonObject
import com.example.services.addPlatformCharge
import com.example.services.getPlatformFees
import java.math.BigInteger

@Controller("/")

class PlatformFees {
    @Get("/platformFee")
    fun totalPlatformCharge(): HttpResponse<*>{
        var response = mutableMapOf<String, BigInteger>()
        response["platformFee"] = getPlatformFees()
      return HttpResponse.ok(response)
    }
}
