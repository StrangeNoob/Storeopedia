package com.example.hackathon.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.hackathon.R
import kotlinx.android.synthetic.main.activity_view_shop_profile.*


class ViewShopProfile : AppCompatActivity() {

    lateinit var  ShopPhoneNo: String
    var ShopLocationLat : Double ?= null
    var ShopLocationLang : Double ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_shop_profile)
        val shopName =  intent.getStringExtra("ShopName")
        val ShopOwnerName = intent.getStringExtra("ShopOwnerName")
        val ShopCategories = intent.getStringExtra("ShopCategory")
        ShopPhoneNo = intent.getStringExtra("ShopPhonNo")
        ShopLocationLat=intent.getDoubleExtra("ShopLocationLat",0.0)
        ShopLocationLang = intent.getDoubleExtra("ShopLocationLang",0.0)
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
        backBtn.setOnClickListener {
            onBackPressed()
        }
        viewPhone.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions()
            }
            val u: Uri = Uri.parse("tel:" + ShopPhoneNo)
            val intent : Intent = Intent(Intent.ACTION_CALL)
            intent.setData(u)
            startActivity(intent)
        }
        viewDirection.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=$ShopLocationLat,$ShopLocationLang")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CALL_PHONE),
            1
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("User Details", "Permission has been denied by user")

                } else {
                    Log.i("User Details", "Permission has been granted by user")
                    val u: Uri = Uri.parse("tel:$ShopPhoneNo")
                    val intent : Intent = Intent(Intent.ACTION_CALL)
                    intent.setData(u)
                    if (ActivityCompat.checkSelfPermission(
                            this@ViewShopProfile,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    startActivity(intent)
                }
            }
        }
    }

}
