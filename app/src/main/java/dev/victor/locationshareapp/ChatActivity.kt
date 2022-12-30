package dev.victor.locationshareapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatActivity : AppCompatActivity() {
    private lateinit var locationDataView: TextView
    private lateinit var locationInputEditText: EditText
    private lateinit var sendLocationButton: Button

    private var longitude: Double? = null
    private var latitude: Double? = null

    private val uiScope = CoroutineScope(Dispatchers.Main)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .build()

        // Create the location callback
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations){
                    // Update the longitude and latitude variables with the location coordinates
                    longitude = location.longitude
                    latitude = location.latitude
                }
            }
        }

        // Get the fused location client
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@ChatActivity)

        // Register the location callback
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)


        locationDataView = findViewById(R.id.location_data_view)
        locationInputEditText = findViewById(R.id.location_input_edit_text)
        sendLocationButton = findViewById(R.id.send_location_button)

        val host = intent.getStringExtra("HOST")
        val port = intent.getIntExtra("PORT", 5555)

        uiScope.launch {

            if (host != null) {
                LocationClient.instance.connect(host, port)
            }

            // Reads data from server and updates locationDataView
            LocationClient.instance.read(this@ChatActivity) {
                    result ->
                    locationDataView.text = result
            }

            // On trigger of sendLocationButton sends data to server
            sendLocationButton.setOnClickListener {
                val message = locationInputEditText.text.toString()

                uiScope.launch(Dispatchers.IO)  {
                    if (message.isNotEmpty()) {
                        LocationClient.instance.send("$longitude, $latitude")
                    }
                }
                locationInputEditText.setText("")
            }
        }
    }
}