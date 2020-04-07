package com.example.hackathon.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide

import com.example.hackathon.R
import com.example.hackathon.models.ShopModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.ngallazzi.fancyswitch.FancySwitch
import kotlinx.android.synthetic.main.fragment_shopkeeper_home.*

class ShopkeeperHomeFragment : Fragment() {

    lateinit var auth : FirebaseAuth
    lateinit var db : FirebaseFirestore
    var openClose : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopkeeper_home, container, false)
    }

    companion object {
        fun newInstance() = ShopkeeperHomeFragment()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        db.collection("Shops").document(user!!.uid).get().addOnSuccessListener {
            var shop = it.toObject(ShopModel::class.java)
            if(shop!!.open){
                shopOpenCloseSwitch.setState(FancySwitch.State.ON)

            }else{
                shopOpenCloseSwitch.setState(FancySwitch.State.OFF)

            }

        }.addOnFailureListener {
            Log.d("Users",it.message)
        }

        shopOpenCloseSwitch.setSwitchStateChangedListener(object : FancySwitch.SwitchStateChangedListener {
            override fun onChanged(newState: FancySwitch.State) {

                if(newState.name.contentEquals("ON")){
                    Log.d("Users", newState.name)
                    Log.d("Users",user!!.uid)
                    db.collection("Shops").document(user!!.uid).update("open",true).addOnSuccessListener {
                        Log.d("Users", newState.name)
                        Toast.makeText(context," Shop is Open", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Log.d("Users", it.message)
                    }
                }else{
                    db.collection("Shops").document(user!!.uid).update("open",false).addOnSuccessListener {
                        Toast.makeText(context," Shop is Close", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Log.d("Users", it.toString())
                    }
                }
            }
        })

    }
}