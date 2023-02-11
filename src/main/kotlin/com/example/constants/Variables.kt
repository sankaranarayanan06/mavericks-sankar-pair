package com.example.constants

import com.example.model.history.Transaction
import com.example.model.inventory.Inventory
import com.example.model.order.Order
import com.example.model.user.User
import com.example.model.vesting.VestingData
import java.math.BigInteger

val regex = Regex()

var inventoryList: MutableList<Inventory> = mutableListOf()

var inventoryData = HashMap<String, MutableList<Inventory>>()

var orderList = mutableMapOf<Int, Order>()

var transactions: MutableMap<Int, MutableList<Transaction>> = mutableMapOf()

var vestings: MutableMap<String, MutableList<VestingData>> = mutableMapOf()

var vestingHistory: MutableMap<String, MutableList<VestingData>> = mutableMapOf()

var vestingPercentages = mutableListOf<Long>(30, 20, 10, 40)

var vestingTimings = mutableListOf<Long>(10, 20, 30, 40)

var totalPlatformFees: BigInteger = BigInteger.valueOf(0)

var allUsers: HashMap<String, User> = HashMap<String, User>()
