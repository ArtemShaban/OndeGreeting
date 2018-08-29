package com.onde.greeting.ondegreeting

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.bumptech.glide.Glide
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
                val loadImageUrls = loadImageUrls(serverUrl)
                loadImageUrls.forEach {
                    val loadImage = loadImage(it)
                    images.add(loadImage)
                }

                handler.post {
                    horizontalPreviewsContainer.adapter = PreviewsAdapter(images, this)
                }

                val result = mergeImages(images)

//                val a = BitmapFactory.decodeResource(resources, R.drawable.a)
//                val b = BitmapFactory.decodeResource(resources, R.drawable.b)
//                val result = mergeImages(Arrays.asList(a,b))

                handler.post { imageView.setImageBitmap(result) }
            }).start()
        }
    }

    private fun toBitmap(drawable: Drawable): Bitmap {
        val bitmapDrawable = drawable as BitmapDrawable
        return bitmapDrawable.bitmap;
    }

    private fun loadImage(url: String): Bitmap {
        return toBitmap(Glide.with(applicationContext)
                .load(url)
                .submit(1000, 1000)
                .get())
    }

    private fun mergeImages(images: List<Bitmap>): Bitmap {
        val sideSize = Math.min(images[0].width, images[0].height)
        val result = Bitmap.createBitmap(sideSize, sideSize, images[0].config)
        for (i in 0 until sideSize) {
            for (j in 0 until sideSize) {
                for (bitmap in images) {
                    val pixel = bitmap.getPixel(i, j)
                    if (pixel != Color.WHITE) {
                        result.setPixel(i, j, pixel)
                        break
                    }
                }
            }
        }
        return result
    }

    private fun loadImageUrls(serverUrl: String): List<String> {
        val apiResponse = URL(serverUrl).readText()
        Log.i("ImageUrlsResponse", apiResponse)

        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(apiResponse)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val images = mutableListOf<String>()
        val jsonArray = jsonObject?.getJSONArray("imageUrls")
        for (i in 0..(jsonArray?.length()?.minus(1)!!)) {
            val url = jsonArray.getString(i)
            images.add(url)
        }
        return images
    }
}
