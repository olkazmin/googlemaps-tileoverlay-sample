package org.kazminov.mygooglemapsdemo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

/**
 * Draws tree variants for bitmaps:
 * - icon
 * - main icon + text
 * - main icon + bottom icon + text
 */
class MarkerBitmapGenerator(var icon: Drawable) {

    var bottomIcon: Drawable? = null
    var text: String? = null

    val canvas = Canvas()

    fun create(): Bitmap {
        // convert main icon to bitmap
        val mainBitmap = drawableToBitmap(icon)
        // convert text to bitmap
        val textBitmap = text?.let { textToBitmap(it) }
        // convert bottom icon to bitmap
        val bottomBitmap = bottomIcon?.let { drawableToBitmap(it) }

        // create target bitmap
        var width = mainBitmap.width
        var height = mainBitmap.height
        if (textBitmap != null && bottomBitmap != null) {
            width = width.coerceAtLeast(textBitmap.width + bottomBitmap.width)
            height += bottomBitmap.height.coerceAtLeast(textBitmap.height)
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        // draw main icon
        canvas.drawBitmap(mainBitmap,
            bitmap.width.div(2).minus(mainBitmap.width.div(2)).toFloat(),
            0f,
            null)
        if (textBitmap != null && bottomBitmap != null) {
            val bottomSectionHeight = bitmap.height - mainBitmap.height
            // draw bottom icon
            val iconTop = bottomSectionHeight.div(2).minus(bottomBitmap.height.div(2))
            val iconLeft = bitmap.width.div(2).minus(textBitmap.width.plus(bottomBitmap.width).div(2))
            canvas.drawBitmap(bottomBitmap,
                iconLeft.toFloat(),
                mainBitmap.height.plus(iconTop).toFloat(),
                null)
            // draw text icon
            val textTop = bottomSectionHeight.div(2).minus(textBitmap.height.div(2))
            canvas.drawBitmap(textBitmap,
                iconLeft.plus(bottomBitmap.width).toFloat(),
                mainBitmap.height.plus(textTop).toFloat(),
                null)
        }

        // recycle bitmaps
        mainBitmap.recycle()
        textBitmap?.let {
            it.recycle()
        }
        bottomBitmap?.let {
            it.recycle()
        }
        // TODO: keep main icon allocated?
        return bitmap
    }

    companion object {
        fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }
            var width = drawable.intrinsicWidth
            width = if (width > 0) width else 1
            var height = drawable.intrinsicHeight
            height = if (height > 0) height else 1

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        bitmap.eraseColor(Color.GREEN)

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        fun textToBitmap(text: String): Bitmap {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.textAlign = Paint.Align.LEFT
            textPaint.textSize = 40f
            textPaint.style = Paint.Style.STROKE
            textPaint.color = Color.WHITE;
            textPaint.strokeWidth = 4f

//        textPaint.getTextBounds(text, 0, text.length, bounds)
            val baseline: Float = -textPaint.ascent() // ascent() is negative
            val width = (textPaint.measureText(text) + 0.5f)// round
            val height = (baseline + textPaint.descent() + 0.5f)

            val textBitmap = Bitmap.createBitmap(
                width.toInt(),
                height.toInt(),
                Bitmap.Config.ARGB_8888
            )
//        textBitmap.eraseColor(Color.BLUE)
            val canvas = Canvas(textBitmap)
            canvas.drawText(text, 0f, baseline, textPaint)

            textPaint.style = Paint.Style.FILL
            textPaint.color = 0xff494949.toInt();
            textPaint.strokeWidth = 0f

//        textPaint.getTextBounds(text, 0, text.length, bounds)
            canvas.drawText(text, 0f, baseline, textPaint)

            return textBitmap
        }
    }
}