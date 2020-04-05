package com.example.hackathon.models

data class ShopModel (
    var shopName:String = "",
    var ownerName:String ="",
    var category: String ="",
    var email:String ="",
    var phoneNo:Long =0,
    var time:String ="",
    var shopLocationLat:Double=0.0,
    var shopLocationLang:Double=0.0,
    var image: String=""
)