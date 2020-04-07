package com.example.hackathon.adapters


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackathon.R
import com.example.hackathon.activities.ViewShopProfile
import com.example.hackathon.models.ShopModel
import kotlinx.android.synthetic.main.shopcardview.view.*
class ShopAdapter(val shops: ArrayList<ShopModel>,val context: Context): RecyclerView.Adapter<ShopViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        return ShopViewHolder(LayoutInflater.from(context).inflate(R.layout.shopcardview,parent,false))
    }

    override fun getItemCount(): Int {
        return shops.size
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {

        holder.shopName.text = shops.get(position).shopName
        Glide.with(context).load(shops.get(position).image).into(holder.shopImage)
        holder.shopTime.text = shops.get(position).time
//        holder.shopPhoneNo.text = shops.get(position).phoneNo
        holder.category.text = shops.get(position).category
        if(!shops[position].open){
            holder.cardView.background = context.getDrawable(R.drawable.rounded_cornerectangle_gray)
        }
        holder.viewMore.setOnClickListener {
            var intent = Intent(context!!, ViewShopProfile::class.java)
            intent.putExtra("ShopName",shops.get(position).shopName)
            intent.putExtra("ShopOwnerName",shops.get(position).ownerName)
            intent.putExtra("ShopCategory",shops.get(position).category)
            intent.putExtra("ShopPhonNo",shops.get(position).phoneNo)
            intent.putExtra("ShopLocationLat",shops.get(position).shopLocationLat)
            intent.putExtra("ShopLocationLang",shops.get(position).shopLocationLang)
            intent.putExtra("ShopTiming",shops.get(position).time)
            intent.putExtra("ShopEmail",shops.get(position).email)
            context.startActivity(intent)

            Log.d("Shop Details",shops.get(position).shopName+"is Clicked")
        }
    }
}

class ShopViewHolder(view: View) : RecyclerView.ViewHolder(view){

    val shopImage = view.shopImageView
    val shopName = view.name_of_shop
    val shopTime = view.opentime
    //    val shopPhoneNo = view.phonenumber
    val viewMore = view.viewMore
    val category = view.category
    val cardView = view.shopCardView
}
