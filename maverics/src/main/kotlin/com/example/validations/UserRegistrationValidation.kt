package com.example.validations

import com.example.constants.regex
import com.example.validations.user.UserValidation
import io.micronaut.json.tree.JsonNode


fun isEmailValid(email: String): Boolean {
    if(regex.getEmailRegex().toRegex().matches(email)){
        val splitFrom = "@"
        val emailParts = email.split(splitFrom)
        if (emailParts[0].length > 64 || emailParts[1].length > 255) {
            return false
        }
        return true
    }
    return false
}

fun checkUserName(username: String) = regex.getUsernameRegex().toRegex().matches(username)

fun checkPhoneNumber(phoneNumber: String) = regex.getPhoneNumberRegex().toRegex().matches(phoneNumber)

fun firstLastName(flname: String) = regex.firstLastNameRegex().toRegex().matches(flname)

fun nullBoolean(variable: Any?) = variable == null

fun nullvalidation(variable: Any?, variableName: String): MutableList<String> {
    val nullValidationResponse = mutableListOf<String>()
    if (nullBoolean(variable)) {
        nullValidationResponse.add("$variableName Cannot be null")
    }
    return nullValidationResponse
}

fun typeValidation(variable: Any?, variableName: String?): MutableList<String> {
    val typeErrorResponse = mutableListOf<String>()
    try {
        variable.toString()
    } catch (_: Exception) {
        typeErrorResponse.add("$variableName should be String")
    }

    return typeErrorResponse
}

fun emptyBoolean(variable: String): Boolean {
    if (variable == "") {
        println("Its empty")
    }
    return variable == ""
}

fun emptyValidation(variable: String, variableName: String): MutableList<String> {
    val emptyValidationResponse = mutableListOf<String>()
    if (emptyBoolean(variable)) {
        emptyValidationResponse.add("$variableName cannot be empty")
    }
    return emptyValidationResponse
}


fun fieldValidation(variable: JsonNode?, validationName: String): MutableList<String> {
    val errorResponseList = mutableListOf<String>()
    errorResponseList += nullvalidation(variable, validationName)
    if (errorResponseList.size != 0) {
        println("null fail")
        return errorResponseList
    }
    errorResponseList += typeValidation(variable, validationName)
    if (errorResponseList.size != 0) {
        println("type fail")
        return errorResponseList
    }
    val phoneNumber: String = variable!!.stringValue
    errorResponseList += emptyValidation(phoneNumber, validationName)
    if (errorResponseList.size != 0) {
        println("empty fail")
        return errorResponseList
    }
    return errorResponseList
}


// Email Validation
fun emailValidation(email: JsonNode?): MutableList<String> {
    val emailErrorValidationList = mutableListOf<String>()
    emailErrorValidationList += fieldValidation(email, "email")
    if (emailErrorValidationList.size != 0) {
        return emailErrorValidationList
    }

    val emailId = email!!.stringValue
    if (!isEmailValid(emailId)) {
        emailErrorValidationList.add("Email is not valid")
        return emailErrorValidationList
    }

    if (!UserValidation.ifUniqueEmail(emailId)) {
        emailErrorValidationList.add("Email Already exists")
        return emailErrorValidationList
    }
    return emailErrorValidationList
}


// Phone Number
fun phoneNumberValidation(phonenumber: JsonNode?): MutableList<String> {
    val phoneNumberErrorValidationList = mutableListOf<String>()
    phoneNumberErrorValidationList += fieldValidation(phonenumber, "phoneNumber")
    if (phoneNumberErrorValidationList.size != 0) {
        return phoneNumberErrorValidationList
    }

    val phoneNumber = phonenumber!!.stringValue
    if (!checkPhoneNumber(phoneNumber)) {
        phoneNumberErrorValidationList.add("Invalid phone number")
        return phoneNumberErrorValidationList
    }

    if (!UserValidation.ifUniquePhoneNumber(phoneNumber)) {
        phoneNumberErrorValidationList.add("User with given phone number already exists")
    }

    return phoneNumberErrorValidationList
}

// First Last Name
fun firstLastNameValidation(flname: JsonNode?, variableName: String): MutableList<String> {
    val firstLastNameErrorValidationList = mutableListOf<String>()
    firstLastNameErrorValidationList += fieldValidation(flname, variableName)
    if (firstLastNameErrorValidationList.size != 0) {
        return firstLastNameErrorValidationList
    }

    if (firstLastName(flname!!.stringValue)) {
        firstLastNameErrorValidationList.add("$variableName is not valid")
    }
    return firstLastNameErrorValidationList
}

// Username
fun userNameValidation(username: JsonNode?): MutableList<String> {
    val userNameErrorValidationList = mutableListOf<String>()
    userNameErrorValidationList += fieldValidation(username, "userName")
    if (userNameErrorValidationList.size != 0) {
        return userNameErrorValidationList
    }

    val userName = username!!.stringValue
    if (checkUserName(userName)) {
        userNameErrorValidationList.add("Invalid userName")
        return userNameErrorValidationList
    }

    if (!UserValidation.ifUniqueUsername(userName)) {
        userNameErrorValidationList.add("userName already exists")
    }

    return userNameErrorValidationList
}


fun registerValidation(
    username: JsonNode?,
    firstname: JsonNode?,
    lastname: JsonNode?,
    phonenumber: JsonNode?,
    email: JsonNode?
): MutableList<String> {
    val registerValidationResponse = mutableListOf<String>()
    registerValidationResponse += firstLastNameValidation(firstname, "firstName")
    registerValidationResponse += firstLastNameValidation(lastname, "lastName")
    registerValidationResponse += userNameValidation(username)
    registerValidationResponse += emailValidation(email)
    registerValidationResponse += phoneNumberValidation(phonenumber)
    return registerValidationResponse
}



