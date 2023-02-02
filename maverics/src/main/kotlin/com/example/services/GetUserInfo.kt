package com.example.services

import com.example.constants.allUsers


fun getUserInfo(username: String): MutableMap<String, Any> {
    val user = allUsers[username]

    val userData: MutableMap<String, Any> = mutableMapOf<String, Any>()

    userData["firstName"] = user!!.firstName
    userData["lastName"] = user.lastName
    userData["phoneNumber"] = user.phoneNumber
    userData["email"] = user.email
    return userData
}
