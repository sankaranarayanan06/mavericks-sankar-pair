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

@Controller("/user")
class WalletController(){
    @Post("/{username}/addMoney")
    fun addMoneyInWallet(@PathVariable username: String, @Body body: JsonObject): HttpResponse<*> {
        if(UserValidation.isUserExist(username)){
            val amount:Int = body["amount"].intValue
            val wallet: Wallet = walletList.get(username)!!
            wallet.freeAmount += amount

            return HttpResponse.ok(Message("${amount} added to account."))
        }else{
            val response = mutableMapOf<String, MutableList<String>>();
            var errorList = mutableListOf<String>("User doesn't exist.")
            response["error"] = errorList;

            return HttpResponse.badRequest(response);
        }
    }

    @Get("/{username}/walletBalance")
    fun getWalletBalance(@PathVariable username: String): HttpResponse<*> {
        if (UserValidation.isUserExist(username)) {
            val userWallet = walletList[username];

            val response = mutableMapOf<String, Int>();

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