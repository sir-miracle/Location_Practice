package com.example.locationpractice

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import com.google.android.gms.location.*
import java.util.*

class MainActivity : AppCompatActivity(){
    var permissionId = 100
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
     private lateinit var locationRequest: LocationRequest
    lateinit var locattionText: TextView
    lateinit var getLocationBtn: Button

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //instantiate the fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getLocationBtn = findViewById(R.id.location_permssion_practice_btn)
        getLocationBtn.setOnClickListener {
            getLastLocation()
        }

    }

    //A function to get the last location
    @RequiresApi(Build.VERSION_CODES.S)
    private fun getLastLocation(){
        //first you check permission

        if (checkPermission()){
            //entering here means permission is granted
            //then we check if the location services is enabled
            if (isLocationServiceEnabled()){

                //now get the last known location
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if(location == null){
                        //if the location is null, we get the new location
                        getNewLocation()

                    }else{

                        //getNewLocation()

                        //since the location is not null, we display the location
                      locattionText = findViewById(R.id.text_location)
                      locattionText.text = "Your current Location cordinates are: \n Lat: ${location.latitude} \n Long: ${location.longitude}"+ "\n " +
                              "Your city name is: ${getCityName(location.latitude, location.longitude)} \n " +
                              "your country name is: ${getCountryName(location.latitude, location.longitude)}"

                    }
                }

            }else{

                Toast.makeText(this, "Please enable your location services from settings", Toast.LENGTH_SHORT).show()
            }

        }

        else{
            requestPermission()
        }
    }



    private fun isFineLocationGranted() =
        checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    private fun isCoarseLocationGranted() =
        checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        //check is permissions is/are granted
    private fun checkPermission():Boolean{
        if (isFineLocationGranted()||isCoarseLocationGranted()){
            return true
        }
        return false
    }

        // if permissions are not granted, request for them
    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf
            (Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),permissionId)

    }

    //check if location service of the device is enabled

    private fun isLocationServiceEnabled():Boolean{
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun getNewLocation(){
        locationRequest = LocationRequest.create()

        locationRequest.apply {
            locationRequest.priority = android.location.LocationRequest.QUALITY_HIGH_ACCURACY
            locationRequest.interval = 2
            locationRequest.fastestInterval = 0
            locationRequest.numUpdates = 2
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,locationCallback, Looper.myLooper())

             }
    }

    private  val locationCallback = object: LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val newLocation = p0.lastLocation
            locattionText = findViewById(R.id.text_location)
            locattionText.text =
                "Your current Location cordinates are: \n " +  "\n " +
                        "Lat:${newLocation.latitude} \n Long: ${newLocation.longitude} \n " + "\n " +
                        "Your city name is: ${getCityName(newLocation.latitude, newLocation.longitude)} \n " +
                        "your country name is: ${getCountryName(newLocation.latitude, newLocation.longitude)}"

        }
    }

    //function to get the city name
    private fun getCityName(lat: Double, long: Double): String{
        var cityName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, long,1)
        cityName = address.get(0).locality
        return cityName
    }

    //function to get the country name
    private fun getCountryName(lat: Double, long: Double): String{
        var theCountryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var address = geoCoder.getFromLocation(lat, long,1)
        theCountryName = address.get(0).countryName
        return theCountryName
    }







    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionId){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
        }
    }

}















































//var  permissionBtn = findViewById<Button>(R.id.location_permssion_practice)
//permissionBtn.setOnClickListener {
//
//    if(!isLocationPermissionGranted()){
//        grantLocationPermission()
//    }else{
//        Toast.makeText(this, " Permission granted", Toast.LENGTH_SHORT).show()
//        permissionBtn.text = "Location Permission has been granted"
//        return@setOnClickListener
//
//    }
//}

//private fun isLocationPermissionGranted()  =
//    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//
//@RequiresApi(Build.VERSION_CODES.M)
//fun  grantLocationPermission(){
//
//    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
//        AlertDialog.Builder(this).setTitle(" Location Permission needed")
//            .setMessage("For the app to work fine, you have to give the permission to access your location")
//            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
//                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),100)
//            })
//            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
//                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
//                dialog.dismiss() }).create().show()
//
//    }else{
//        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),100)
//    }
//
//}