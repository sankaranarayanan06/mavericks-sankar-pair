package com.example.services

import com.example.constants.allUsers


fun getUserInfo(username: String): MutableMap<String, Any> {
    val user = allUsers[username]

    val userData: MutableMap<String, Any> = mutableMapOf<String, Any>()

    userData["firstName"] = user!!.getFirstName()
    userData["lastName"] = user.getLastName()
    userData["phoneNumber"] = user.getPhoneNumber()
    userData["email"] = user.getEmail()
    return userData
}
