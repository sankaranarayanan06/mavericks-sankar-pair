package com.example.model


import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class User(firstName: String, lastName: String, phoneNumber: String, email: String, username: String) {
    @NotEmpty
    var firstName = firstName
    @NotNull @NotBlank @NotEmpty
    var lastName = lastName
    @NotEmpty @NotBlank @NotNull
    var phoneNumber = phoneNumber
    @NotEmpty @NotBlank @NotNull
    var email:String = email
    @NotEmpty @NotBlank @NotNull
    var userName = username
}
