package com.example.controller
import com.example.constants.response
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
import com.example.validations.user.UserValidation
import com.example.validations.WalletValidation


var walletList = mutableMapOf<String,Wallet>()


@Controller("/user")
class WalletController() {
    @Post("/{username}/wallet")
    fun addMoneyInWallet(@PathVariable username: String, @Body body: JsonObject): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            performESOPVestings(username)
            val walletValidation = WalletValidation()
            var amount: Long = 0L
            try {
                amount = body["amount"].longValue
            } catch (e: Exception) {
                response["error"] = mutableListOf<String>("Please enter amount(Number)")
                return HttpResponse.ok(response)
            }
            val wallet: Wallet = walletList.get(username)!!
            var errorList = mutableListOf<String>()
            errorList += walletValidation.validations(wallet.freeAmount, amount)
            if(errorList.size > 0){
                response["error"] = errorList;
                return HttpResponse.ok(response);
            }
            else{
                wallet.freeAmount += amount
                return HttpResponse.ok(Message("${amount} added to account."))
            }
        } else {
            response["error"] = mutableListOf<String>("User doesn't exist.")
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
