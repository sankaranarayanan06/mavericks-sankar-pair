package com.example.services

import com.example.model.User
import com.example.validations.registerValidation
import javax.validation.constraints.Email

fun CreateUser(firstName: String, lastName: String, phoneNumber:String, email: String, userName: String): Boolean {
    val newUser = User(firstName, lastName, phoneNumber, email, userName)
    return true
}