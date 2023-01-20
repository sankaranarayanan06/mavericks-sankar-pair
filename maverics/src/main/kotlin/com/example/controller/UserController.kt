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
import io.micronaut.validation.validator.constraints.EmailValidator
import javax.validation.constraints.Email
import io.micronaut.validation.validator.constraints.PatternValidator


fun isEmailValid(email: String): Boolean {
    var emailRegex = ("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")
    return emailRegex.toRegex().matches(email)
}

fun checkUserName(username: String): Boolean {
    println(username)
    return ("^[A-Za-z0-9_-]*$").toRegex().matches(username)
}

@Controller("/user")
class UserController {

    @Get
    fun test(): String {
        return "sucess";
    }

    fun checkFirstName(firstName: String): MutableList<String> {
        val checkuserlist = mutableListOf<String>()
        if (firstName== null) {
            checkuserlist.add("First Name is Required")
        }
        if (firstName.length <= 0) {
            checkuserlist.add("First Name cannot be empty")
        }
        return checkuserlist
    }

    @Post("/register")
    fun register(@Body body: JsonObject): HttpResponse<*> {
        var errorList = mutableListOf<String>()
        val errorResponse = mutableMapOf<String, MutableList<String>>();
        var firstName: String = ""
        var lastName: String = ""
        var phoneNumber: String = ""
        var email: String = ""
        var username: String = ""
        try {
            errorList += checkFirstName(body["firstName"].stringValue)

            if (body["lastName"] == null) {
                errorList.add("Last Name is Required")
            }

            try {
                lastName = body["lastName"].stringValue

                if (lastName.length <= 0) {
                    errorList.add("Last Name cannot be empty")
                }
            } catch (e: Exception) {
            }

            if (body["phoneNumber"] == null) {
                errorList.add("Phone number is Required")
            }

            try {
                phoneNumber = body["phoneNumber"].stringValue


                if (phoneNumber.length <= 0) {
                    errorList.add("Phone number cannot be empty")
                }

            } catch (e: Exception) {
            }

            if (body["email"] == null) {
                errorList.add("Email is required")
            }

            try {
                email = body["email"].stringValue

                if (email.length <= 0) {
                    errorList.add("Email cannot be empty")
                }

            } catch (e: Exception) {
            }


            if (isEmailValid(email) == false) {
                errorList.add("Email is not valid")
            }

            if (body["username"] == null) {
                errorList.add("Username is Requied")
            }

            try {
                username = body["username"].stringValue

                if (username.length <= 0) {
                    errorList.add("Username cannot be empty")
                }

            } catch (e: Exception) {
            }

            if (checkUserName(username) == false) {
                errorList.add("Invalid User Name")
            }

            errorResponse["errors"] = errorList
            if (errorList.size > 0) {
                return HttpResponse.badRequest(errorResponse)
            }

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
                var errorList = mutableListOf<String>()
                errorResponse["error"] = errorList;
                if (!isUserNameUnique) {
                    errorList.add("User with given username already exists");
                }

                if (!isPhoneNumberUnique) {
                    errorList.add("User with given phone number already exists");
                }

                if (!isEmailUnique) {
                    errorList.add("User with given email already exists");
                }

                return HttpResponse.badRequest(errorResponse);

            }


        } catch (e: Exception) {
            return HttpResponse.badRequest(e);
        }


    }

}
