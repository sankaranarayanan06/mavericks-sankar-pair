package com.example.validations

import com.example.constants.Limits
import io.micronaut.http.HttpResponse
import io.micronaut.json.tree.JsonObject

var limitsError: MutableList<String> = mutableListOf()

class LimitsValidation {
    companion object {
        fun validateLimits(body: JsonObject): HttpResponse<*> {
            if (body["maxOrderPrice"] == null && body["maxOrderQuantity"] == null && body["maxInventoryPrice"] == null && body["maxInventoryQuantity"] == null) {
                limitsError.add("Invalid Key Entered. Valid Keys: [maxOrderPrice, maxOrderQuantity, maxWalletAmount, maxInventoryQuantity]")
                val response = mutableMapOf<String, MutableList<String>>()

                response["errors"] = limitsError
                return HttpResponse.ok(response)
            }

            if(body["maxOrderPrice"] != null) {
                limitsError.add("maxOrderPrice")
                Limits.setMaxOrderPrice(body["maxOrderPrice"]!!.bigIntegerValue)
            }

            if(body["maxOrderQuantity"] != null) {
                limitsError.add("maxOrderQuantity")
                Limits.setMaxOrderPrice(body["maxOrderQuantity"]!!.bigIntegerValue)
            }

            if(body["maxInventoryQuantity"] != null) {
                limitsError.add("maxInventoryQuantity")
                Limits.setMaxOrderPrice(body["maxInventoryQuantity"]!!.bigIntegerValue)
            }

            if(body["maxWalletAmount"] != null) {
                limitsError.add("maxWalletAmount")
                Limits.setMaxOrderPrice(body["maxWalletAmount"]!!.bigIntegerValue)
            }

            val response = mutableMapOf<String, MutableList<String>>()

            response["updates"] = limitsError

            return HttpResponse.ok(response)
        }
    }
}
