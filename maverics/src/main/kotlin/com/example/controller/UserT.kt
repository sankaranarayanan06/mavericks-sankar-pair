package com.example.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post


@Controller("/user")
class UserT {

    @Get
    fun test(): String{
        return "sucess";
    }

}