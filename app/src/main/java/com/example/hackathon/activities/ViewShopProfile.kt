package com.example.hackathon.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.hackathon.R
import kotlinx.android.synthetic.main.activity_view_shop_profile.*

class ViewShopProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_shop_profile)
        val shopName =  intent.getStringExtra("ShopName")
        val ShopOwnerName = intent.getStringExtra("ShopOwnerName")
        val ShopCategories = intent.getStringExtra("ShopCategory")
        val ShopPhoneNo = intent.getStringExtra("ShopPhonNo")
        val ShopLocationLat=intent.getDoubleExtra("ShopLocationLat",0.0)
        val ShopLocationLang = intent.getDoubleExtra("ShopLocationLang",0.0)
        val ShopTime = intent.getStringExtra("ShopTiming")
        val ShopEmail = intent.getStringExtra("ShopEmail")
        val ShopImage = intent.getStringExtra("ShopImage")

        Log.d("Shop Details Image",ShopImage)
        Glide.with(this).load(ShopImage).into(viewShopImage)
        viewShopName.text = shopName
        viewShopCategories.text = ShopCategories
        viewShopOwner.text = ShopOwnerName
        viewShopEmail.text = ShopEmail
        viewShopTiming.text = ShopTime
        viewShopPhone.text = ShopPhoneNo
        backBtn.setOnClickListener {
            onBackPressed()
        }
    }
}
