package com.example.controller

import com.example.model.Inventory
import com.example.model.User
import com.example.model.Wallet
import com.example.model.allUsers
import com.example.validations.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject


@Controller("/user")
class UserController {

    @Get
    fun test(): String{
        return "sucess";
    }

    @Post("/register")
    fun register(@Body body:JsonObject): HttpResponse<MutableList<String>>{
        val firstName:String = body["firstName"].stringValue
        val lastName:String = body["lastName"].stringValue
        val phoneNumber:String = body["phoneNumber"].stringValue
        val email:String = body["email"].stringValue
        val username:String = body["username"].stringValue
        var newUser = User(firstName, lastName, phoneNumber, email, username)
        var errorsBody = mutableListOf<String>();
        var successBody = mutableListOf<String>()

        var isUserNameUnique = UserValidation().ifUniqueUsername(username)
        var isEmailUnique = UserValidation().ifUniqueEmail(email)
        var isPhoneNumberUnique = UserValidation().ifUniquePhoneNumber(phoneNumber)

        if (isUserNameUnique && isEmailUnique && isPhoneNumberUnique) {
            allUsers.put(username, newUser)
            inventorMap.put(username, Inventory())
            walletList.put(username, Wallet())

            successBody.add("User added successfully");

           return HttpResponse.ok(successBody);
        } else {
            if (!isUserNameUnique) {
                errorsBody.add("User with given username already exists");
            }

            if (!isPhoneNumberUnique) {
                errorsBody.add("User with given phone number already exists");
            }

            if (!isEmailUnique) {
                errorsBody.add("User with given email already exists");
            }
        }

        return HttpResponse.badRequest(errorsBody);
    }


}