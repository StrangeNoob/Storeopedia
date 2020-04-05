package com.example.hackathon.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.hackathon.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_shopkeeper_login.*

class ShopkeeperLoginActivity : AppCompatActivity() {

    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopkeeper_login)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)

        new_shp_acc.setOnClickListener {
            startActivity(Intent(applicationContext, ShopkeeperRegistrationActivity::class.java))
        }
        forget_text.setOnClickListener {
            if (shopkeeperEmail.text.isNullOrBlank()) {
                Toast.makeText(this, "Enter Your Email ", Toast.LENGTH_LONG).show()
                shopkeeperEmail.error = "Enter Your Email"
            } else {
                val emailAddress = shopkeeperEmail.text.toString()
                auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Send", "Email sent.")
                            Toast.makeText(this, "Email has been sent", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        shopkeeper_LoginBtn.setOnClickListener{
            val emaiAddress = shopkeeperEmail.text.toString()
            val password = shopkeeperPassword.text.toString()
            auth.signInWithEmailAndPassword(emaiAddress,password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        sharedPref.edit().putInt("KEY",2).apply()
                        Toast.makeText(this,"LogIn Successfully ",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,ShopkeeperDashboardActivity::class.java))
                    } else {
                        shopkeeperEmail.setText(" ")
                        shopkeeperPassword.setText(" ")
                        Toast.makeText(this,"LogIn Failed",Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
