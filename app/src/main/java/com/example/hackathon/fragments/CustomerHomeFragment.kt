package com.example.hackathon.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat

import androidx.fragment.app.Fragment
import com.example.hackathon.R
import com.google.android.gms.location.*

import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import kotlinx.android.synthetic.main.fragment_customer_home.*

class CustomerHomeFragment : Fragment() {

    private  val mapboxMap: MapboxMap ? =null
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
            Mapbox.getInstance(context!!, getString(R.string.access_token))
            return inflater.inflate(R.layout.fragment_customer_home, container, false)
    }

    companion object {
        fun newInstance() = CustomerHomeFragment()

    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


         mapview.onCreate(savedInstanceState)
         // Initializing is asynchrounous- getMapAsync will return a map
         mapview.getMapAsync { map ->
             // Set one of the many styles available
             map.setStyle(Style.OUTDOORS) { style ->
                  Style.MAPBOX_STREETS //| Style.SATELLITE etc...
             }
         }
         mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
         getLastLocation()
         getLocation.setOnClickListener {
             getLastLocation()
         }

     }
//    Map Box Setting Starts
     override fun onStart() {
        super.onStart()
        mapview.onStart()

    }

     override fun onResume() {
        super.onResume()
        mapview.onResume()
    }

     override fun onPause() {
        super.onPause()
        mapview.onPause()
    }

     override fun onStop() {
        super.onStop()
        mapview.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapview.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapview.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapview.onSaveInstanceState(outState)
    }

    fun addMarker(lat: Double, lang:Double){
        mapview.getMapAsync { map->
            map?.addMarker(
                MarkerOptions()
                .position(LatLng(lat, lang))
                .title("Home"))
        }
    }
    fun ChangeLocation(lat: Double,lang: Double){
        Log.d("Location in Change() ",lat.toString()+" , "+lang.toString())
        val position = CameraPosition.Builder()
            .target(LatLng(lat,lang))
            .zoom(15.0)
            .tilt(20.0)
            .build()

        mapview.getMapAsync { map ->
            // Set one of the many styles available
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 4000)
            map?.addMarker(
                MarkerOptions()
                    .position(LatLng(lat, lang))
                    .title("Home"))

        }
    }
//    Map Box Setting Ends
//    Location Setting Starts

    @SuppressLint("MissingPermission")
    public fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(activity!!) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        Log.d("Location",location.toString())
                        ChangeLocation(location.latitude,location.longitude)
                    }
                }
            } else {
                Toast.makeText(context!!, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            Log.d("Last Location",mLastLocation.toString())
            ChangeLocation(mLastLocation.latitude,mLastLocation.longitude)
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
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
                getLastLocation()
            }
        }
    }

}
