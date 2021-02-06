package com.example.fooddelieveryapp.database

import androidx.room.*

@Dao
interface OrderDao {

    @Insert
    fun insertOrder(orderEntity: OrderEntity)

    @Delete
    fun deleteOrder(orderEntity: OrderEntity)
    @Query("SELECT * FROM orders")
    fun getAllOrders() : List<OrderEntity>

    @Query("DELETE FROM orders WHERE resId = :resId")
    fun deleteOrders(resId : String)

}