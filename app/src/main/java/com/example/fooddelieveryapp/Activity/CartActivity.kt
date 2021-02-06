package com.example.fooddelieveryapp.Activity

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.camera2.params.RggbChannelVector.BLUE
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.media.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddelieveryapp.*
import com.example.fooddelieveryapp.Adaptor.CartItemAdaptor
import com.example.fooddelieveryapp.Activity.LoginActivity.Companion.userId
import com.example.fooddelieveryapp.Adaptor.RestaurantsMenuAdaptor
import com.example.fooddelieveryapp.Fragment.RestaurantFragment
import com.example.fooddelieveryapp.database.OrderEntity
import com.example.fooddelieveryapp.database.RestaurantDatabase
import com.example.fooddelieveryapp.model.FoodItem
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {


    lateinit var btnConfirmOrder: Button
    lateinit var txtCartRestaurantName: TextView
    lateinit var recyclerCartItems: RecyclerView
    lateinit var rlLoading: RelativeLayout
    lateinit var cartProgressBar: ProgressBar
    lateinit var rlCart : RelativeLayout
    var orderList = arrayListOf<FoodItem>()
    var sum = 0
    var resId: Int? = 0
    var resName: String? = ""
    lateinit var toolbar: Toolbar
    lateinit var cartItemAdaptor: CartItemAdaptor

    private val CHANNEL_ID = "com.example.fooddelieveryapp.Activity"
    private val notificationID = 101



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        setupToolbar()
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder)
        txtCartRestaurantName = findViewById(R.id.txtCartRestaurantName)
        recyclerCartItems = findViewById(R.id.recyclerCartItems)
        rlLoading = findViewById(R.id.rlLoading)
        rlCart = findViewById(R.id.rlCart)

        cartProgressBar = findViewById(R.id.cartProgressBar)
        val bundle = intent.getBundleExtra("data")
        resId = bundle?.getInt("resId", 0) as Int
        resName = bundle.getString("resName", "") as String
        txtCartRestaurantName.text = resName


        val dbList = DBAAsynctask(
            applicationContext
        ).execute().get()

        for (element in dbList) {
            orderList.addAll(
                Gson().fromJson(element.foodItems, Array<FoodItem>::class.java).asList()
            )
        }

        if (orderList.isEmpty()) {
            rlCart.visibility = View.GONE
            rlLoading.visibility = View.VISIBLE
        } else {
            rlCart.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
        }

        for (i in 0 until orderList.size) {
            sum += orderList[i].cost as Int
        }
        val total = "Place Order Rs. $sum"
        btnConfirmOrder.text = total

        cartItemAdaptor = CartItemAdaptor(
            this@CartActivity,
            orderList
        )
        val mLayoutManager = LinearLayoutManager(this@CartActivity)
        recyclerCartItems.adapter = cartItemAdaptor
        recyclerCartItems.layoutManager = mLayoutManager

        btnConfirmOrder.setOnClickListener {

            val queue = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            val jsonParams = JSONObject()
           /* jsonParams.put(
                "user_id",
                this@CartActivity.getSharedPreferences(
                    "Login Shared Preferences",
                    Context.MODE_PRIVATE
                ).getString("user_id",null) as String
            )*/


            jsonParams.put("user_id",userId)
            jsonParams.put("restaurant_id", RestaurantFragment.resId?.toString() as String)
            jsonParams.put("total_cost", sum.toString())
            val foodArray = JSONArray()
            for (i in 0 until orderList.size) {
                val foodId = JSONObject()
                foodId.put("food_item_id", orderList[i].id)
                foodArray.put(i, foodId)
            }
            jsonParams.put("food", foodArray)

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if(success){

                        val clearCart = ClearDBASyncTask(
                            applicationContext,
                            resId.toString()
                        ).execute().get()
                        RestaurantsMenuAdaptor.isCartEmpty = true

                        val dialog = Dialog(
                            this@CartActivity,
                            android.R.style.Theme_Black_NoTitleBar_Fullscreen
                        )
                        dialog.setContentView(R.layout.order_placed_dialog)
                        dialog.show()
                        dialog.setCancelable(false)
                        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                        btnOk.setOnClickListener {
                           // dialog.dismiss()

                            val intent = Intent(this@CartActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }

                            val pendingIntent : PendingIntent = PendingIntent.getActivity(this , 0 , intent , 0)

                            //startActivity(Intent(this@CartActivity, MainActivity::class.java))
                            //ActivityCompat.finishAffinity(this@CartActivity)


                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                val name = "Notification Title"
                                val descriptionText = "Notification Description"
                                val importance = NotificationManager.IMPORTANCE_HIGH
                                val channel = NotificationChannel(CHANNEL_ID , name , importance).apply {

                                    description = descriptionText
                                }
                                channel.enableLights(true)
                                channel.enableVibration(true)



                                val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                notificationManager.createNotificationChannel(channel)

                                val bitmap =BitmapFactory.decodeResource(applicationContext.resources , R.drawable.food_logo)
                                val bitMapLargeIcon = BitmapFactory.decodeResource(applicationContext.resources , R.drawable.food_logo)


                                val notificationLayout = RemoteViews(packageName,R.layout.notification_layout)
                                val builder = androidx.core.app.NotificationCompat.Builder(this,CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_thumbs_up)
                                    .setContentTitle("$resName")
                                    .setContentText("You have Placed the Order Successfully!!")
                                    .setCustomContentView(notificationLayout)
                                    .setLargeIcon(bitMapLargeIcon)

                                    .setContentIntent(pendingIntent)
                                    .setStyle(NotificationCompat.DecoratedMediaCustomViewStyle())
                                   .setStyle(androidx.core.app.NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                                    //.setStyle(Notification.BigPictureStyle().bigPicture(bitmap))
                                   .setPriority(Notification.PRIORITY_DEFAULT)

                                val note : Notification = builder.build()
                                note.defaults = Notification.DEFAULT_VIBRATE
                                note.defaults = Notification.DEFAULT_SOUND

                                with(NotificationManagerCompat.from(this)){
                                    notify(notificationID , builder.build())
                                }

                            }else{
                                val bitMapLargeIcon = BitmapFactory.decodeResource(applicationContext.resources , R.drawable.food_logo)
                                val builder = androidx.core.app.NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                                    .setContentTitle("Example Title")
                                    .setContentText("You have Placed the Order Successfully")
                                    .setLargeIcon(bitMapLargeIcon)

                                with(NotificationManagerCompat.from(this)){
                                    notify(notificationID , builder.build())
                                }

                            }
                            dialog.dismiss()
                            startActivity(intent)
                            ActivityCompat.finishAffinity(this@CartActivity)

                        }

                    }else {
                        rlCart.visibility = View.VISIBLE
                        Toast.makeText(this@CartActivity, "Somee Error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, Response.ErrorListener {

                    rlCart.visibility = View.VISIBLE
                    Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "25c5be62209ad2"
                        return headers
                    }

                }
            queue.add(jsonObjectRequest)
        }
    }
        class DBAAsynctask(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {

            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
            override fun doInBackground(vararg p0: Void?): List<OrderEntity> {
                return db.orderDao().getAllOrders()
            }


        }

        class ClearDBASyncTask(context: Context, val resId: String) :
            AsyncTask<Void, Void, Boolean>() {

            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
            override fun doInBackground(vararg p0: Void?): Boolean {
                db.orderDao().deleteOrders(resId)
                db.close()
                return true
            }

        }

        private fun setupToolbar() {
            toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.title = "My Cart"
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    override fun onSupportNavigateUp(): Boolean {
        if (ClearDBASyncTask(
                applicationContext,
                resId.toString()
            ).execute().get()) {
            RestaurantsMenuAdaptor.isCartEmpty = true
            onBackPressed()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        ClearDBASyncTask(
            applicationContext,
            resId.toString()
        ).execute().get()
        RestaurantsMenuAdaptor.isCartEmpty = true
        startActivity(Intent(this@CartActivity,MainActivity::class.java))
        finish()
       // super.onBackPressed()
    }


}