package dev.victor.locationshareapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MarkerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker)

        overlayImages(findViewById(R.id.imageView))
    }

    private fun overlayImages(imageView: ImageView) {
        // Decode the images from resources or file paths
        val circleBitmap = BitmapFactory.decodeResource(resources, R.drawable.circle)
        val avatarBitmap = BitmapFactory.decodeResource(resources, R.drawable.avatar)

        // Create a new bitmap and canvas for the output image
        val resultBitmap = Bitmap.createBitmap(circleBitmap.width, circleBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // Draw the white circle image on the canvas
        canvas.drawBitmap(circleBitmap, 0f, 0f, null)

        // Draw the user avatar image on top of the white circle image
        val x = (circleBitmap.width - avatarBitmap.width) / 2  // center the avatar horizontally
        val y = (circleBitmap.height - avatarBitmap.height) / 4  // center the avatar vertically
        canvas.drawBitmap(avatarBitmap, x.toFloat(), y.toFloat(), null)

        // Display the output image in the image view
        imageView.setImageBitmap(resultBitmap)
    }
}
