package com.onde.greeting.ondegreeting

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var serverUrl = "http://159.69.7.101:8080/images"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        horizontalPreviewsContainer.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Merging...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            val handler = Handler()
            Thread(Runnable
            {
                val images = mutableListOf<Bitmap>()
                val loadedEncodedImages = loadEncodedImages(serverUrl)
                loadedEncodedImages.forEach {
                    val decodedImage = decodeImage(it)
                    images.add(decodedImage)
                }

                handler.post {
                    horizontalPreviewsContainer.adapter = PreviewsAdapter(images, this)
                }

                val result = mergeImages(images)

                handler.post { imageView.setImageBitmap(result) }
            }).start()
        }
    }

    private fun decodeImage(base64: String): Bitmap {
        val imageByteArray = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }

    private fun mergeImages(images: List<Bitmap>): Bitmap {
        val imageWidth = images[0].width
        val imageHeight = images[0].height
        val result = Bitmap.createBitmap(imageWidth, imageHeight, images[0].config)
        for (y in 0 until imageHeight) {
            for (x in 0 until imageWidth) {
                for (bitmap in images) {
                    val pixel = bitmap.getPixel(x, y)
                    if (pixel != Color.TRANSPARENT) {
                        result.setPixel(x, y, pixel)
                        break
                    }
                }
            }
        }
        return result
    }

    private fun loadEncodedImages(serverUrl: String): List<String> {
        val apiResponse = URL(serverUrl).readText()
        Log.i("ImageUrlsResponse", apiResponse)

        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(apiResponse)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val images = mutableListOf<String>()
        val jsonArray = jsonObject?.getJSONArray("pngImagesInBase64")
        for (i in 0..(jsonArray?.length()?.minus(1)!!)) {
            val url = jsonArray.getString(i)
            images.add(url)
        }
        return images
    }
}
