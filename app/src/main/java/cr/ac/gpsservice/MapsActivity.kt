package cr.ac.gpsservice

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import cr.ac.gpsservice.databinding.ActivityMapsBinding
import cr.ac.gpsservice.db.LocationDatabase
import cr.ac.gpsservice.entity.Location

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val SOLICITA_GPS = 1
    private lateinit var mLocationClient : FusedLocationProviderClient // Proveedor de Localización Google
    private lateinit var mLocationRequest : LocationRequest
    private lateinit var mLocationCallback: LocationCallback

    private lateinit var locationDatabase: LocationDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationDatabase = LocationDatabase.getInstance(this)

        mLocationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {

                if(mMap  != null){
                    if(locationResult.equals(null))
                        return

                }// Dibujar en el Mapa los Puntos
                for(location in locationResult.locations){

                    val sydney = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

                    locationDatabase.locationDao.insert(Location(null,location.latitude,location.longitude))

                }
            }
        }

        mLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)


        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        validaPermisos()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        iniciarServicio()
        recuperarPuntos()

    }

    /**
     * Obtener los puntos almacenados en la bd y mostrarlos en el mapa
     */

    fun recuperarPuntos(){
        var lista : List<Location>  = locationDatabase.locationDao.query()

        for(loc in lista) {
            // Add a marker in Sydney and move the camera
            val sydney = LatLng(loc.latitude, loc.longitude)
            mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        }
    }

    /**
     *Hace un filtro del broadcast GPS (cr.ac.gpsservice.GPS_EVENT)
     * E inicia el servicio (startService) GpsService
     */

    fun iniciarServicio(){

    }

    /**
     * Valida si la app tiene permisos de ACCESS_FINE_LOCATION
     * si no tiene permisos solicita al usuario oermisos (requestPermissions)
     */

    fun validaPermisos(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //No Tengo Permisos
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                SOLICITA_GPS
            )
        }else{
            mLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, null
            )
        }
    }

    /**
     *
     */

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            SOLICITA_GPS -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // El Usuario dio permisos
                    mLocationClient.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback, null
                    )
                }
                else{//El Usuario no dio permisos de GPS
                    System.exit(1)
                }
            }
        }
    }

    /**
     * Es la clase para recibir los mensajes de broadcast
     */

    class ProgressReceiver : BroadcastReceiver(){

        /**
         * Se obtiene el parametro enviado por el servicio (Loccation)
         * Coloca en el mapa la localización
         * Mueve la cámara a esa localización
         */

        override fun onReceive(p0: Context?, p1: Intent?) {

        }
    }
}