package com.example.hackathon.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide

import com.example.hackathon.R
import com.example.hackathon.activities.LoginActivity
import com.example.hackathon.models.ShopModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_shopkeeper_profile.*

class ShopkeeperProfileFragment : Fragment() {


    lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore
    lateinit var sharedPref : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopkeeper_profile, container, false)
    }

    companion object {
        fun newInstance() = ShopkeeperProfileFragment()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        Log.d("Shop Details", auth.currentUser?.email)
        progressBar2.visibility = View.VISIBLE
        db.collection("Shops").document(user!!.uid).get().addOnSuccessListener {
            var shop = it.toObject(ShopModel::class.java)
            Log.d("Shop Details", shop.toString())


            Glide.with(context!!).load(shop!!.image).into(profileimage)
            ownername.text = shop!!.ownerName
            profilename.text = shop!!.shopName
            email.text = shop!!.email
            category.text= shop!!.category
            phonenumber.text = shop!!.phoneNo
            timings.text = shop!!.time
            progressBar2.visibility = View.INVISIBLE
        }

        signoutbtn.setOnClickListener {
            auth.signOut()
            sharedPref = activity!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            sharedPref.edit().putInt("KEY",0).apply()
            startActivity(Intent(context!!, LoginActivity::class.java))
        }
    }
}
