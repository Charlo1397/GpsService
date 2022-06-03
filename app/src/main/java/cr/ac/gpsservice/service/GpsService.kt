package cr.ac.gpsservice.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.os.Looper
import com.google.android.gms.location.*
import cr.ac.gpsservice.db.LocationDatabase
import cr.ac.gpsservice.entity.Location


class GpsService : IntentService("GpsService") {

    lateinit var locationCallback: LocationCallback
    private lateinit var locationDatabase: LocationDatabase
    private lateinit var locationRequest : LocationRequest
    lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        var GPS = "cr.ac.gpsservice.GPS_EVENT"
    }

    override fun onHandleIntent(intent: Intent?) {
        locationDatabase = LocationDatabase.getInstance(this)
        getLocation()
    }


    /**
     * Inicializa los atributos locationCallback y fusedLocationClient
     * coloca un intervalo de actualización de 10000 y una prioridad de PRIORITY_HIGH_ACCURACY
     * recibe la ubicación de gps mediante un onLocationResult
     * y envia un broadcast con una instancia de Location y la acción GPS (cr.ac.gpsservice.GPS_EVENT)
     * además guarda la localización en la BD
     */

    @SuppressLint("MissingPermission")
    fun getLocation(){

        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)

        //val locationRequest=LocationRequest.create()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()
        locationRequest.interval = 10000 // If not here
        locationRequest.fastestInterval = 5000  // If it can it'll do it here
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            // determina que se hace cuando hay una ubicacion gps
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationRequest==null) {
                    return
                } // Dibuja en el mapa los puntos
                for (location in locationResult.locations) {
                    val localizar= Location(null,location.latitude,location.longitude)
                    val intent=Intent()
                    intent.action=GPS


                    intent.putExtra("localizacion", localizar)
                    sendBroadcast(intent)
                    locationDatabase.locationDao.insert(Location(null, localizar.latitude, localizar.longitude))
                    LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
        Looper.loop()

    }



}