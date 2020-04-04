package com.example.hackathon.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.hackathon.R
import com.example.hackathon.models.ShopModel
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_shopkeeper_registration.*
import kotlinx.android.synthetic.main.fragment_loading.*
import java.io.ByteArrayOutputStream

class ShopkeeperRegistrationActivity : AppCompatActivity() {

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var shopmodel: ShopModel
    var mStorageRef = FirebaseStorage.getInstance().getReference()
    lateinit var auth : FirebaseAuth
    lateinit var db : FirebaseFirestore
    var imageUri: String = ""
    var fl = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopkeeper_registration)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        shopmodel= ShopModel()
        spinnerView.apply {
            lifecycleOwner = this@ShopkeeperRegistrationActivity
            setOnSpinnerItemSelectedListener<String> { index, text ->
                if(text.contentEquals("Others")){
                    shopDescription.visibility=visibility
                }else{
                    shopDescription.visibility=View.INVISIBLE
                    shopmodel.category=text
                }
            }
        }

        shopImage.setOnClickListener{
            captureImage()
        }
        shopLocation.setOnClickListener{
            getLastLocation()
        }
        shopDescription.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                shopmodel!!.category=s.toString()
            }
        })
        create_shop_btn.setOnClickListener{
            if(validateInput()){
                Log.d("Shop Details", "Firebase Data Upload Started")
                createShopAccount()
                Log.d("Shop Details","Firebase Data Upload Done")

            }
        }
    }

    // To Get Location
    @SuppressLint("MissingPermission")
    public fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        shopmodel!!.shopLocationLat = location.latitude
                        shopmodel!!.shopLocationLang = location.longitude
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            shopmodel!!.shopLocationLat = mLastLocation.latitude
            shopmodel!!.shopLocationLang = mLastLocation.longitude
            Log.d("Shop Details Location",mLastLocation.toString())
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
    // End of Get Location

    // Validate All inputs
    fun validateInput(): Boolean {
        Log.d("Shop Details ",shopName.text.toString()+" "+shopOwner.text.toString()+" "+shopEmail.text.toString()+" "+shopNo.text.toString())

        if(shopmodel!!.shopLocationLang == null || shopmodel!!.shopLocationLat == null){
            Toast.makeText(this,"Click Shop Location Button",Toast.LENGTH_LONG).show()
            return false
        }
        if( shopName.text.toString() == null ||
            shopOwner.text.toString() == null ||
            shopEmail.text.toString() == null ||
            shopNo.text.toString() == null ||
            fl==0 ||
            shopPassword.text.toString() == null
        ) {
            shopName.error="Shop Info cannot be Blank"
            return false
        }
        if (!isValidEmail(shopEmail.text.toString())){
            shopEmail.error="Enter Vaild Email"
            return false
        }
        if(shopNo.text.toString().length != 10) {
            shopNo.error = "Enter Valid Phone No "
            return false
        }
        shopmodel!!.shopName=shopName.text.toString()
        shopmodel!!.ownerName=shopOwner.text.toString()
        shopmodel!!.email=shopEmail.text.toString().toLowerCase()
        shopmodel!!.phoneNo=shopNo.text.toString().toLong()
        return true
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
    // To Get/Capture Picture
    fun captureImage() {

        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 1)
            } else if (options[item] == "Choose from Gallery") {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2)

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })

        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check which request we're responding to
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    val photo = data?.extras!!.get("data") as Bitmap
                    fl=1
                    Log.d("Shop Details PhotoURI",photo.toString())
                    shopImage.setImageBitmap(photo)
                }  else if (requestCode == 2) {
                    fl=1
                    var img_uri = data?.data
                    Log.d("Shop Details PhotoURI",imageUri)
                    shopImage.setImageURI(img_uri)
                }
            }
    }

    //Submit Hangling Code

    private fun createShopAccount() {

        uploadprogressbar?.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(shopmodel.email,shopPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Shop Details", "Firebase Data Upload Started in "+auth.currentUser!!.email.toString())
                    val user = auth.currentUser
                    var data: ByteArray? = null
                    val bitmap = (shopImage.getDrawable() as BitmapDrawable).bitmap
                    fl=1
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
                    data = baos.toByteArray()
                    db.collection("Shops").add(shopmodel).addOnSuccessListener {
                        it.id

                        val id = it.id

                        db.collection("Shops").document(it.id)
                            .update("id",it.id).addOnSuccessListener {

                                Log.d("Shop Details", "Firebase Data Upload Started"+id.toString())
                                if(fl == 1) {

                                    val farmerImgRef = mStorageRef.child("shopImage/" + user!!.uid + ".jpg")

                                    if (data != null) {
                                        farmerImgRef.putBytes(data).addOnSuccessListener {

                                            farmerImgRef.downloadUrl.addOnSuccessListener {
                                                Log.d("Shop Details", "Firebase Data Upload Started "+it.toString())
                                                db.collection("Shops").document(id)
                                                    .update("image",it.toString()).addOnSuccessListener {
                                                        Toast.makeText(this,"This Shop is added to database",Toast.LENGTH_SHORT).show()
                                                        startActivity(Intent(applicationContext,ShopkeeperDashboardActivity::class.java))
                                                    }
                                            }
                                        }.addOnProgressListener {
                                            uploadprogressbar.visibility = View.VISIBLE
                                        }.addOnFailureListener {
                                            uploadprogressbar.visibility = View.INVISIBLE
                                            Log.d("Shops Details","Problem Occured in Uploading Pic")
                                            Toast.makeText(this,"Problem Occurred",Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(this,"Item added to database",Toast.LENGTH_LONG).show()
                                    onBackPressed()
                                }

                            }.addOnFailureListener {
                                Log.d("Shops Details","Problem Occured in Uploading Pic")
                                Toast.makeText(this,"Problem Occurred",Toast.LENGTH_LONG).show()
                            }
                    }
                }else{
                    Toast.makeText(this,"Check Your Internet Concetion",Toast.LENGTH_LONG).show()
                }
            }

    }
}
