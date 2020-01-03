package org.kazminov.mymapboxdemo

import android.graphics.Bitmap
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import android.graphics.drawable.Drawable
import android.app.Activity
import android.graphics.Canvas
import org.kazminov.mygooglemapsdemo.R
import yo.radar.Constants
import java.io.ByteArrayOutputStream


object RadarDrawableUtil {

    fun getDrawableAsBitmap(activity: Activity, errorImage: Boolean): ByteArray {
        if (activity != null) {
            val drawable = ChessBoardDrawable(32)

            val bitmapBuffer = Bitmap.createBitmap(
                Constants.TILE_SIZE,
                Constants.TILE_SIZE,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmapBuffer)
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
            drawable.draw(canvas)

            if (errorImage) {
                val renewDrawable = VectorDrawableCompat.create(
                    activity!!.getResources(),
                    R.drawable.ic_autorenew_v,
                    activity!!.getTheme()
                )
                DrawableCompat.setTint(renewDrawable!!, 0x70FFFFFF)
                if (renewDrawable != null) {
                    renewDrawable.setBounds(
                        Constants.TILE_BUTTON_OFFSET,
                        Constants.TILE_BUTTON_OFFSET,
                        canvas.getWidth() - Constants.TILE_BUTTON_OFFSET,
                        canvas.getHeight() - Constants.TILE_BUTTON_OFFSET
                    )
                    renewDrawable.draw(canvas)
                }
            }

            val stream = ByteArrayOutputStream()
            bitmapBuffer.compress(Bitmap.CompressFormat.PNG, 70, stream)

            return stream.toByteArray()
        }
        return ByteArray(0)
    }
}