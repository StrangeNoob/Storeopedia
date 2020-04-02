package com.example.hackathon.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hackathon.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        cstm_login_btn.setOnClickListener {
            startActivity(Intent(applicationContext,CustomerDashboardActivity::class.java))
        }

        shpkr_login_btn.setOnClickListener {
            startActivity(Intent(applicationContext,ShopkeeperDashboardActivity::class.java))
        }
    }

}
