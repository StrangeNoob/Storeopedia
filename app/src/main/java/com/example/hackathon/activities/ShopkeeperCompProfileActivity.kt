package com.example.hackathon.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hackathon.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_shopkeeper_comp_profile.*
import kotlinx.android.synthetic.main.shopcardview.*

class ShopkeeperCompProfileActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var db : FirebaseFirestore
    var OpeningTime: String =""
    var ClosingTime: String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopkeeper_comp_profile)

        auth=FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        complete_shop_profile.setOnClickListener{
//            Log.d("Shops Details",openingTime.hour.toString()+":"+openingTime.minute.toString())
//            Log.d("Shops Details",closingTime.hour.toString()+":"+closingTime.minute.toString())
            var combinedTime: String = getTime()
            timeUpdate(combinedTime)
        }

    }
    private fun getTime() : String{
        Log.d("User Details",openingTime.toString())
        if(openingTime.minute < 9){
            openingTime.minute = ("0"+openingTime.minute.toString()).toInt()
        }
        if(closingTime.minute < 9){
            closingTime.minute = ("0"+closingTime.minute.toString()).toInt()
        }
        if(openingTime.hour > 12 && closingTime.hour > 12 ){
            OpeningTime = (openingTime.hour-12).toString()+":"+openingTime.minute.toString()+"PM"
            ClosingTime = (closingTime.hour-12).toString()+":"+closingTime.minute.toString()+"PM"
        }else if( openingTime.hour < 12 && closingTime.hour > 12){
            OpeningTime = (openingTime.hour).toString()+":"+openingTime.minute.toString()+"AM"
            ClosingTime = (closingTime.hour-12).toString()+":"+closingTime.minute.toString()+"PM"
        }else if(openingTime.hour > 12 && closingTime.hour < 12){
            OpeningTime = (openingTime.hour-12).toString()+":"+openingTime.minute.toString()+"PM"
            ClosingTime = (closingTime.hour).toString()+":"+closingTime.minute.toString()+"AM"
        }else{
            OpeningTime = (openingTime.hour).toString()+":"+openingTime.minute.toString()+"AM"
            ClosingTime = (closingTime.hour).toString()+":"+closingTime.minute.toString()+"AM"
        }
        Log.d("User Details","$OpeningTime-$ClosingTime")
        return "$OpeningTime-$ClosingTime"
    }

    fun timeUpdate(time:String){
        val user = auth.currentUser

        db.collection("Shops").document(user!!.uid).update("time",time).addOnSuccessListener {
                Toast.makeText(this,"Successfully Updated Your Profile Login to Enter ",Toast.LENGTH_LONG).show()
                auth.signOut()
                startActivity(Intent(applicationContext,ShopkeeperLoginActivity::class.java))
        }.addOnFailureListener {
            Toast.makeText(this," Try Again ",Toast.LENGTH_LONG).show()
        }
    }
}
