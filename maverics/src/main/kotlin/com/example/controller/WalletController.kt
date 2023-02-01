package com.example.controller

import com.example.model.Wallet
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject
import com.example.model.Message
import com.example.services.performESOPVestings
import com.example.validations.WalletValidation
import com.example.validations.isUniqueUsername
import com.example.validations.isUserExists
import java.math.BigInteger


var walletList = mutableMapOf<String, Wallet>()


@Controller("/user")
class WalletController {
    @Post("/{username}/wallet")
    fun addMoneyInWallet(@PathVariable username: String, @Body body: JsonObject): HttpResponse<*> {
        if (isUserExists(username)) {
            performESOPVestings(username)
            val walletValidation = WalletValidation()
            var amount: BigInteger
            try {
                amount = body["amount"]!!.bigIntegerValue
            } catch (e: Exception) {
                val response = mutableMapOf<String, MutableList<String>>()
                response["error"] = mutableListOf("Please enter amount(Number)")
                return HttpResponse.ok(response)
            }
            val wallet: Wallet = walletList[username]!!
            val errorList = mutableListOf<String>()
            errorList += walletValidation.validations(wallet.freeAmount, amount)
            if (errorList.size > 0) {
                val response = mutableMapOf<String, MutableList<String>>()
                response["error"] = errorList
                return HttpResponse.ok(response)
            }

            wallet.freeAmount += amount
            return HttpResponse.ok(Message("$amount added to account."))

        } else {
            val response = mutableMapOf<String, MutableList<String>>()
            response["error"] = mutableListOf("User doesn't exist.")
            return HttpResponse.badRequest(response)
        }
    }


    @Get("/{username}/wallet")
    fun getWalletBalance(@PathVariable username: String): HttpResponse<*> {
        if (isUniqueUsername(username)) {
            val userWallet = walletList[username]

            val response = mutableMapOf<String, BigInteger>()

            if (userWallet != null) {
                response["Free Balance"] = userWallet.freeAmount
            }

            if (userWallet != null) {
                response["Locked Balance"] = userWallet.lockedAmount
            }

            return HttpResponse.ok(response)
        } else {
            val response = mutableMapOf<String, MutableList<String>>()
            val errorList = mutableListOf("User doesn't exist")
            response["error"] = errorList

            return HttpResponse.badRequest(response)
        }
    }
}
