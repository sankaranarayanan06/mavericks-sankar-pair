package com.example.controller

import com.example.constants.inventorMap
import com.example.constants.regex
import com.example.model.Inventory
import com.example.model.User
import com.example.model.Wallet
import com.example.model.allUsers
import com.example.model.*
import com.example.validations.user.UserValidation
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject


fun isEmailValid(email: String): Boolean {
    return regex.getEmailRegex().toRegex().matches(email)
}

fun checkUserName(username: String): Boolean {
    return  !regex.getUsernameRegex().toRegex().matches(username)
}
fun checkPhoneNumber(phoneNumber:String):Boolean{
    return regex.getPhoneNumberRegex().toRegex().matches(phoneNumber)
}
@Controller("/user")
class UserController {
    @Post("/register")
    fun register(@Body body: JsonObject): HttpResponse<*> {
        val errorList = mutableListOf<String>()
        val errorResponse = mutableMapOf<String, MutableList<String>>()
        val firstName = ""
        var lastName = ""
        var phoneNumber = ""
        var email = ""
        var username = ""
        try {
            errorList += UserValidation.checkFirstName(body["firstName"].stringValue)

            if (body["lastName"] == null) {
                errorList.add("Last Name is Required")
            }

            try {
                lastName = body["lastName"].stringValue

                if (lastName.isEmpty()) {
                    errorList.add("Last Name cannot be empty")
                }
            } catch (e: Exception) {
            }

            if (body["phoneNumber"] == null) {
                errorList.add("Phone number is Required")
            }

            try {
                phoneNumber = body["phoneNumber"].stringValue


                if (phoneNumber.isEmpty()) {
                    errorList.add("Phone number cannot be empty")
                }

            } catch (e: Exception) {
            }

            if (body["email"] == null) {
                errorList.add("Email is required")
            }

            try {
                email = body["email"].stringValue

                if (email.isEmpty()) {
                    errorList.add("Email cannot be empty")
                }

            } catch (e: Exception) {
            }


            if (!isEmailValid(email)) {
                errorList.add("Email is not valid")
            }

            if (body["username"] == null) {
                errorList.add("Username is Required")
            }

            try {
                username = body["username"].stringValue

                if (username.isEmpty()) {
                    errorList.add("Username cannot be empty")
                }

            } catch (e: Exception) {
            }

            if (!checkUserName(username)) {
                errorList.add("Invalid User Name")
            }
            if (!checkPhoneNumber(phoneNumber)) {
                errorList.add("Invalid phone number")
            }
            errorResponse["errors"] = errorList
            if (errorList.size > 0) {
                return HttpResponse.badRequest(errorResponse)
            }

            val newUser = User(firstName, lastName, phoneNumber, email, username)
            val successBody = mutableListOf<String>()
            val isUserNameUnique = UserValidation.ifUniqueUsername(username)
            val isEmailUnique = UserValidation.ifUniqueEmail(email)
            val isPhoneNumberUnique = UserValidation.ifUniquePhoneNumber(phoneNumber)

            if (isUserNameUnique && isEmailUnique && isPhoneNumberUnique) {
                allUsers[username] = newUser
                inventorMap[username] = mutableListOf(Inventory(type = "PERFORMANCE"), Inventory(type = "NON_PERFORMANCE"))
                walletList[username] = Wallet()
                successBody.add("User added successfully");
                return HttpResponse.ok(successBody);
            } else {
                val errorList = mutableListOf<String>()
                errorResponse["error"] = errorList
                if (!isUserNameUnique) {
                    errorList.add("User with given username already exists")
                }

                if (!isPhoneNumberUnique) {
                    errorList.add("User with given phone number already exists")
                }

                if (!isEmailUnique) {
                    errorList.add("User with given email already exists")
                }

                return HttpResponse.badRequest(errorResponse);

            }


        } catch (e: Exception) {
            e.printStackTrace()
            errorResponse["error"] = (listOf("An unknown error occurred.").toMutableList())
            return HttpResponse.badRequest(errorResponse);
        }
    }
}
