package dev.victor.locationshareapp

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionRequestActivity : AppCompatActivity()
{
    private val locationPerm = 1
    private val bgLocationPerm = 2
    private lateinit var ok: Button
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // All necessary location permissions are granted
            startHostActivity()
        }
        else
        {
            // One or more necessary location permissions are not granted
            setContentView(R.layout.activity_permission_request)
            ok = findViewById(R.id.location_permission)
            ok.setOnClickListener {checkPermission()}
        }


    }

    private fun checkPermission()
    {
        if (ContextCompat.checkSelfPermission(this@PermissionRequestActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // Fine Location permission is granted
            // Check if current android version >= 11, if >= 11 check for Background Location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                if (ContextCompat.checkSelfPermission(this@PermissionRequestActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    startHostActivity()
                    // Background Location Permission is granted so do your work here
                } else
                {
                    // Ask for Background Location Permission
                    askPermissionForBackgroundUsage()
                }
            }
        }
        else
        {
            // Fine Location Permission is not granted so ask for permission
            askForLocationPermission()
        }
    }

    private fun askForLocationPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this@PermissionRequestActivity, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            AlertDialog.Builder(this)
                .setTitle("Permission Needed!")
                .setMessage("Location Permission Needed!")
                .setPositiveButton("OK")
                { dialog, which ->
                    ActivityCompat.requestPermissions(this@PermissionRequestActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPerm)
                }
                .setNegativeButton(
                    "CANCEL"
                ) { dialog, which ->
                    // Permission is denied by the user
                }.create().show()
        }
        else
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPerm)
        }
    }

    private fun askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@PermissionRequestActivity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permission Needed!")
                .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
                .setPositiveButton(
                    "OK"
                ) { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this@PermissionRequestActivity,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        bgLocationPerm
                    )
                }
                .setNegativeButton(
                    "CANCEL"
                ) { dialog, which ->
                    // User declined for Background Location Permission.
                }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                bgLocationPerm
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPerm) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted location permission
                // Now check if android version >= 11, if >= 11 check for Background Location Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(
                            this@PermissionRequestActivity,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startHostActivity()

                        // Background Location Permission is granted so do your work here
                    } else {
                        // Ask for Background Location Permission
                        askPermissionForBackgroundUsage()
                    }
                }
            } else {
                // User denied location permission
            }
        } else if (requestCode == bgLocationPerm) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startHostActivity()
                // User granted for Background Location Permission.
            } else {
                // User declined for Background Location Permission.
            }
        }
    }
    private fun startHostActivity() {
        startActivity(Intent(this, HostActivity::class.java))
        finish()
    }
}