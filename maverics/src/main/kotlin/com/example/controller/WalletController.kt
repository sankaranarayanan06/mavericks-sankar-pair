package com.example.controller
import com.example.model.Order
import com.example.model.Wallet
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.json.tree.JsonObject
import com.example.model.Inventory
import com.example.model.Message
import com.example.validations.UserValidation
import io.micronaut.json.tree.JsonArray


var walletList = mutableMapOf<String,Wallet>()
const val maxWalletAmount:Long = 100_00_00_000

@Controller("/user")
class WalletController() {
    @Post("/{username}/wallet")
    fun addMoneyInWallet(@PathVariable username: String, @Body body: JsonObject): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            val amount: Long = body["amount"].longValue
            if (amount in 1..maxWalletAmount) {
                val wallet: Wallet = walletList.get(username)!!
                wallet.freeAmount += amount
                return HttpResponse.ok(Message("${amount} added to account."))
            } else {
                val response = mutableMapOf<String, MutableList<String>>();
                var errorList = mutableListOf<String>("Amount out of Range. Max: 100 Crore, Min: 1")
                response["error"] = errorList;
                return HttpResponse.badRequest(response);
            }

        } else {
            val response = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>("User doesn't exist.")
            response["error"] = errorList;

            return HttpResponse.badRequest(response);
        }
    }


    @Get("/{username}/wallet")
    fun getWalletBalance(@PathVariable username: String): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            val userWallet = walletList[username];

            val response = mutableMapOf<String, Long>();

            if (userWallet != null) {
                response["Free Balance"] = userWallet.freeAmount
            };

            if (userWallet != null) {
                response["Locked Balance"] = userWallet.lockedAmount
            };

            return HttpResponse.ok(response)
        } else {
            val response = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>("User doesn't exist.")
            response["error"] = errorList;

            return HttpResponse.badRequest(response);
        }
    }
}
