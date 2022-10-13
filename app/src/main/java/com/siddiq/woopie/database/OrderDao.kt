package com.siddiq.woopie.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {

    @Insert
    fun insertOrder(orderEntity: OrderEntity)

    @Delete
    fun deleteOrder(orderEntity: OrderEntity)

    @Query("SELECT * FROM orders")
    fun getAllOrders(): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE order_id = :orderId")
    fun getOrderById(orderId: String): OrderEntity

    @Query("DELETE FROM orders")
    fun deleteAllOrders()

    @Query("SELECT SUM(order_price) FROM orders")
    fun getOrderPrice(): Int
}