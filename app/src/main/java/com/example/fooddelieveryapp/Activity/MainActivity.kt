package com.example.fooddelieveryapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.Volley
import com.example.fooddelieveryapp.Activity.LoginActivity.Companion.userName
import com.example.fooddelieveryapp.Activity.LoginActivity.Companion.userPhoneNumber
import com.example.fooddelieveryapp.Adaptor.RestaurantsMenuAdaptor
import com.example.fooddelieveryapp.Fragment.*
import com.example.fooddelieveryapp.Fragment.RestaurantFragment.Companion.resId
import com.example.fooddelieveryapp.R
import com.google.android.material.navigation.NavigationView



class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar : Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem : MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar =  findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)



        setUpToolbar(toolbar)
        openHome()
        //actionBarDrawerToggle used to tie the functionality of actionbar/toolbar & drawertoggle
        //action bardrawer toggle j hamburgericon che(toggle )
        val actionBarDrawertoggle=ActionBarDrawerToggle(this@MainActivity,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawertoggle)
        actionBarDrawertoggle.syncState()





        navigationView.setNavigationItemSelectedListener {
            if(previousMenuItem != null){
                previousMenuItem ?.isChecked=false
            }
            it.isChecked=true
            it.isCheckable=true
            it.isEnabled=true
            previousMenuItem=it
            when(it.itemId){
                R.id.home ->{
                    openHome()

                    drawerLayout.closeDrawers()
                }
                R.id.my_profile ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            MyProfileFragment()
                        )
                        .commit()
                    supportActionBar?.title="Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favourite_restaurants ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FavouriteRestaurantsFragment()
                        )
                        .commit()
                    supportActionBar?.title="Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.order_history ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            OrderHistoryFragment()
                        )
                        .commit()
                    supportActionBar?.title="Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FaqsFragment()
                        )
                        .commit()
                    supportActionBar?.title="FAQ'S"
                    drawerLayout.closeDrawers()
                }
                R.id.logout ->{
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want exit?")
                        .setPositiveButton("Yes") { _, _ ->

                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            finish()
                            //Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                            //ActivityCompat.finishAffinity(this)
                        }
                        .setNegativeButton("No") { _, _ ->
                            openHome()
                        }
                        .create()
                        .show()
                }
            }
            return@setNavigationItemSelectedListener true
        }


    }

    fun setUpToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Toolbar example"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //MenuItem is also known as homebutton/actionbar toggle
        val id = item.itemId
        if (id == android.R.id.home) {
            //id.home j hamburger nu id che
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)


    }
    fun openHome(){
        val fragment= HomeFragment()
        val transaction= supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout,fragment)
        transaction.commit()
        supportActionBar?.title="Home"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        val f = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (f) {
            is HomeFragment -> {
                Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                super.onBackPressed()
            }
            is RestaurantFragment -> {
                if (!RestaurantsMenuAdaptor.isCartEmpty) {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Going back will reset cart items. Do you still want to proceed?")
                        .setPositiveButton("Yes") { _, _ ->
                            val clearCart =
                                CartActivity.ClearDBASyncTask(applicationContext, resId.toString()).execute().get()
                            openHome()
                            RestaurantsMenuAdaptor.isCartEmpty = true
                        }
                        .setNegativeButton("No") { _, _ ->

                        }
                        .create()
                        .show()
                } else {
                    openHome()
                }
            }
            else -> openHome()
        }
    }

}

// on click logout dialogbox opens

