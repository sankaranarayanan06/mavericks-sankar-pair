package com.example.validations

import com.example.model.allUsers

class UserValidation {
    companion object {
        fun ifUniquePhoneNumber(phoneNumber: String): Boolean {
            for (user in allUsers.keys) {
                if (allUsers[user]?.phoneNumber == phoneNumber) {
                    return false
                }
            }
            return true
        }

        fun isUserExist(username: String): Boolean {
            return allUsers.containsKey(username)
        }

        fun ifUniqueEmail(email: String): Boolean {
            for (user in allUsers.keys) {
                if (allUsers[user]?.email == email) {
                    return false
                }
            }
            return true
        }


        fun checkFirstName(firstName: String): List<String> {
            return checkFirstNameExists(firstName) + checkFirstNameNotTooBig(firstName)
        }

        private fun checkFirstNameNotTooBig(firstName: String): List<String> {
            return if(firstName.length > 50) {
                listOf("First name should be less than 50 characters")
            } else listOf()
        }

        private fun checkFirstNameExists(firstName: String): List<String> {
            return if(firstName.isEmpty()) {
                listOf("Username cannot be empty")
            } else listOf()
        }

        fun ifUniqueUsername(username: String): Boolean {
            return !allUsers.containsKey(username)
        }
    }
}
