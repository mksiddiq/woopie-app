package com.siddiq.woopie.model

import org.json.JSONArray

data class OrderHistoryDish(
    val orderId: Int,
    val resName: String,
    val orderDate: String,
    val foodItem: JSONArray,
)