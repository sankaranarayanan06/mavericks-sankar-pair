package com.example.model



import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class User(@field:NotEmpty var firstName: String,
           @field:NotNull @field:NotBlank @field:NotEmpty var lastName: String,
           @field:NotEmpty @field:NotBlank @field:NotNull var phoneNumber: String,
           @field:NotEmpty @field:NotBlank @field:NotNull var email: String, username: String) {
    @NotEmpty @NotBlank @NotNull
    var userName = username
}
