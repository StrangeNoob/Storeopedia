package com.example.hackathon.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
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
            AuthUI.IdpConfig.GoogleBuilder().build(), //Google login
            AuthUI.IdpConfig.PhoneBuilder().build()   //Phone login
        )

        showSignInOptions()


        // Action Listener in Create Acc Button

        cst_create_acc.setOnClickListener {
            if(invalidInput()){
                customerModel= CustomerModel(cst_email_edittext.text.toString(),cst_name_edittext.text.toString(),cst_phone_edittext.text.toString())
                db.collection("Users").document(auth.currentUser!!.uid).set(customerModel!!).addOnSuccessListener {
                    Toast.makeText(applicationContext,"User is added to Database",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(applicationContext,"Problem Occurred",Toast.LENGTH_SHORT).show()
                }
                sharedPref.edit().putInt("KEY",1).apply()
                sharedPref.edit().putInt("Registered",1).apply()
                startActivity(Intent(this,CustomerDashboardActivity::class.java))
            }

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
                Toast.makeText(this, "User is Logged In",Toast.LENGTH_SHORT).show()

                if(sharedPref.getInt("Registered",0)==1){
                    startActivity(Intent(this,CustomerDashboardActivity::class.java))
                }
                else{
                    if(user!!.email != null){
                        Log.d("User",""+user!!.email+" "+user!!.displayName+" "+user!!.phoneNumber)
                        Toast.makeText(this, ""+user!!.email+"is Logged In",Toast.LENGTH_SHORT).show()
                        cst_email_edittext.setText(user!!.email)
                        cst_name_edittext.setText(user!!.displayName)
                    }else{
                        Log.d("User",user.phoneNumber)
                        Toast.makeText(applicationContext,user.phoneNumber,Toast.LENGTH_LONG).show()
                        cst_phone_edittext.setText(user.phoneNumber)
                    }
                }
            }

            else
            {
                Toast.makeText(this, ""+response!!.error!!.message,Toast.LENGTH_SHORT).show()
            }

        }
    }
    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
    private fun invalidInput(): Boolean{
        if(!isValidEmail(cst_email_edittext.text.toString())){
            cst_email_edittext.error = "You should enter valid email"
            return false
        }else if(cst_name_edittext.text.toString().isEmpty()){
            cst_email_edittext.error = "You should enter valid email"
            return false
        }else if(cst_phone_edittext.text.toString().length != 10){
            cst_phone_edittext.error = " You should enter valid phone no"
        }
        return true
    }
    private fun showSignInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
//            .setTheme(R.style.AppTheme_NoActionBar)
            .build(),MY_REQUEST_CODE
        )
    }
}
