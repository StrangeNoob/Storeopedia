package com.example.hackathon.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        Log.d("Shared Pref ",sharedPref.getInt("KEY",0).toString())
        if(sharedPref.getInt("KEY",0)==1){
            startActivity(Intent(applicationContext,CustomerDashboardActivity::class.java))
        }else if(sharedPref.getInt("KEY",0)==2){
            startActivity(Intent(applicationContext,ShopkeeperDashboardActivity::class.java))
        }

        cstm_login_btn.setOnClickListener {
            startActivity(Intent(applicationContext,CustomerLoginActivity::class.java))
        }

        shpkr_login_btn.setOnClickListener {
            startActivity(Intent(applicationContext,ShopkeeperDashboardActivity::class.java))
        }
    }
    companion object {

    }


}
