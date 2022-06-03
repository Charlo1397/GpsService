package cr.ac.gpsservice
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.PolyUtil
import com.google.maps.android.data.geojson.GeoJsonPolygon
import cr.ac.gpsservice.databinding.ActivityMapsBinding
import cr.ac.gpsservice.db.LocationDatabase
import cr.ac.gpsservice.entity.Location
import cr.ac.gpsservice.service.GpsService
import org.json.JSONObject
import java.util.jar.Manifest



private lateinit var mMap: GoogleMap
private lateinit var locationDatabase: LocationDatabase
private lateinit var layer : GeoJsonLayer
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var binding: ActivityMapsBinding
    private val SOLICITAR_GPS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationDatabase=LocationDatabase.getInstance(this)
        validaPermisos()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        definePoligono(googleMap)
        recuperarPuntos(mMap)
        iniciaServicio()

    }

    fun recuperarPuntos(googleMap:GoogleMap){
        mMap = googleMap

        for(location in locationDatabase.locationDao.query()){
            val costaRica = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(costaRica).title("Marker"))
        }

    }


    fun iniciaServicio(){
        val filter= IntentFilter()
        filter.addAction(GpsService.GPS)
        val rcv = ProgressReceiver()
        registerReceiver(rcv,filter)
        startService(Intent(this,GpsService::class.java))
    }

    fun validaPermisos(){
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                SOLICITAR_GPS
            )
        }
    }
    /**
     * Fuera del punto Mexico
     */

    fun definePoligono(googleMap: GoogleMap){
        val geoJsonData= JSONObject("{\n" +
                "  \"type\": \"FeatureCollection\",\n" +
                "  \"features\": [\n" +
                "    {\n" +
                "      \"type\": \"Feature\",\n" +
                "      \"properties\": {},\n" +
                "      \"geometry\": {\n" +
                "        \"type\": \"Polygon\",\n" +
                "        \"coordinates\": [\n" +
                "          [\n" +
                "            [\n" +
                "              -95.625,\n" +
                "              15.580710739162123\n" +
                "            ],\n" +
                "            [\n" +
                "              -94.7900390625,\n" +
                "              19.72534224805787\n" +
                "            ],\n" +
                "            [\n" +
                "              -102.1728515625,\n" +
                "              21.779905342529645\n" +
                "            ],\n" +
                "            [\n" +
                "              -102.919921875,\n" +
                "              18.104087015773956\n" +
                "            ],\n" +
                "            [\n" +
                "              -95.625,\n" +
                "              15.580710739162123\n" +
                "            ]\n" +
                "          ]\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}")
        layer = GeoJsonLayer(googleMap, geoJsonData)
        layer.addLayerToMap()
    }

    /**
     * dentro del punto Costa Rica
     */
    /*
    fun definePoligono(googleMap: GoogleMap){
        val geoJsonData= JSONObject("{\n" +
                "  \"type\": \"FeatureCollection\",\n" +
                "  \"features\": [\n" +
                "    {\n" +
                "      \"type\": \"Feature\",\n" +
                "      \"properties\": {},\n" +
                "      \"geometry\": {\n" +
                "        \"type\": \"Polygon\",\n" +
                "        \"coordinates\": [\n" +
                "          [\n" +
                "            [\n" +
                "              -85.69335937499999,\n" +
                "              11.30770770776545\n" +
                "            ],\n" +
                "            [\n" +
                "              -86.0009765625,\n" +
                "              10.120301632173907\n" +
                "            ],\n" +
                "            [\n" +
                "              -85.14404296875,\n" +
                "              9.514079262770904\n" +
                "            ],\n" +
                "            [\n" +
                "              -84.83642578125,\n" +
                "              9.774024565864734\n" +
                "            ],\n" +
                "            [\n" +
                "              -82.8369140625,\n" +
                "              7.776308503776205\n" +
                "            ],\n" +
                "            [\n" +
                "              -82.55126953124999,\n" +
                "              9.687398430760624\n" +
                "            ],\n" +
                "            [\n" +
                "              -83.7158203125,\n" +
                "              11.005904459659451\n" +
                "            ],\n" +
                "            [\n" +
                "              -85.69335937499999,\n" +
                "              11.30770770776545\n" +
                "            ]\n" +
                "          ]\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}")
        layer = GeoJsonLayer(googleMap, geoJsonData)
        layer.addLayerToMap()
    }*/


    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            SOLICITAR_GPS -> {
                if ( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.exit(1)
                }

            }
        }
    }

    class ProgressReceiver:BroadcastReceiver() {
        fun getPolygon(layer: GeoJsonLayer): GeoJsonPolygon? {
            for (feature in layer.features) {
                return feature.geometry as GeoJsonPolygon
            }
            return null
        }

        override fun onReceive(p0: Context, p1: Intent) {
            if (p1.action == GpsService.GPS) {
                val localizacion: Location = p1.getSerializableExtra("localizacion") as Location
                val punto = LatLng(localizacion.latitude, localizacion.longitude)
                mMap.addMarker(MarkerOptions().position(punto).title("Marker"))


                if (PolyUtil.containsLocation(
                        localizacion.latitude,
                        localizacion.longitude,
                        getPolygon(layer)!!.outerBoundaryCoordinates, false)) {
                    Toast.makeText(p0,"En el punto",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(p0,"No esta en el punto",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}