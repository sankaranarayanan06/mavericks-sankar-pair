package com.example.constants

class Regex {
    fun getEmailRegex(): String {
        return "([a-zA-Z0-9]+([+._-]?[a-zA-z0-9])*)[@]([a-zA-Z]+([-]?[a-zA-z0-9])+[.])+[a-zA-Z]{2,}"
    }

    fun getUsernameRegex(): String {
        return "(@#$%^*/!&|.)$"
    }

    fun firstLastNameRegex(): String {
        return "\\s+"
    }

    fun getPhoneNumberRegex(): String {
        return "^[+]?[(]?[0-9]{3}[)]?[-s.]?[0-9]{3}[-s.]?[0-9]{4,6}$"
    }
}