package com.example.hackathon.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon.R
import com.example.hackathon.models.CustomerModel
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        var customer = CustomerModel("","sipun123@gm.com","Prats","1234567890")
        email_id.text = customer.email
        username.text = customer.name
        phonenum.text = customer.phoneNo
        if(customer.profilephoto.isNotEmpty()||customer.profilephoto.isNotBlank()) {
                    profile_photo.setImageURI()
        }

        signoutbutton.setOnClickListener {

        }

    }
}
