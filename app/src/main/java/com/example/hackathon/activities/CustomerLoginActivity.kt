package com.example.hackathon.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.example.hackathon.R
import com.example.hackathon.models.CustomerModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_customer_login2.*
import java.util.*


class CustomerLoginActivity : AppCompatActivity() {


    lateinit var providers: List<AuthUI.IdpConfig>
    val MY_REQUEST_CODE: Int = 7//Any random number
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPref: SharedPreferences
    private lateinit var customerModel : CustomerModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_login2)

        db = FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        //Init
        providers = Arrays.asList<AuthUI.IdpConfig>(
//            AuthUI.IdpConfig.EmailBuilder().build(),  //Email login
            AuthUI.IdpConfig.GoogleBuilder().build() //Google login
//            AuthUI.IdpConfig.PhoneBuilder().build()   //Phone login
        )

        showSignInOptions()

        //Event
//        btn_sign_out.setOnClickListener{
//            //Signout
//            AuthUI.getInstance().signOut(this@CustomerLoginActivity)
//                .addOnCompleteListener {
//                    btn_sign_out.isEnabled = false
//                    showSignInOptions()
//                }
//                .addOnFailureListener {
//                        e-> Toast.makeText(this@CustomerLoginActivity,e.message,Toast.LENGTH_SHORT).show()
//                }
//
//        }
        //disable the Create Account Button
        cst_create_acc.isEnabled= false
        cst_create_acc.background=applicationContext.getDrawable(R.drawable.rounded_corner_gray)
        //Phone No 10 nos
        cst_phone_edittext.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    Log.d("Text Count",""+s.length)
                    if(s.length == 10){
                        cst_create_acc.background=applicationContext.getDrawable(R.drawable.rounded_corner)
                        cst_create_acc.isEnabled=true
                    }
            }
        })
        // Action Listener in Create Acc Button

        cst_create_acc.setOnClickListener {

            customerModel!!.phoneNo = cst_phone_edittext.text.toString()
            db.collection("Users").document(auth.currentUser!!.uid).set(customerModel!!).addOnSuccessListener {
                Toast.makeText(applicationContext,"User is added to Database",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext,"Problem Occurred",Toast.LENGTH_SHORT).show()
            }
            sharedPref.edit().putInt("KEY",1).apply()
            sharedPref.edit().putInt("Registered",1).apply()
            Log.d("Shared Pref ",sharedPref.getInt("KEY",0).toString())
            startActivity(Intent(this,CustomerDashboardActivity::class.java))
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MY_REQUEST_CODE)
        {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK)
            {
                val user = auth.currentUser //get current user
                Toast.makeText(this, ""+user!!.email+"is Logged In",Toast.LENGTH_SHORT).show()

                if(sharedPref.getInt("Registered",0)==1){
                    startActivity(Intent(this,CustomerDashboardActivity::class.java))
                }
                else{
                    Log.d("User",""+user!!.email+" "+user!!.displayName+" "+user!!.phoneNumber)
                    Toast.makeText(this, ""+user!!.email+"is Logged In",Toast.LENGTH_SHORT).show()
                    cst_email_edittext.setText(user!!.email)
                    cst_name_edittext.setText(user!!.displayName)
                    customerModel = CustomerModel(user.email!!,user.displayName!!)
                }
            }

            else
            {
                Toast.makeText(this, ""+response!!.error!!.message,Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun checkPhoneNo(){
        val user = auth.currentUser

    }
    private fun showSignInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
//            .setTheme(R.style.AppTheme_NoActionBar)
            .build(),MY_REQUEST_CODE
        )
    }
}
