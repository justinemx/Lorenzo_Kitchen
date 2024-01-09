package com.mooncode.lorenzoskitchen

import android.R
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import io.realm.Realm
import java.io.ByteArrayOutputStream
import kotlin.math.pow
import kotlin.math.sqrt

val realm: Realm = Realm.getDefaultInstance()
lateinit var sharedPrefs: SharedPreferences

fun typeDP(d: Float, activity: Activity) : Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, d, activity.resources.displayMetrics)
}

fun typeDP(d: Int, activity: Activity) : Int {
    return typeDP(d.toFloat(), activity).toInt()
}

class ImageUtils {
    companion object {
        fun toBase64(bitmap: Bitmap): String {
            // convert resized bitmap to a Base64 string
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        fun fromBase64(base64: String): Bitmap {
            // resize image into 2mp image
            val decodedString: ByteArray = Base64.decode(base64, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }

        fun saveToSharedPref(sharedPref: SharedPreferences, UUID: String, image: Bitmap) {
            val imageBase64 = toBase64(image)
            with (sharedPref.edit()) {
                putString(UUID, imageBase64)
                apply()
            }
        }

        fun saveToSharedPref(sharedPref: SharedPreferences, UUID: String, imageBase64: String) {
            with (sharedPref.edit()) {
                putString(UUID, imageBase64)
                apply()
            }
        }

        fun saveToSharedPref(sharedPref: SharedPreferences, UUID: String, imageList: List<Bitmap>) {
            val imageBase64List = mutableListOf<String>()
            for (image in imageList) {
                imageBase64List.add(toBase64(image))
            }
            with (sharedPref.edit()) {
                putStringSet(UUID, imageBase64List.toSet())
                apply()
            }
        }

        fun getFromSharedPref(sharedPref: SharedPreferences, UUID: String): Bitmap {
            val imageBase64 = sharedPref.getString(UUID, "")
            return fromBase64(imageBase64!!)
        }

        fun getListFromSharedPref(sharedPref: SharedPreferences, UUID: String): List<Bitmap> {
            val imageBase64List = sharedPref.getStringSet(UUID, setOf())
            val imageList = mutableListOf<Bitmap>()
            for (imageBase64 in imageBase64List!!) {
                imageList.add(fromBase64(imageBase64))
            }
            return imageList
        }

        fun resize(bitmap: Bitmap, newWidth: Int, newHeight: Int):Bitmap{
            val width = bitmap.width
            val height = bitmap.height
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height
            val matrix = android.graphics.Matrix()
            matrix.postScale(scaleWidth, scaleHeight)

            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
        }

        fun getFromDrawable(context: Context, drawableId: Int): Bitmap {
            val drawable: Drawable = ContextCompat.getDrawable(context, drawableId)
                ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }

        fun cropToRatio(bitmap: Bitmap, x: Double, y: Double): Bitmap {
            val targetAspectRatio = x / y

            val originalWidth = bitmap.width
            val originalHeight = bitmap.height
            val originalAspectRatio = originalWidth.toFloat() / originalHeight.toFloat()

            val left: Int
            val top: Int
            val right: Int
            val bottom: Int

            if (originalAspectRatio > targetAspectRatio) {
                val newWidth = (originalHeight * targetAspectRatio).toInt()
                val widthDifference = originalWidth - newWidth
                left = widthDifference / 2
                top = 0
                right = originalWidth - widthDifference / 2
                bottom = originalHeight
            } else {
                val newHeight = (originalWidth / targetAspectRatio).toInt()
                val heightDifference = originalHeight - newHeight
                left = 0
                top = heightDifference / 2
                right = originalWidth
                bottom = originalHeight - heightDifference / 2
            }

            val croppedBitmap = Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)

            // val resizedBitmap = Bitmap.createScaledBitmap(croppedBitmap, newWidth, newHeight, true)

            return croppedBitmap
        }
    }
}
