package com.example.fooddelieveryapp.Adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddelieveryapp.R
import com.example.fooddelieveryapp.model.FoodItem

class RestaurantsMenuAdaptor(val context : Context,val menuList : ArrayList<FoodItem> , val listener : OnItemClickListener)
    : RecyclerView.Adapter<RestaurantsMenuAdaptor.MenuViewHolder>() {

    companion object{
        var isCartEmpty = true
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MenuViewHolder {

        val view = LayoutInflater.from(p0.context).inflate(R.layout.restaurant_food_item,p0,false)
        return MenuViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {

        return menuList.size
    }


    interface OnItemClickListener {

        fun onAddItemClick(foodItem : FoodItem)
        fun onRemoveItemClick(foodItem: FoodItem)
    }


    override fun onBindViewHolder(p0: MenuViewHolder, p1: Int) {
        val menuObject = menuList[p1]
        p0.txtMenuName.text = menuObject.name
        val cost = "Rs. ${menuObject.cost?.toString()}"
        p0.txtCost.text = cost
        p0.txtSerialNo.text = (p1 + 1).toString()
        p0.btnAddToFavourites.setOnClickListener {
            p0.btnAddToFavourites.visibility = View.GONE
            p0.btnRemove.visibility = View.VISIBLE
            listener.onAddItemClick(menuObject)
        }

        p0.btnRemove.setOnClickListener {
            p0.btnRemove.visibility = View.GONE
            p0.btnAddToFavourites.visibility = View.VISIBLE
            listener.onRemoveItemClick(menuObject)
        }


    }

    class MenuViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val txtSerialNo : TextView = view.findViewById(R.id.txtSerialNo)
        val txtMenuName : TextView = view.findViewById(R.id.txtMenuName)
        val txtCost : TextView = view.findViewById(R.id.txtCost)
        val btnAddToFavourites : Button = view.findViewById(R.id.btnAddToFavourites)
        val btnRemove : Button = view.findViewById(R.id.btnRemove)

    }

}