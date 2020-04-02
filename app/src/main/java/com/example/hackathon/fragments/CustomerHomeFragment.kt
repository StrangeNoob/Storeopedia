package com.example.hackathon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hackathon.R

import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import kotlinx.android.synthetic.main.fragment_customer_home.*

class CustomerHomeFragment : Fragment() {

    private val mapView: MapView? = null
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
                 // Style.MAPBOX_STREETS | Style.SATELLITE etc...
             }
         }
     }
    public override fun onStart() {
        super.onStart()
        mapview.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapview.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapview.onPause()
    }

    public override fun onStop() {
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
}
