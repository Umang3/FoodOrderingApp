package com.example.fooddelieveryapp.Adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddelieveryapp.R
import com.example.fooddelieveryapp.model.FoodItem
import com.example.fooddelieveryapp.model.OrderDetails
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdaptor(val context: Context , val orderHistory : ArrayList<OrderDetails>) : RecyclerView.Adapter<OrderHistoryAdaptor.OrderViewHolder>()  {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_history_custom_row,parent,false)
        return OrderViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return orderHistory.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderList = orderHistory[position]
        holder.orderHistoryResName.text = orderList.resName
        holder.txtOrderDate.text = formatDate(orderList.orderDate)

        val foodList = ArrayList<FoodItem>()
        for (i in 0 until orderList.foodItem.length()) {
            val orderJsonObject = orderList.foodItem.getJSONObject(i)
            foodList.add(
                FoodItem(
                    orderJsonObject.getString("food_item_id"),
                    orderJsonObject.getString("name"),
                    orderJsonObject.getString("cost").toInt()
                )
            )
        }

        val cartItemAdaptor =
            CartItemAdaptor(
                context,
                foodList
            )
        holder.recyclerOrderHistory.adapter = cartItemAdaptor
        val mLayoutManager = LinearLayoutManager(context)
        holder.recyclerOrderHistory.layoutManager = mLayoutManager
    }
    class OrderViewHolder(view: View): RecyclerView.ViewHolder(view){

        val orderHistoryResName : TextView = view.findViewById(R.id.orderHistoryResName)
        val txtOrderDate : TextView = view.findViewById(R.id.txtOrderDate)
       val recyclerOrderHistory : RecyclerView = view.findViewById(R.id.recyclerOrderHistory)
    }

    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }
}