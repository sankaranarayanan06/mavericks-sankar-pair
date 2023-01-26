package com.example.constants
import com.example.constants.Regex
import com.example.model.Inventory
import com.example.model.Order
import com.example.model.Transaction
import com.example.model.VestingData
import java.math.BigInteger

val regex = Regex()

var inventoryList: MutableList<Inventory> = mutableListOf()

var inventoryData = HashMap<String, MutableList<Inventory>>()

const val maxQuantity = 100_00_000
val response = mutableMapOf<String, MutableList<String>>();

var orderList = mutableListOf<Order>()

var orderID = 0;

var transactions: MutableMap<Int, MutableList<Transaction>> = mutableMapOf()

var vestings: MutableMap<String, MutableList<VestingData>> = mutableMapOf()

var vestingPercentages = mutableListOf<Long>(10,30,20,40)

var vestingTimings = mutableListOf<Long>(10,20,30,40)

var totalPlatformFees :BigInteger=BigInteger.valueOf(0)
