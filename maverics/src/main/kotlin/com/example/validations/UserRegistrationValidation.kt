package com.example.validations

import com.example.constants.allUsers
import com.example.constants.regex
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

fun validateName(name: String) = regex.firstLastNameRegex().toRegex().matches(name)

fun nullBoolean(variable: Any?) = variable == null


fun isUniquePhoneNumber(phoneNumber: String): Boolean {
    for (user in allUsers.keys) {
        if (allUsers[user]?.phoneNumber == phoneNumber) {
            return false
        }
    }
    return true
}

fun isUniqueEmail(email: String): Boolean {
    for (user in allUsers.keys) {
        if (allUsers[user]?.email == email) {
            return false
        }
    }
    return true
}

fun isUniqueUsername(username: String): Boolean {
    return !allUsers.containsKey(username)
}

fun isUserExists(username: String): Boolean {
    return allUsers.containsKey(username)
}

fun nullValidation(variable: Any?, variableName: String): MutableList<String> {
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
    errorResponseList += nullValidation(variable, validationName)
    if (errorResponseList.size != 0) {
        return errorResponseList
    }
    errorResponseList += typeValidation(variable, validationName)
    if (errorResponseList.size != 0) {
        return errorResponseList
    }
    val phoneNumber: String = variable!!.stringValue
    errorResponseList += emptyValidation(phoneNumber, validationName)
    if (errorResponseList.size != 0) {
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

    if (!isUniqueEmail(emailId)) {
        emailErrorValidationList.add("Email Already exists")
        return emailErrorValidationList
    }
    return emailErrorValidationList
}


// Phone Number
fun phoneNumberValidation(phoneNumber: JsonNode?): MutableList<String> {
    val phoneNumberErrorValidationList = mutableListOf<String>()
    phoneNumberErrorValidationList += fieldValidation(phoneNumber, "phoneNumber")
    if (phoneNumberErrorValidationList.size != 0) {
        return phoneNumberErrorValidationList
    }

    if (!checkPhoneNumber(phoneNumber!!.stringValue)) {
        phoneNumberErrorValidationList.add("Invalid phone number")
        return phoneNumberErrorValidationList
    }

    if (!isUniquePhoneNumber(phoneNumber.stringValue)) {
        phoneNumberErrorValidationList.add("User with given phone number already exists")
    }

    return phoneNumberErrorValidationList
}

// First Last Name
fun validateNames(name: JsonNode?, variableName: String): MutableList<String> {
    val firstLastNameErrorValidationList = mutableListOf<String>()
    firstLastNameErrorValidationList += fieldValidation(name, variableName)
    if (firstLastNameErrorValidationList.size != 0) {
        return firstLastNameErrorValidationList
    }

    if (validateName(name!!.stringValue)) {
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

    if (!isUniqueUsername(userName)) {
        userNameErrorValidationList.add("userName already exists")
    }

    return userNameErrorValidationList
}


fun registerValidation(
    userName: JsonNode?, firstName: JsonNode?, lastName: JsonNode?, phoneNumber: JsonNode?, email: JsonNode?
): MutableList<String> {
    val registerValidationResponse = mutableListOf<String>()
    registerValidationResponse += validateNames(firstName, "firstName")
    registerValidationResponse += validateNames(lastName, "lastName")
    registerValidationResponse += userNameValidation(userName)
    registerValidationResponse += emailValidation(email)
    registerValidationResponse += phoneNumberValidation(phoneNumber)
    return registerValidationResponse
}
