package com.example.fooddelieveryapp.Adaptor

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.fooddelieveryapp.R
import com.example.fooddelieveryapp.Fragment.RestaurantFragment
import com.example.fooddelieveryapp.database.RestaurantDatabase
import com.example.fooddelieveryapp.database.RestaurantEntity
import com.example.fooddelieveryapp.model.Restaurants
import com.squareup.picasso.Picasso

class   HomeRecyclerAdaptor(val context: Context,val restaurantsList : ArrayList<Restaurants>)
    : RecyclerView.Adapter<HomeRecyclerAdaptor.HomeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_recycler_single_row, parent, false)
        return HomeViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return restaurantsList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        val res = restaurantsList[position]
        Picasso.get().load(res.imageUrl).error(R.drawable.ic_launcher_background)
            .into(holder.imgRestaurantImage)
        holder.txtRestaurantName.text = res.name
        val cost1 = "${res.costForTwo.toString()}/person"
        holder.txtCost.text = cost1
        holder.txtRating.text = res.rating


        val listOfFavourites = GetAllFavAsyncTask(
            context
        ).execute().get()

        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(res.id.toString())) {
            holder.imgIsFav.setImageResource(R.drawable.ic_is_fav_checked)
        } else {
            holder.imgIsFav.setImageResource(R.drawable.ic_favourites_restaurants)
        }

        holder.imgIsFav.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                res.id,
                res.name,
                res.rating,
                res.costForTwo.toString(),
                res.imageUrl
            )

            if (!DBAsyncTask(
                    context,
                    restaurantEntity,
                    1
                ).execute().get()) {
                val async =
                    DBAsyncTask(
                        context,
                        restaurantEntity,
                        2
                    ).execute()
                val result = async.get()
                if (result) {
                    holder.imgIsFav.setImageResource(R.drawable.ic_is_fav_checked)
                }
            } else {
                val async = DBAsyncTask(
                    context,
                    restaurantEntity,
                    3
                ).execute()
                val result = async.get()

                if (result) {
                    holder.imgIsFav.setImageResource(R.drawable.ic_favourites_restaurants)
                }
            }

        }


        holder.cardRestaurant.setOnClickListener {
            val fragment =
                RestaurantFragment()
            val args = Bundle()
            args.putInt("id", res.id)
            args.putString("name", res.name)
            fragment.arguments = args
            val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, fragment)
            transaction.commit()
            (context as AppCompatActivity).supportActionBar?.title = holder.txtRestaurantName.text.toString()
        }

    }


    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardRestaurant: CardView = view.findViewById(R.id.cardRestaurant)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val imgIsFav: ImageView = view.findViewById(R.id.imgIsFav)
        val txtCost: TextView = view.findViewById(R.id.txtCost)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
    }

    class DBAsyncTask(context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            /*
            Mode 1 -> Check DB if the book is favourite or not
            Mode 2 -> Save the book into DB as favourite
            Mode 3 -> Remove the favourite book
            */

            when (mode) {

                1 -> {
                    val res: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return res != null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }

    class GetAllFavAsyncTask(context: Context) : AsyncTask<Void, Void, List<String>>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg p0: Void?): List<String> {

            val list = db.restaurantDao().getAllRestaurants()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id.toString())
            }
            return listOfIds
        }
    }
}


