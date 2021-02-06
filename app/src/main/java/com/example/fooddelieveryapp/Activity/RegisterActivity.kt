package com.example.fooddelieveryapp.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddelieveryapp.R
import com.example.fooddelieveryapp.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    lateinit var imgBackPressButton: ImageView
    lateinit var txtRegisterYourself: TextView
    lateinit var txtName: EditText
    lateinit var txtEmailAddress: EditText
    lateinit var txtMobileNumber: EditText
    lateinit var txtDeliveryAddress: EditText
    lateinit var txtPassword: EditText
    lateinit var txtConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        imgBackPressButton = findViewById(R.id.imgBackPressButton)
        txtRegisterYourself = findViewById(R.id.txtRegisterYourself)
        txtName = findViewById(R.id.txtName)
        txtEmailAddress = findViewById(R.id.txtEmailAddress)
        txtMobileNumber = findViewById(R.id.txtMobileNumber)
        txtDeliveryAddress = findViewById(R.id.txtDeliveryAddress)
        txtPassword = findViewById(R.id.txtPassword)
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        sharedPreferences = this@RegisterActivity.getSharedPreferences(
            R.string.register_shared_preferences.toString(),
            Context.MODE_PRIVATE
        )
        if(ConnectionManager().isNetworkAvailable(this@RegisterActivity)) {
            btnRegister.setOnClickListener {
                val name = txtName.text.toString()
                val emailAddress = txtEmailAddress.text.toString()
                val mobileNumber = txtMobileNumber.text.toString()
                val address = txtDeliveryAddress.text.toString()
                val password = txtPassword.text.toString()
                val password1 = txtConfirmPassword.text.toString()


                if(name.length>3) {

                    if (mobileNumber.length == 10) {

                        if(password.compareTo(password1) == 0){
                        val queue = Volley.newRequestQueue(this@RegisterActivity as Context)
                        val url = "http://13.235.250.119/v2/register/fetch_result"

                        val jsonParams = JSONObject()
                        jsonParams.put("name", name)
                        jsonParams.put("mobile_number", mobileNumber)
                        jsonParams.put("password", password)
                        jsonParams.put("address", address)
                        jsonParams.put("email", emailAddress)

                        val jsonObjectRequest = object :
                            JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                jsonParams,
                                Response.Listener {
                                    try {
                                        val data = it.getJSONObject("data")
                                        val success = data.getBoolean("success")
                                        if (success) {
                                            val response = data.getJSONObject("data")
                                            sharedPreferences.edit()
                                                .putString("user_id", response.getString("user_id"))
                                                .apply()
                                            sharedPreferences.edit()
                                                .putString("user_name", response.getString("name"))
                                                .apply()
                                            sharedPreferences.edit().putString(
                                                "user_mobile_number",
                                                response.getString("mobile_number")
                                            ).apply()
                                            sharedPreferences.edit()
                                                .putString(
                                                    "user_address",
                                                    response.getString("address")
                                                )
                                                .apply()
                                            sharedPreferences.edit()
                                                .putString(
                                                    "user_email",
                                                    response.getString("email")
                                                )
                                                .apply()

                                            val intent =
                                                Intent(
                                                    this@RegisterActivity,
                                                    LoginActivity::class.java
                                                )
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@RegisterActivity,
                                                "You have already registered",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Some unexpected error occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                },
                                Response.ErrorListener {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Volley Error",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()

                                }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "25c5be62209ad2"
                                return headers
                            }
                        }
                        queue.add(jsonObjectRequest)
                        }else{
                            Toast.makeText(
                                this@RegisterActivity,
                                "Passwords Are Different",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Invalid Number",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }else{
                    Toast.makeText(
                        this@RegisterActivity,
                        "Invalid Name",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }



        }
        }
        else{
            val dialog= AlertDialog.Builder(this@RegisterActivity)
            dialog.setTitle("No Internet")
            dialog.setMessage("Internet Connection is NOT  Found")
            dialog.setPositiveButton("Open Settings"){ text , listener ->
                // Do nothing
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()

            }
            dialog.setNegativeButton("Exit"){ text, listener ->
                // Do Nothing
                // ActivityCompact class che jema finishAffinity method che which is used to close app at the instant.

                ActivityCompat.finishAffinity(this@RegisterActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
        finish()
    }
}