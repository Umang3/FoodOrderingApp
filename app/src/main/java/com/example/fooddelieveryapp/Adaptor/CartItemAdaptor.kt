package com.example.fooddelieveryapp.Adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddelieveryapp.R
import com.example.fooddelieveryapp.model.FoodItem

class CartItemAdaptor(val context : Context, val orderList : ArrayList<FoodItem>): RecyclerView.Adapter<CartItemAdaptor.CartItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_details,parent,false)
        return CartItemViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val orderList1 = orderList[position]
        holder.txtOrderName.text = orderList1.name
        holder.txtOrderRate.text = orderList1.cost.toString()
    }

    class CartItemViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val txtOrderName : TextView = view.findViewById(R.id.txtOrderName)
        val txtOrderRate : TextView = view.findViewById(R.id.txtOrderRate)

    }
}