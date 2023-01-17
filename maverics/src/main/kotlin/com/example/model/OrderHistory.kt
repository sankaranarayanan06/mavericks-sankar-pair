package com.example.model

data class OrderHistory (
  val orderId: Int,
  val price :Int ,
  val quantity :Int,
  val type : String ,
  val filled :MutableList<Transaction>
)
