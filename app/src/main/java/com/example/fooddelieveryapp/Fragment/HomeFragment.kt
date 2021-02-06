package com.example.fooddelieveryapp.Fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddelieveryapp.Adaptor.HomeRecyclerAdaptor
import com.example.fooddelieveryapp.R
import com.example.fooddelieveryapp.model.Restaurants
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class HomeFragment : Fragment() {


/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }*/
    lateinit var recyclerHome : RecyclerView
    lateinit var rlLoading : RelativeLayout
    lateinit var homeProgressBar : ProgressBar
    lateinit var homeRecyclerAdaptor: HomeRecyclerAdaptor
    var restaurantList = arrayListOf<Restaurants>()

    var checkedItem = -1

    var costComparator = Comparator<Restaurants>{ res1,res2 ->
        res1.costForTwo.compareTo(res2.costForTwo)
    }

    var ratingComparator = Comparator<Restaurants>{ res1 , res2 ->
        if(res1.rating.compareTo(res2.rating , true)==0){
            res1.name.compareTo(res2.name , true)
        }
        else{
            res1.rating.compareTo(res2.rating,true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        rlLoading = view.findViewById(R.id.rlLoading)
        homeProgressBar = view.findViewById(R.id.homeProgressBar)
        rlLoading.visibility = View.GONE
        homeProgressBar.visibility = View.GONE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        val jsonObjectRequest = object  : JsonObjectRequest(Method.GET,url,null, Response.Listener {

            val data = it.getJSONObject("data")
            val success = data.getBoolean("success")
            if(success) {
                val resArray = data.getJSONArray("data")

                for (i in 0 until resArray.length()) {
                    val resObject = resArray.getJSONObject(i)
                    val restaurants = Restaurants(
                        resObject.getString("id").toInt(),
                        resObject.getString("name"),
                        resObject.getString("rating"),
                        resObject.getString("cost_for_one").toInt(),
                        resObject.getString("image_url")
                    )
                    restaurantList.add(restaurants)

                    homeRecyclerAdaptor =
                        HomeRecyclerAdaptor(
                            activity as Context,
                            restaurantList
                        )
                    val mLayoutManager = LinearLayoutManager(activity)
                    recyclerHome.adapter = homeRecyclerAdaptor
                    recyclerHome.layoutManager = mLayoutManager
                }
            }
            else{
                Toast.makeText(activity as Context,"Some error occurred", Toast.LENGTH_SHORT).show()
            }



        },Response.ErrorListener {

            Toast.makeText(activity as Context,"Volley error occurred",Toast.LENGTH_SHORT).show()

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.dashboard_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.action_sort -> showDialog(context as Context)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(context: Context){

        val builder: AlertDialog.Builder? = AlertDialog.Builder(context)
        builder?.setTitle("Sort By?")
        builder?.setSingleChoiceItems(R.array.filters,checkedItem){ _, isChecked ->
            checkedItem = isChecked
        }

        builder?.setPositiveButton("Ok"){_, _ ->

            when(checkedItem){

                0 ->{
                    Collections.sort(restaurantList,costComparator)
                }

                1 ->{

                    Collections.sort(restaurantList,costComparator)
                    restaurantList.reverse()
                }
                2 ->{
                    Collections.sort(restaurantList,ratingComparator)
                    restaurantList.reverse()
                }
            }
            homeRecyclerAdaptor.notifyDataSetChanged()
        }

        builder?.setNegativeButton("Cancel") { _, _ ->

        }
        builder?.create()
        builder?.show()
    }

}

