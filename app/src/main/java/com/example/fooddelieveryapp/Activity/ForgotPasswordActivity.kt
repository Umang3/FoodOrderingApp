package com.example.fooddelieveryapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddelieveryapp.R
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var txtForgotPasswordMobileNumber : EditText
    lateinit var txtForgotPasswordEmailAddress : EditText
    lateinit var btnForgotPassword : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        txtForgotPasswordMobileNumber = findViewById(R.id.txtForgotPasswordMobileNumber)
        txtForgotPasswordEmailAddress = findViewById(R.id.txtForgotPasswordEmailAddress)
        btnForgotPassword = findViewById(R.id.btnForgotPassword)

        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
        val jsonParams = JSONObject()
        btnForgotPassword.setOnClickListener {
            val mobileNumber=txtForgotPasswordMobileNumber.text.toString()
            val emailAddress=txtForgotPasswordEmailAddress.text.toString()

            jsonParams.put("mobile_number",mobileNumber)
            jsonParams.put("email",emailAddress)

            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val firstTry = data.getBoolean("first_try")

                        val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                        if (firstTry) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "This is your 1st time",
                                Toast.LENGTH_SHORT
                            ).show()
                            intent.putExtra("mobile_number", mobileNumber)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "it looks you have tried before!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            intent.putExtra("mobile_number", mobileNumber)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Sorry Account details NOT found",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }catch (e : Exception){
                    Toast.makeText(this@ForgotPasswordActivity,"Some unexpected error occurred",Toast.LENGTH_SHORT).show()
                }

            },Response.ErrorListener {
                Toast.makeText(this@ForgotPasswordActivity,"Volley error occurred",Toast.LENGTH_LONG).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "25c5be62209ad2"
                    return  headers
                }
            }
            queue.add(jsonObjectRequest)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ForgotPasswordActivity,LoginActivity::class.java))
        finish()
    }
}