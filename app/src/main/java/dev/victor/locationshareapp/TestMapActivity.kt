package dev.victor.locationshareapp
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.logo.logo
import org.json.JSONObject


class TestMapActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private lateinit var getZoom: FloatingActionButton
    private var annotation: AnnotationPlugin? = null
    private lateinit var annotationConfig: AnnotationConfig
    private val layerID = "map_annotation"
    private var pointAnnotationManager : PointAnnotationManager? = null
    private var markerList :ArrayList<PointAnnotationOptions> = ArrayList()

    private var latitudeList : ArrayList<Double> = ArrayList()
    private var longitudeList : ArrayList<Double> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_map)

        mapView = findViewById(R.id.mapView)
        getZoom = findViewById(R.id.get_zoom)

        mapView?.attribution?.enabled = false
        mapView?.logo?.enabled = false

        createLatLongForMarker()

        mapView?.getMapboxMap()?.loadStyleUri(
            "mapbox://styles/victordvickie/clbvxxghl000a14oo9px7vuq7"
        ) {
            zoomCamera()
            //  addOnMapClickListener(this@HomeFragmentNew)

            annotation = mapView?.annotations
            annotationConfig = AnnotationConfig(
                layerId = layerID
            )
            pointAnnotationManager = annotation?.createPointAnnotationManager(annotationConfig)!!

            createMarkerList()


            try {
                mapView!!.gestures.pitchEnabled = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        getZoom.setOnClickListener {
            val cameraState = mapView!!.getMapboxMap().cameraState
            Toast.makeText(this@TestMapActivity, "${cameraState.zoom},${cameraState.center}", Toast.LENGTH_SHORT).show()
            Log.d("Map", "${cameraState.zoom},${cameraState.center}")
        }
    }

    private fun createLatLongForMarker(){
        latitudeList.add(13.03333)
        longitudeList.add(80.165355)

        latitudeList.add(12.934336)
        longitudeList.add(80.142465)

    }

    private fun zoomCamera(){
        mapView!!.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(80.16355,13.03333))
                .zoom(11.0)
                .build()
        )
    }


    private fun createMarkerList(){

        clearAnnotation()

        // It will work when we create marker


        markerList =  ArrayList()
        val bitmap = convertDrawableToBitmap(AppCompatResources.getDrawable(this, R.drawable.marker))
        for (i in 0 until  2){

            val mObe = JSONObject()
            mObe.put("some-key",i)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitudeList[i], latitudeList[i]))
                .withData(Gson().fromJson(mObe.toString(), JsonElement::class.java))
                .withIconImage(bitmap!!)
            markerList.add(pointAnnotationOptions)
        }

        pointAnnotationManager?.create(markerList)

    }
    private fun clearAnnotation(){
        markerList = ArrayList()
        pointAnnotationManager?.deleteAll()
    }
    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

}