package com.example.model

var allUsers : HashMap<String, User> = HashMap<String, User>()
class User(firstName: String, lastName: String, phoneNumber: String, email: String, username: String) {
    var firstName = firstName
    var lastName = lastName
    var phoneNumber = phoneNumber
    var email = email
    var userName = username
}