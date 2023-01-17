package com.example.validations

import com.example.model.allUsers

class UserValidation {

    fun ifUniqueUsername(username: String): Boolean {
        if (allUsers.containsKey(username)) {
            return false
        }

        return true
    }

    fun ifUniqueEmail(email: String): Boolean {
        for(user in allUsers.keys) {
            if (allUsers[user]?.email == email) {
                return false
            }
        }

        return true
    }

    fun ifUniquePhoneNumber(phoneNumber: String): Boolean {
        for(user in allUsers.keys) {
            if (allUsers[user]?.phoneNumber == phoneNumber) {
                return false
            }
        }

        return true
    }
    companion object {
        fun isUserExist(username: String): Boolean {
            if (allUsers.containsKey(username)) {
                return true;
            }

            return false;
        }
    }

}