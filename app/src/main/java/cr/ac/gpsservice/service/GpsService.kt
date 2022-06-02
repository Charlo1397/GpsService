package cr.ac.gpsservice.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Intent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback


class GpsService : IntentService("GpsService") {

    lateinit var locationCallback: LocationCallback
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val GPS = "cr.ac.gpsservice.GPS_EVENT"
    }

    override fun onHandleIntent(intent: Intent?) {
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

    fun getLocation() {

    }

}