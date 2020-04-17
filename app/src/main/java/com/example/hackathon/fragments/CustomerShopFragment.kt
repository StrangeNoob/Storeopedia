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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.example.hackathon.R
import com.example.hackathon.adapters.ShopAdapter
import com.example.hackathon.models.ShopModel
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.fragment_customer_shop.*
import java.util.*
import kotlin.collections.ArrayList


class CustomerShopFragment : Fragment(), SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename and change types of parameters

    var adapter : ShopAdapter ?=null
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var userLat : Double =0.0
    var userLang : Double =0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_shop, container, false)
    }

    companion object {
        fun newInstance() = CustomerShopFragment()
        var ShopList : ArrayList<ShopModel> = ArrayList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        adapter = ShopAdapter(ShopList,context!!)
        shopRecyclerView.adapter = adapter
        shopPB?.visibility=View.INVISIBLE
        shopRecyclerView.layoutManager = LinearLayoutManager(activity)
        updateShop()

        shopSearch!!.setOnQueryTextListener(this)

        shopRefresh.setOnRefreshListener {

            updateShop()
            shopRefresh.isRefreshing=false
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        Toast.makeText(this.context,"Not Found ", Toast.LENGTH_LONG).show()
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        filter(newText)
        return true
    }
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        var searchOrder : ArrayList<ShopModel> = ArrayList()
        if (charText.length == 0) {
            searchOrder.addAll(ShopList)
        } else {
            searchOrder.clear()
            for (wp in ShopList) {
                if (wp.shopName.toLowerCase(Locale.getDefault()).contains(charText) || wp.category.toLowerCase(Locale.getDefault()).contains(charText)  ) {
                    searchOrder.add(wp)
                }
            }
        }
        if(searchOrder.isEmpty())
            Toast.makeText(this.context,"Not Found ", Toast.LENGTH_LONG).show()
        shopRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ShopAdapter(searchOrder,context)
        }

    }

    override fun onRefresh() {

    }

    // Database Connection and Update
    fun updateShop(){

        val db = FirebaseFirestore.getInstance()

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        db.firestoreSettings = settings
        getLastLocation()
        shopPB?.visibility = View.VISIBLE
        db.collection("Shops").get().addOnSuccessListener {
            var list = it.documents
            if(list.isNotEmpty()){

                ShopList.clear()

                for ( d in list){

                    var shop =d.toObject(ShopModel::class.java)
                    if(shop !=null){
                        if(userLat-0.2 <= shop.shopLocationLat
                            && shop.shopLocationLat <= userLat+0.2
                            && userLang -0.2 <= shop.shopLocationLang
                            && shop.shopLocationLang <= userLang+0.2){

                            ShopList.add(shop)
                        }
                    }
                }
                shopPB?.visibility=View.INVISIBLE
                adapter!!.notifyDataSetChanged()
            }else{
                shopPB?.visibility=View.INVISIBLE
            }
        }
    }

    // User Location

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
                        userLat=location.latitude
                        userLang=location.longitude
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
            userLat=mLastLocation.latitude
            userLang=mLastLocation.longitude
        }
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
                getLastLocation()
            }
        }
    }


}
