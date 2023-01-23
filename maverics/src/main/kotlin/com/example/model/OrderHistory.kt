package com.example.model

data class BuyOrderHistory (
  val orderId: Int,
  val price :Long ,
  val quantity :Long,
  val type : String ,
  val staus: String="unfilled",
  val filled :MutableList<Transaction>
)

data class SellOrderHistory (
  val orderId: Int,
  val price :Long ,
  val quantity :Long,
  val type : String ,
  val esopTyoe: String? = null,
  val staus: String="unfilled",
  val filled :MutableList<Transaction>
)
