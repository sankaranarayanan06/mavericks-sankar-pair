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

var inventoryMap= HashMap<String, Inventory>()
class Inventory(val user_id:Int){
    var free:Int = 0
        get() = field
        set(value) {field = value}

    var locked:Int = 0
        get() = field
        set(value) {field = value}
}


var walletList = mutableMapOf<String,Wallet>()

@Controller("/user")
class WalletController(){
    @Post("/{username}/wallet")
    fun wallet(@PathVariable username: String, @Body body: JsonObject): HttpResponse<Wallet> {
        val amount:Int = body["amount"].intValue
        if(!walletList.containsKey(username)){
            val newWallet:Wallet = Wallet(username)
            walletList.put(username,newWallet)
        }
        val wallet: Wallet = walletList.get(username)!!
        wallet.freeAmount += amount
        return HttpResponse.ok(wallet)
    }
}