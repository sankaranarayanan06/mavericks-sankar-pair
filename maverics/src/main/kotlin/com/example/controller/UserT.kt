package com.example.controller

import com.example.model.User
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.databind.util.JSONPObject
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject


@Controller("/user")
class UserT {

    @Get
    fun test(): String{
        return "sucess";
    }

    @Post("/register")
    fun register(@Body body:JsonObject):User{
        val firstName:String = body["firstName"].stringValue
        val lastName:String = body["lastName"].stringValue
        val phoneNumber:String = body["phoneNumber"].stringValue
        val email:String = body["email"].stringValue
        val username:String = body["username"].stringValue
        var newUser=User(firstName, lastName, phoneNumber, email, username)

        // check all fields
        // check if phonenumber, email and username are unique.
        return newUser
    }


}