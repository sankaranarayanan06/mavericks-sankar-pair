package com.example.constants
import com.example.constants.Regex
import com.example.model.Inventory
import com.example.model.Order

val regex = Regex()
var inventoryList: MutableList<Inventory> = mutableListOf()
var inventorMap = HashMap<String, MutableList<Inventory>>()
const val maxQuantity = 100_00_000
val response = mutableMapOf<String, MutableList<String>>();
var orderList = mutableListOf<Order>()
var orderID = 0;
var transactions: MutableMap<Int, MutableList<Pair<Long, Long>>> = mutableMapOf()