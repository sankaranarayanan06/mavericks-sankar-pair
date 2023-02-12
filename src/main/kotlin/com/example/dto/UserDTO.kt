package com.example.dto

import org.jetbrains.annotations.NotNull
import javax.validation.constraints.NotBlank

data class UserDTO(
    @field:NotNull @field:NotBlank val firstName:String,
    @field:NotNull @field:NotBlank val lastName:String,
    @field:NotNull @field:NotBlank val userName:String,
    @field:NotNull @field:NotBlank val email:String,
    @field:NotNull @field:NotBlank val phoneNumber:String
){
    private val errors = mutableListOf<String>()

    init {
        validateUserName()?.let { it -> errors.add(it) }

    }

    fun getErrors(): MutableList<String> {
        return errors
    }
    private fun validateUserName(): String? {
        val error = mutableListOf<String>()
        if(userName.contains("^(?=.{4,32}\$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])\$")){
            return null
        }
        return("Invalid userName: userName can contain only alphabets,underscores and period." +
                "\n\tuserName can contain 4-32 characters and cannot start or end with _ and .")
    }
}
