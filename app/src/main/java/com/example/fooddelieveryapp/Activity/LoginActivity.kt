package com.example.fooddelieveryapp.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddelieveryapp.R
import com.example.fooddelieveryapp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var txtEnterMobileNo: EditText
    lateinit var txtEnterPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtSignUpNow: TextView
    lateinit var sharedPreferences: SharedPreferences

    companion object{
        @SuppressLint("StaticFieldLeak")
        var userId: Int? = 0
        var userEmail : String? = ""
        var userPhoneNumber : String? =""
        var userName : String? = ""
        var userAddress : String? = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        txtEnterMobileNo = findViewById(R.id.txtEnterMobileNo)
        txtEnterPassword = findViewById(R.id.txtEnterPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtSignUpNow = findViewById(R.id.txtSignUpNow)
        sharedPreferences = getSharedPreferences(R.string.login_shared_preferences.toString(), Context.MODE_PRIVATE)
       /* var isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }*/



        title = "Log In"

        if (ConnectionManager().isNetworkAvailable(this@LoginActivity as Context)) {
            btnLogin.setOnClickListener {
                val mobileNumber = txtEnterMobileNo.text.toString()
                val password = txtEnterPassword.text.toString()


                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("password", password)

                val url = "http://13.235.250.119/v2/login/fetch_result"
                val queue = Volley.newRequestQueue(this@LoginActivity as Context)


                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.POST,
                    url,
                    jsonParams,
                    Response.Listener<JSONObject> { response ->


                        try {
                            val data = response.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {

                                val response = data.getJSONObject("data")
                                sharedPreferences.edit()
                                    .putString("user_id", response.getString("user_id")).apply()

                                userId = response.getString("user_id").toInt()

                                sharedPreferences.edit()
                                    .putString("user_name", response.getString("name")).apply()

                                userName = response.getString("name")

                                sharedPreferences.edit()
                                    .putString("user_email", response.getString("email"))
                                    .apply()

                                userEmail = response.getString("email")

                                sharedPreferences.edit().putString(
                                    "user_mobile_number",
                                    response.getString("mobile_number")
                                ).apply()

                                userPhoneNumber = response.getString("mobile_number")

                                sharedPreferences.edit()
                                    .putString("user_address", response.getString("address"))
                                    .apply()

                                userAddress =response.getString("address")

                                sharedPreferences.edit()
                                    .putBoolean("isLoggedIn",true)
                                    .apply()

                                // isLoggedIn = true

                                val intent =
                                    Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()


                            } else {
                                Toast.makeText(
                                    this@LoginActivity as Context,
                                    "Sorry LogIn details not found",
                                    Toast.LENGTH_LONG
                                ).show()
                            }


                        } catch (e: JSONException) {
                            Toast.makeText(this@LoginActivity,"Some unexpected error occurred",
                                Toast.LENGTH_SHORT).show()
                        }


                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@LoginActivity as Context,
                            "it's Volley Error",
                            Toast.LENGTH_SHORT
                        ).show()
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
        } else {
            val dialog= AlertDialog.Builder(this@LoginActivity)
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

                ActivityCompat.finishAffinity(this@LoginActivity)
            }
            dialog.create()
            dialog.show()
        }

        txtForgotPassword.setOnClickListener{
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()

        }
        txtSignUpNow.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()

        }

    }



}

