package com.example.fooddelieveryapp.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddelieveryapp.Activity.CartActivity
import com.example.fooddelieveryapp.R
import com.example.fooddelieveryapp.Adaptor.RestaurantsMenuAdaptor
import com.example.fooddelieveryapp.database.OrderEntity
import com.example.fooddelieveryapp.database.RestaurantDatabase
import com.example.fooddelieveryapp.model.FoodItem
import com.google.gson.Gson


class RestaurantFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var resId: Int? = 0
        var resName: String? = ""
    }

    lateinit var txtRestaurantName : TextView
    lateinit var recyclerMenuItems : RecyclerView
    lateinit var btnGoToCart : Button
    lateinit var rlLoading : RelativeLayout
    lateinit var restaurantProgressBar :ProgressBar
    lateinit var restaurantsMenuAdaptor: RestaurantsMenuAdaptor
    var menuList = arrayListOf<FoodItem>()
    var orderList = arrayListOf<FoodItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_restaurant, container, false)

        resId = arguments?.getInt("id",0)
        resName = arguments?.getString("name","")

        txtRestaurantName = view.findViewById(R.id.txtRestaurantName)
        recyclerMenuItems = view.findViewById(R.id.recyclerMenuItems)
        btnGoToCart = view.findViewById(R.id.btnGoToCart)
        rlLoading = view.findViewById(R.id.rlLoading)
        restaurantProgressBar = view.findViewById(R.id.restaurantProgressBar)

        restaurantProgressBar.visibility = View.VISIBLE
        btnGoToCart.visibility = View.GONE
        btnGoToCart.setOnClickListener {
            proceedToCart()
        }
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        val jsonObjectRequest = object  : JsonObjectRequest(Method.GET,url+ resId,null, Response.Listener {
            restaurantProgressBar.visibility = View.GONE
            val data = it.getJSONObject("data")
            val success = data.getBoolean("success")
            if(success) {

                val resArray = data.getJSONArray("data")

                for (i in 0 until resArray.length()) {

                    val menuObject = resArray.getJSONObject(i)
                    val menuItem = FoodItem(
                        menuObject.getString("id"),
                        menuObject.getString("name"),
                        menuObject.getString("cost_for_one").toInt()
                    )
                    menuList.add(menuItem)

                    restaurantsMenuAdaptor =
                        RestaurantsMenuAdaptor(
                            activity as Context,
                            menuList,
                            object :
                                RestaurantsMenuAdaptor.OnItemClickListener {
                                override fun onAddItemClick(foodItem: FoodItem) {
                                    orderList.add(foodItem)
                                    if (orderList.size > 0) {
                                        btnGoToCart.visibility = View.VISIBLE
                                        RestaurantsMenuAdaptor.isCartEmpty =
                                            false
                                    }
                                }

                                override fun onRemoveItemClick(foodItem: FoodItem) {
                                    orderList.remove(foodItem)
                                    if (orderList.isEmpty()) {
                                        btnGoToCart.visibility = View.GONE
                                        RestaurantsMenuAdaptor.isCartEmpty =
                                            true
                                    }
                                }
                            })
                    val mLayoutManager = LinearLayoutManager(activity)
                    recyclerMenuItems.adapter = restaurantsMenuAdaptor
                    recyclerMenuItems.layoutManager = mLayoutManager
                }
            }else{
                Toast.makeText(activity as Context,"Some error occurred" , Toast.LENGTH_SHORT).show()

            }

        },Response.ErrorListener {
            Toast.makeText(activity as Context,"Volley error occurred" , Toast.LENGTH_SHORT).show()

        }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "25c5be62209ad2"
                return headers
            }

        }
        queue.add(jsonObjectRequest)
        return view
    }

    private fun proceedToCart(){


        val gson = Gson()
        val foodItems = gson.toJson(orderList)
        val async = ItemsOfCart(
            activity as Context,
            resId.toString(),
            foodItems,
            1
        ).execute()
        val result = async.get()
        if (result) {
            val data = Bundle()
            data.putInt("resId", resId as Int)
            data.putString("resName",
                resName
            )
            val intent = Intent(activity, CartActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        } else {
            Toast.makeText((activity as Context), "Some unexpected error", Toast.LENGTH_SHORT)
                .show()
        }
    }


    class ItemsOfCart(context: Context, val restaurantId: String,
                      val foodItems: String,
                      val mode: Int):AsyncTask<Void,Void,Boolean>(){
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }
            }
            return false
        }

    }

}