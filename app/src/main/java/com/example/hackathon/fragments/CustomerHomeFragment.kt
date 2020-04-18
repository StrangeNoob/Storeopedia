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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_customer_home.*

class CustomerHomeFragment : Fragment(),OnMapReadyCallback {

    private lateinit var sharedPref: SharedPreferences
    private var shopList: ArrayList<ShopModel> = ArrayList()
    private lateinit var googleMap: GoogleMap
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (10 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
    val PERMISSION_ID = 42

    private var latitude = 0.0
    private var longitude = 0.0
    private var mapSuported: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_customer_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        try {
            MapsInitializer.initialize(activity!!)
        } catch (e: GooglePlayServicesNotAvailableException) {
            mapSuported = false
        }
        if (mapView != null) {
            mapView.onCreate(savedInstanceState)
        }
    }

    companion object {
        fun newInstance() = CustomerHomeFragment()
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Prevent leaks
        mapView.onDestroy()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.getMapAsync(this)
        sharedPref = activity!!.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        sharedPref.edit().putInt("KEY", 1).apply()
        Log.d("Shared Prefs ", sharedPref.getInt("KEY", 0).toString())
    }

    private fun shopFromDB(userLatLng: LatLng) {

        Log.d("User Details", userLatLng.toString())
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
                        if (userLat - 0.2 <= shop.shopLocationLat
                            && shop.shopLocationLat <= userLat + 0.2
                            && userLang - 0.2 <= shop.shopLocationLang
                            && shop.shopLocationLang <= userLang + 0.2
                        ) {
                            Log.d("User Detail",shop.toString())
                            shopList.add(shop)
                        }
                    }
                }
                Log.d("User Details", "Shop List has " + shopList.size.toString() + " Elements")
                Toast.makeText(
                    context!!,
                    "Your Neighbourhood has " + shopList.size.toString() + " Shops ",
                    Toast.LENGTH_LONG
                ).show()
                if(shopList.size !=0) initAddMarker()
            }
        }.addOnFailureListener {
            Toast.makeText(context!!, "Check your Internet", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(map: GoogleMap?) {

        map?.let {
            googleMap = it
            it.setMinZoomPreference(15.0F)
        }
        if (googleMap != null) {

            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition
                        .builder()
                        .target(LatLng(latitude, longitude)).zoom(15F).build()
                )
            )
            googleMap.isMyLocationEnabled = true

        }
    }
    private fun initAddMarker() {
        for (d in shopList){
            googleMap.addMarker(MarkerOptions().position(LatLng(d.shopLocationLat, d.shopLocationLang)).title(d.shopName).snippet(d.category + "\n"))
        }
    }
    protected fun startLocationUpdates() {
        // initialize location request object
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.run {
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            setInterval(UPDATE_INTERVAL)
            setNumUpdates(3)
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
        if (Build.VERSION.SDK_INT >= 23 ) {
            if(checkPermissions()){
                Log.d("User Details",checkPermissions().toString())
                if(isLocationEnabled()){
                    Log.d("User Details",isLocationEnabled().toString())
                    LocationServices.getFusedLocationProviderClient(activity!!)
                        .requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
                }else {
                    Toast.makeText(context!!, "Turn on location", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                requestPermissions()
            }
        }
    }

    //
    private fun onLocationChanged(location: Location) {
        // create message for toast with updated latitude and longitude
        var msg = "Updated Location: " + location.latitude + " , " + location.longitude
        Log.d("User Detail",msg)
        val location = LatLng(location.latitude, location.longitude)
        shopFromDB(location)
        googleMap!!.clear()
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))

    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
               registerLocationListner()
            }
        }
    }


}

