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
import com.example.hackathon.models.CustomerModel
import com.example.hackathon.models.ShopModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_customer_profile.*


class CustomerProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_customer_profile, container, false)
    }

    companion object {
        fun newInstance() = CustomerProfileFragment()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val user = auth.currentUser
        Log.d("Shop Details", auth.currentUser?.email)
        cst_profile_progress?.visibility = View.VISIBLE
        db.collection("Users").document(user!!.uid).get().addOnSuccessListener {
            var customerModel = it.toObject(CustomerModel::class.java)
            cst_name.text = customerModel!!.name
            cst_email.text = customerModel!!.email
            cst_phonenumber.text = customerModel!!.phoneNo
            cst_profile_progress?.visibility = View.INVISIBLE
        }

        cst_signoutbtn.setOnClickListener {
            auth.signOut()
            sharedPref = activity!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            sharedPref.edit().putInt("KEY",0).apply()
            startActivity(Intent(context!!, LoginActivity::class.java))
        }
    }
}
