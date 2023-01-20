package com.example.model

data class OrderHistory (
  val orderId: Int,
  val price :Long ,
  val quantity :Long,
  val type : String ,
  val esopTyoe: String? = null,
  val filled :MutableList<Transaction>
)
