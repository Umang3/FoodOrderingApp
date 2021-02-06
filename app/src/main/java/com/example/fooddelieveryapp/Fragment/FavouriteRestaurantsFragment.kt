package com.example.fooddelieveryapp.Fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.fooddelieveryapp.Adaptor.HomeRecyclerAdaptor
import com.example.fooddelieveryapp.R
import com.example.fooddelieveryapp.database.RestaurantDatabase
import com.example.fooddelieveryapp.database.RestaurantEntity
import com.example.fooddelieveryapp.model.Restaurants

class FavouriteRestaurantsFragment : Fragment() {

    lateinit var recyclerFavouriteRestaurants : RecyclerView
    lateinit var homeRecyclerAdaptor: HomeRecyclerAdaptor
     var restaurantList = arrayListOf<Restaurants>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_favourite_restaurants, container, false)

        recyclerFavouriteRestaurants = view.findViewById(R.id.recyclerFavouriteRestaurants)
        val resList = GetAllFavSyncTask(
            activity as Context
        ).execute().get()

        for(i in resList){

            val resInfoList = Restaurants(
                i.id,
                i.name,
                i.rating,
                i.costForTwo.toInt(),
                i.imageUrl
            )
            restaurantList.add(resInfoList)
        }
        homeRecyclerAdaptor =
            HomeRecyclerAdaptor(
                activity as Context,
                restaurantList
            )
        recyclerFavouriteRestaurants.adapter = homeRecyclerAdaptor
        val mLayoutManager = LinearLayoutManager(activity)
        recyclerFavouriteRestaurants.layoutManager = mLayoutManager

        return view
    }

    class GetAllFavSyncTask(context : Context) : AsyncTask<Void,Void,List<RestaurantEntity>>() {

        val db = Room.databaseBuilder(context,RestaurantDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
            return db.restaurantDao().getAllRestaurants()
        }
    }

}