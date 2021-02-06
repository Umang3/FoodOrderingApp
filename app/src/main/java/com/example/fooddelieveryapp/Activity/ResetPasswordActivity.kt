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

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var txtEnterReceivedOtp : EditText
    lateinit var txtEnterNewPassword : EditText
    lateinit var txtEnterConfirmPassword : EditText
    lateinit var btnSubmit : Button
    lateinit var mobileNumber :String


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        txtEnterReceivedOtp = findViewById(R.id.txtEnterReceivedOtp)
        txtEnterNewPassword = findViewById(R.id.txtEnterNewPassword)
        txtEnterConfirmPassword = findViewById(R.id.txtEnterConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        if(intent != null){
            mobileNumber = intent.getStringExtra("mobile_number") as String
        }


        btnSubmit.setOnClickListener {

            val otp= txtEnterReceivedOtp.text.toString()
            val password = txtEnterNewPassword.text.toString()

            val url = "http://13.235.250.119/v2/reset_password/fetch_result"
            val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number",mobileNumber)
            jsonParams.put("password",password)
            jsonParams.put("otp",otp)

            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,url,jsonParams, Response.Listener {

                val data = it.getJSONObject("data")
                val success = data.getBoolean("success")
                if(success){
                    startActivity(
                        Intent(
                            this@ResetPasswordActivity,
                            LoginActivity::class.java
                        )
                    )
                }else{
                    Toast.makeText(this@ResetPasswordActivity,"Otp is incorrect",Toast.LENGTH_LONG).show()
                }

            },Response.ErrorListener {

                Toast.makeText(this@ResetPasswordActivity,"it might be Volley error!!",Toast.LENGTH_LONG).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["content-type"] = "application/json"
                    headers["token"] = "25c5be62209ad2"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ResetPasswordActivity,LoginActivity::class.java))
        finish()
    }
}