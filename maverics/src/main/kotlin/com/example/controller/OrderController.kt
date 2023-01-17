package com.example.controller

import com.example.model.Order
import com.example.model.User
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject



@Controller("/user")
class OrderController {

    @Post("/register")
    fun register(@Body body: JsonObject): Order {

        var order :




        val firstName:String = body["firstName"].stringValue
        val lastName:String = body["lastName"].stringValue
        val phoneNumber:String = body["phoneNumber"].stringValue
        val email:String = body["email"].stringValue
        val username:String = body["username"].stringValue


        // check all fields
        // check if phonenumber, email and username are unique.
        return newUser
    }


}