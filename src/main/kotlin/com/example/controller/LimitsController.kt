package com.example.controller

import com.example.validations.LimitsValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject

@Controller("/limits")
class LimitsController {
    @Post("/")
    fun setLimits(@Body body: JsonObject): HttpResponse<*> {

        return LimitsValidation.validateLimits(body)
    }

}
