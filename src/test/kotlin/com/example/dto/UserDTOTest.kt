package com.example.dto

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserDTOTest{
    @Test
    fun `it should accept a valid user`(){
        val user = UserDTO("sankar","m","sankar","sankar@sahaj.ai","7550276216")

        val errors = user.getErrors()

        println(errors)
        assertTrue(errors.isEmpty())
    }
}