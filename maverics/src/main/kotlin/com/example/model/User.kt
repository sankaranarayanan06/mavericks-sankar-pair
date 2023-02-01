package com.example.model


import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class User(@field:NotEmpty var firstName: String,
           @field:NotNull @field:NotBlank @field:NotEmpty var lastName: String,
           @field:NotEmpty @field:NotBlank @field:NotNull var phoneNumber: String,
           @field:NotEmpty @field:NotBlank @field:NotNull var email: String,
           @field:NotEmpty @field:NotBlank @field:NotNull var username: String
) {
}
