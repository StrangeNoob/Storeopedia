package com.example.hackathon.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import com.example.hackathon.R
import com.example.hackathon.models.ShopModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_customer_home.*

class CustomerHomeFragment : Fragment(),OnMapReadyCallback {

    private lateinit var sharedPref: SharedPreferences
    private var shopList : ArrayList<ShopModel> = ArrayList()
    private lateinit var googleMap : GoogleMap
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (10 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */

    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_customer_home, container, false)
    }

    companion object {
        fun newInstance() = CustomerHomeFragment()
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)

         mapView.onCreate(savedInstanceState)
         mapView.getMapAsync(this)
         sharedPref = activity!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
         sharedPref.edit().putInt("KEY",1).apply()
         Log.d("Shared Prefs ",sharedPref.getInt("KEY",0).toString())
     }

    private fun shopFromDB(userLatLng: LatLng) {

        val db = FirebaseFirestore.getInstance()
        var userLat = userLatLng.latitude
        var userLang = userLatLng.longitude
        db.collection("Shops").get().addOnSuccessListener {
            var list = it.documents
            if (list.isNotEmpty()) {
                shopList.clear()
                for (d in list) {

                    var shop = d.toObject(ShopModel::class.java)
                    if (shop != null) {
                        if(     userLat-0.2 <= shop.shopLocationLat
                            && shop.shopLocationLat <= userLat+0.2
                            && userLang-0.2 <= shop.shopLocationLang
                            && shop.shopLocationLang <= userLang+0.2){
                            shopList.add(shop)
                        }
                    }
                }
                Log.d("User Details","Shop List has "+shopList.size.toString()+" Elements")
                Toast.makeText(context!!,"Your Neighbourhood has "+shopList.size.toString()+" Shops ",Toast.LENGTH_LONG).show()

            }
        }.addOnFailureListener {
            Toast.makeText(context!!,"Check your Internet",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(map: GoogleMap?) {

        map?.let {
            googleMap = it
        }
        if (googleMap != null) {
            googleMap!!.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("Current Location"))
        }

    }
    protected fun startLocationUpdates() {
        // initialize location request object
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.run {
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            setInterval(UPDATE_INTERVAL)
            setFastestInterval(FASTEST_INTERVAL)
        }

        // initialize location setting request builder object
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        // initialize location service object
        val settingsClient = LocationServices.getSettingsClient(activity!!)
        settingsClient!!.checkLocationSettings(locationSettingsRequest)

        // call register location listener
        registerLocationListner()
    }

    private fun registerLocationListner() {
        // initialize location callback object
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.getLastLocation())
            }
        }
        // 4. add permission if android version is greater then 23
        if(Build.VERSION.SDK_INT >= 23 && checkPermission()) {
            LocationServices.getFusedLocationProviderClient(activity!!).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
        }
    }

    //
    private fun onLocationChanged(location: Location) {
        // create message for toast with updated latitude and longitudefa
        var msg = "Updated Location: " + location.latitude  + " , " +location.longitude

        // show toast message with updated location
        //Toast.makeText(this,msg, Toast.LENGTH_LONG).show()
        val location = LatLng(location.latitude, location.longitude)
        googleMap!!.clear()
        googleMap!!.addMarker(MarkerOptions().position(location).title("Current Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }

    private fun checkPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(context!! , android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions()
            return false
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(activity!!, arrayOf("Manifest.permission.ACCESS_FINE_LOCATION"),1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION) {
                registerLocationListner()
            }
        }
    }
}
