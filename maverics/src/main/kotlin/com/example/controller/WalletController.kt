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
import java.time.temporal.TemporalAmount
import java.util.stream.LongStream


var walletList = mutableMapOf<String,Wallet>()
const val maxWalletAmount:Long = 100_00_00_000

fun walletvalidation(walletAmount: Long, userAmount: Long): MutableList<String>{
    val walleterror = mutableListOf<String>()
    if (userAmount !in 1..maxWalletAmount) {
        walleterror.add("Amount out of Range. Max: 100 Crore, Min: 1")
    }
    if(walletAmount+userAmount > maxWalletAmount){
        walleterror.add("Max wallet limit of 100 Crores would be exceeded.")
    }
    return walleterror
}


@Controller("/user")
class WalletController() {
    @Post("/{username}/wallet")
    fun addMoneyInWallet(@PathVariable username: String, @Body body: JsonObject): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            val amount: Long = body["amount"].longValue
            val wallet: Wallet = walletList.get(username)!!
            val response = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>()
            errorList += walletvalidation(wallet.freeAmount, amount)
            if(errorList.size > 0){
                response["error"] = errorList;
                return HttpResponse.badRequest(response);
            }

            else{
                wallet.freeAmount += amount
                return HttpResponse.ok(Message("${amount} added to account."))
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
