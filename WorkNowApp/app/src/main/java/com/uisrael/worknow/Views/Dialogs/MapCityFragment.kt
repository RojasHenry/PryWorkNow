package com.uisrael.worknow.Views.Dialogs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import com.uisrael.worknow.R
import kotlinx.android.synthetic.main.fragment_map_city.*


class MapCityFragment(private val applicationContext: Context,
                      val handlerResponse: IResponseMapFragment?,
                      val isOnlyView:Boolean,
                      val locationView:LatLng?,
                      val nameLocation:String?) : DialogFragment(), GoogleMap.OnMarkerDragListener{

    lateinit var mapGoogle: GoogleMap
    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 210
    val GPS_ENABLE = 211
    val DEFAULT_ZOOM = 15
    lateinit var mapFragment:SupportMapFragment
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastKnownLocation:Location
    var defaultLocation:LatLng = LatLng(0.0, 0.0)
    var locationManager: LocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var mSettingClient = LocationServices.getSettingsClient(applicationContext)
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationRequest: LocationRequest? = null
    lateinit var ubicacionLocation:LatLng
    lateinit var addressLocation:String
    init {
        mLocationRequest = LocationRequest.create()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = 1000
        mLocationRequest?.fastestInterval = 500
        if (mLocationRequest != null) {
            val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest!!)
            mLocationSettingsRequest = builder.build()
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mapGoogle = googleMap
        mapGoogle.uiSettings.isTiltGesturesEnabled = false
        mapGoogle.uiSettings.isMapToolbarEnabled = isOnlyView
        mapGoogle.setOnMarkerDragListener(this)
        if(isOnlyView){
            mapGoogle.uiSettings.isScrollGesturesEnabled = false
            mapGoogle.uiSettings.isRotateGesturesEnabled = false
            mapGoogle.uiSettings.isZoomGesturesEnabled = false
            mapGoogle.addMarker(MarkerOptions()
                .position(locationView)
                .title(nameLocation)
                .draggable(true))
            mapGoogle.moveCamera(CameraUpdateFactory.newLatLngZoom(locationView, DEFAULT_ZOOM.toFloat()))
        }else{
            if(locationView != null && nameLocation != null){
                if(getLocationPermission()){ // false
                    mapGoogle.addMarker(MarkerOptions()
                        .position(locationView)
                        .title(nameLocation)
                        .draggable(true))
                    mapGoogle.moveCamera(CameraUpdateFactory.newLatLngZoom(locationView, DEFAULT_ZOOM.toFloat()))
                    mapGoogle.isMyLocationEnabled = true

                    mapGoogle.setOnMyLocationButtonClickListener {
                        if (isLocationEnabled()){
                            btnMapaUbicacion.isEnabled = true
                            getDeviceLocation()
                            return@setOnMyLocationButtonClickListener true
                        }else{
                            btnMapaUbicacion.isEnabled = false
                            turnOnGPS()
                            return@setOnMyLocationButtonClickListener false
                        }
                    }
                }else{
                    activity?.let {
                        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                    }

                }
            }else{
                if(getLocationPermission()){ // true
                    if (isLocationEnabled()){
                        btnMapaUbicacion.isEnabled = true
                        getDeviceLocation()
                    }else{
                        btnMapaUbicacion.isEnabled = false
                        turnOnGPS()
                    }
                    mapGoogle.isMyLocationEnabled = true

                    mapGoogle.setOnMyLocationButtonClickListener {
                        if (isLocationEnabled()){
                            btnMapaUbicacion.isEnabled = true
                            getDeviceLocation()
                            return@setOnMyLocationButtonClickListener true
                        }else{
                            btnMapaUbicacion.isEnabled = false
                            turnOnGPS()
                            return@setOnMyLocationButtonClickListener false
                        }
                    }
                }else{
                    activity?.let {
                        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                    }

                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.fragment_map_city, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Places.initialize(applicationContext, applicationContext.getString(R.string.google_maps_key))
        Places.createClient(applicationContext)

        fusedLocationProviderClient = activity?.let {
            LocationServices.getFusedLocationProviderClient(
                it
            )
        }!!

        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(callback)

        val autocompleteField =
            childFragmentManager.findFragmentById(R.id.autocompleteMapaUbicacion)
                    as AutocompleteSupportFragment

        autocompleteField.setPlaceFields(listOf(Place.Field.LAT_LNG,Place.Field.ADDRESS))
        autocompleteField.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("MAPA", "Place: ${place.address}, ${place.latLng}")
                ubicacionLocation = place.latLng?.let { LatLng(it.latitude, place.latLng!!.longitude) }!!
                addressLocation = place.address.toString()
                mapGoogle.clear()
                mapGoogle.addMarker(MarkerOptions()
                    .position(ubicacionLocation)
                    .title(place.address.toString())
                    .draggable(true))
                mapGoogle.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionLocation, DEFAULT_ZOOM.toFloat()))
            }

            override fun onError(status: Status) {
                Log.i("MAPA", "An error occurred: $status")
            }

        })

        if(isOnlyView){
            btnMapaUbicacion.isVisible = false
            titleMapaUbicacion.text = Editable.Factory.getInstance().newEditable(nameLocation)
            rltautocompleteMapaUbicacion.isVisible = false
        }else{
            btnMapaUbicacion.setOnClickListener {
                handlerResponse?.responseMap("${ubicacionLocation}%DIR%address(${addressLocation})")
                dismiss()
            }
        }
        imgCloseMapaUbicacion.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        mapFragment.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapFragment.onStop()
    }

    private fun getLocationPermission():Boolean {
        return (ContextCompat.checkSelfPermission(this.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    val ubicacion = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                    addressLocation = "Ubicación Actual"
                    mapGoogle.clear()
                    mapGoogle.addMarker(MarkerOptions()
                        .position(ubicacion)
                        .title("Ubicación Actual")
                        .draggable(true))
                    mapGoogle.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            lastKnownLocation.latitude,
                            lastKnownLocation.longitude), DEFAULT_ZOOM.toFloat()))

                    ubicacionLocation = LatLng( lastKnownLocation.latitude, lastKnownLocation.longitude)
                } else {
                    mapGoogle.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                    mapGoogle.uiSettings.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION ->{
                val perms: MutableMap<String, Int> = HashMap()
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] = PackageManager.PERMISSION_GRANTED
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) perms[permissions[i]] = grantResults[i]
                    if (perms[Manifest.permission.ACCESS_COARSE_LOCATION] === PackageManager.PERMISSION_GRANTED) {
                        if (isLocationEnabled()){
                            btnMapaUbicacion.isEnabled = true
                            getDeviceLocation()
                        }else{
                            btnMapaUbicacion.isEnabled = false
                            turnOnGPS()
                        }
                        if(!isOnlyView){
                            mapGoogle.isMyLocationEnabled = true
                            mapGoogle.setOnMyLocationButtonClickListener {
                                if (isLocationEnabled()){
                                    btnMapaUbicacion.isEnabled = true
                                    getDeviceLocation()
                                    return@setOnMyLocationButtonClickListener true
                                }else{
                                    btnMapaUbicacion.isEnabled = false
                                    turnOnGPS()
                                    return@setOnMyLocationButtonClickListener false
                                }
                            }
                        }
                    }else{
                        showDialogOK("Esta aplicación requiere el uso de gps.") { _, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> activity?.let { requestPermissions(
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                                }
                            }
                        }
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .create()
            .show()
    }

    @SuppressLint("ShowToast")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GPS_ENABLE) {
            getDeviceLocation()
            btnMapaUbicacion.isEnabled = true
            Snackbar
                .make(
                    rltMapaUbicacion,
                    "GPS habilitado, vuelva a intentarlo.",
                    Snackbar.LENGTH_SHORT
                )
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(resources.getColor(R.color.black))
                .show()
        } else {
            btnMapaUbicacion.isEnabled = false
            Snackbar
                .make(
                    rltMapaUbicacion,
                    "Para continuar se requiere la activación del GPS.",
                    Snackbar.LENGTH_SHORT
                )
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(resources.getColor(R.color.black))
                .show()
        }
    }

    @SuppressLint("ShowToast")
    fun turnOnGPS() {
        activity?.let {
            mSettingClient?.checkLocationSettings(mLocationSettingsRequest)
                ?.addOnSuccessListener(it) {
                    Log.d("GPS", "GPS activado")
                }
                ?.addOnFailureListener { ex ->
                    if ((ex as ApiException).statusCode
                        == LocationSettingsStatusCodes.RESOLUTION_REQUIRED
                    ) {
                        try {
                            val resolvableApiException = ex as ResolvableApiException
                            resolvableApiException.startResolutionForResult(
                                activity,
                                GPS_ENABLE
                            )
                        } catch (e: Exception) {
                            Log.d("GPS", "turnOnGPS: Unable to start default functionality of GPS")
                        }

                    } else {
                        if (ex.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                            val errorMessage = "Se requiere acceder a las configuraciones de ubicación para arreglar el problema."
                            Snackbar
                                .make(rltMapaUbicacion, errorMessage, Snackbar.LENGTH_SHORT)
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.black))
                                .show()
                        }
                    }
                }
        }
    }

    override fun onMarkerDragStart(p0: Marker) {

    }

    override fun onMarkerDrag(p0: Marker) {

    }

    override fun onMarkerDragEnd(marker: Marker) {
        ubicacionLocation = marker.position
        addressLocation = if(marker.title == "Ubicación Actual") "Ubicación Escogida" else marker.title
        mapGoogle.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, DEFAULT_ZOOM.toFloat()))
    }
}
