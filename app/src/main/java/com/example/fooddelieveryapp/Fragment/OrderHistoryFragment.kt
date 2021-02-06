package com.example.fooddelieveryapp.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddelieveryapp.Activity.LoginActivity.Companion.userId
import com.example.fooddelieveryapp.Adaptor.OrderHistoryAdaptor
import com.example.fooddelieveryapp.R
import com.example.fooddelieveryapp.model.OrderDetails

import kotlin.collections.HashMap


class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrderHistoryFragment : RecyclerView
    lateinit var orderHistoryAdaptor: OrderHistoryAdaptor
    var orderHistory = arrayListOf<OrderDetails>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerOrderHistoryFragment = view.findViewById(R.id.recyclerOrderHistoryFragment)

        val url = "http://13.235.250.119/v2/orders/fetch_result/"
        val queue = Volley.newRequestQueue(context)

        val userId = userId
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET,url + userId,null, Response.Listener {

            val data = it.getJSONObject("data")
            val success = data.getBoolean("success")
            if(success){
                val resArray = data.getJSONArray("data")
                for(i in 0 until resArray.length()){
                    val orderObject = resArray.getJSONObject(i)
                    val orders = OrderDetails(
                        orderObject.getInt("order_id"),
                        orderObject.getString("restaurant_name"),
                        orderObject.getString("order_placed_at"),
                        orderObject.getJSONArray("food_items")
                    )
                    orderHistory.add(orders)
                    orderHistoryAdaptor =
                        OrderHistoryAdaptor(
                            activity as Context,
                            orderHistory
                        )
                    val mLayoutManager = LinearLayoutManager(activity)
                    recyclerOrderHistoryFragment.adapter = orderHistoryAdaptor
                    recyclerOrderHistoryFragment.layoutManager = mLayoutManager
                }

            }

            else{
                Toast.makeText(activity as Context,"Some error occurred", Toast.LENGTH_SHORT).show()
            }

        } , Response.ErrorListener {

            Toast.makeText(activity as Context,"Volley error occurred", Toast.LENGTH_SHORT).show()

        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "25c5be62209ad2"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
        return view

    }



}