package com.example.hackathon.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
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
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point

import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.android.synthetic.main.fragment_customer_home.*

class CustomerHomeFragment : Fragment(),OnMapReadyCallback, PermissionsListener {

    private lateinit var map: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var locationEngine: LocationEngine
    private lateinit var callback: LocationChangeListeningCallback

    private var shopList : ArrayList<ShopModel> = ArrayList()



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
        const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
        const val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
        const val  SOURCE_ID = "SOURCE_ID"
        const val  ICON_ID = "ICON_ID"
        const val  LAYER_ID = "LAYER_ID"
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         map_view.onCreate(savedInstanceState)
         map_view.getMapAsync(this)

     }

    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        callback = LocationChangeListeningCallback()
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it)
        }
    }
    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(context!!)) {
            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(context!!, loadedMapStyle)
                .useDefaultLocationEngine(false)
                .build()
            map.locationComponent.apply {
                activateLocationComponent(locationComponentActivationOptions)
                isLocationComponentEnabled = true                       // Enable to make component visible
                cameraMode = CameraMode.TRACKING                        // Set the component's camera mode
                renderMode = RenderMode.COMPASS                         // Set the component's render mode
            }
            initLocationEngine()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
    }

    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(context!!)
        val request = LocationEngineRequest
            .Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
            .build()
        locationEngine.requestLocationUpdates(request, callback, Looper.getMainLooper())
        locationEngine.getLastLocation(callback)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private inner class LocationChangeListeningCallback :
        LocationEngineCallback<LocationEngineResult> {

        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation ?: return //BECAREFULL HERE, IF NAME LOCATION UPDATE DONT USER -> val resLoc = result.lastLocation ?: return
            if (result.lastLocation != null){
                val lat = result.lastLocation?.latitude!!
                val lng = result.lastLocation?.longitude!!
                val latLng = LatLng(lat, lng)

                if (result.lastLocation != null) {
                    map.locationComponent.forceLocationUpdate(result.lastLocation)
                    val position = CameraPosition.Builder()
                        .target(latLng)
                        .zoom(13.0) //disable this for not follow zoom
                        .tilt(10.0)
                        .build()
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(position))
                    shopFromDB(latLng)
//                    initAddMarker(map)
                }
            }

        }

        override fun onFailure(exception: Exception) {}
    }

    private fun initAddMarker(map: MapboxMap) {
        val symbolLayers = ArrayList<Feature>()
        for(d in shopList)
            symbolLayers.add(Feature.fromGeometry(Point.fromLngLat(d.shopLocationLang, d.shopLocationLat)))
        map.setStyle(
            Style.Builder().fromUri(Style.MAPBOX_STREETS)
                .withImage(ICON_ID, BitmapUtils
                    .getBitmapFromDrawable(ContextCompat.getDrawable(context!!, R.drawable.mapbox_marker_icon_default))!!)
                .withSource(GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(symbolLayers)))
                .withLayer(
                    SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                            PropertyFactory.iconImage(ICON_ID),
                            PropertyFactory.iconSize(1.0f),
                            PropertyFactory.iconAllowOverlap(true),
                            PropertyFactory.iconIgnorePlacement(true)
                        ))
        )
        {
            //Here is style loaded
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(context!!, "Permission not granted!!", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            map.getStyle {
                enableLocationComponent(it)
            }
        } else {
            Toast.makeText(context!!, "Permission not granted!! app will be EXIT", Toast.LENGTH_LONG).show()
            Handler().postDelayed({
                activity!!.finish()
            }, 3000)
        }
    }

    override fun onStart() {
        super.onStart()
        map_view.onStart()
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onStop() {
        super.onStop()
        map_view.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Prevent leaks
        locationEngine.removeLocationUpdates(callback)
        map_view.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
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
                        if(     userLat-0.01 <= shop.shopLocationLat
                            && shop.shopLocationLat <= userLat+0.01
                            && userLang -0.01 <= shop.shopLocationLang
                            && shop.shopLocationLang <= userLang+0.01){

                            shopList.add(shop)
                        }
                    }
                }
//                Toast.makeText(applicationContext!!,"Shop List has"+shopList.size.toString()+" Elements",Toast.LENGTH_LONG).show()

            }
        }.addOnFailureListener {
            Toast.makeText(context!!,"Check your Internet",Toast.LENGTH_SHORT).show()
        }
        Log.d("User Details","Shop List has "+shopList.size.toString()+" Elements")
        if(shopList.size !=0) initAddMarker(map)
    }


}
