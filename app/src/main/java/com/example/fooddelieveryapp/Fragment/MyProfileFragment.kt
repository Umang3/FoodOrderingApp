package com.example.fooddelieveryapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.fooddelieveryapp.Activity.LoginActivity.Companion.userAddress
import com.example.fooddelieveryapp.Activity.LoginActivity.Companion.userEmail
import com.example.fooddelieveryapp.Activity.LoginActivity.Companion.userName
import com.example.fooddelieveryapp.Activity.LoginActivity.Companion.userPhoneNumber
import com.example.fooddelieveryapp.R

class MyProfileFragment : Fragment() {


   //  lateinit var sharedPreferences: SharedPreferences
    lateinit var txtUserName : TextView
    lateinit var txtPhone : TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_profile, container, false)
        txtUserName = view.findViewById(R.id.txtUserName)
        txtPhone = view.findViewById(R.id.txtPhone)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)



   /*  sharedPreferences = (activity as FragmentActivity).getSharedPreferences(
         R.string.register_shared_preferences.toString(),
         Context.MODE_PRIVATE
     )*/
       /* txtUserName.text = sharedPreferences.getString("user_name",null) as String
        txtPhone.text = sharedPreferences.getString("user_mobile_number",null) as String
        txtEmail.text = sharedPreferences.getString("user_email",null) as String
        txtAddress.text = sharedPreferences.getString("user_address",null) as String*/




        txtEmail.text = userEmail
        txtPhone.text = userPhoneNumber.toString()
        txtUserName.text = userName
        txtAddress.text = userAddress
        return view
    }

}