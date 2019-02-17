package com.biryanistudio.trashify

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val CLOSE_BINS = "http://trashify.net/closeBins"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        updateLocationFab.setOnClickListener {
            updateMaps()
        }
        showInfoFab.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage(
                    """Trashify is a platform which uses the user's input to display the closest trash disposal bins.
The main agenda with this platform was the bring awareness of a dire issue in modern times.
Waste disposal has become a huge menace to the environment.
The team behind Trashify plan to start with a small effort to contribute by creating awareness about the disposal bins in the city of New York.
The locations and information of the waste disposal bins are obtained from the Department of Sanitation of the City of New York.
                """.trimIndent()
                ).create().show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        setupMap()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=" +
                        marker?.position?.latitude.toString() +
                        "," +
                        marker?.position?.longitude.toString() +
                        "&travelmode=walking&dir_action=navigate"
            )
        )
        startActivity(intent)
        return true
    }

    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false

        updateMaps()
    }

    @SuppressLint("MissingPermission")
    private fun updateMaps() {
        mMap.clear()
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                CLOSE_BINS.httpGet(listOf("latitude" to lastLocation.latitude, "longitude" to lastLocation.longitude))
                    .responseJson { _, _, result ->
                        when (result) {
                            is Result.Success -> {
                                val data = result.get()
                                val dataArray = data.array()
                                for (i in 0 until dataArray.length()) {
                                    val bin = dataArray.getJSONObject(i)
                                    val markerLatLng = LatLng(
                                        bin.getDouble("latitude"), bin.getDouble("longitude")
                                    )
                                    runOnUiThread {
                                        mMap.addMarker(MarkerOptions().position(markerLatLng))
                                    }
                                }
                            }
                        }
                    }
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent)
                finish()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Location Access Required")
                    .setMessage("Trashify requires location access to identify the closest trash cans")
                    .setPositiveButton("Okay") { _, _ ->
                        startActivity(intent)
                        finish()
                    }.create()
                    .show()
            }
        }
    }
}
