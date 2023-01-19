package com.example.model

data class OrderHistory (
  val orderId: Int,
  val price :Long ,
  val quantity :Long,
  val type : String ,
  val filled :MutableList<Transaction>
)
