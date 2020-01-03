package org.kazminov.mymapboxdemo

import android.graphics.PixelFormat
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable


class ChessBoardDrawable(private val thiknessPx: Int): Drawable() {

    override fun draw(canvas: Canvas) {
        bgPaint.color = myLightColor
        fgPaint.color = myDarkColor

        canvas.drawPaint(bgPaint)

        val b = bounds
        val twiceThickness = thiknessPx shl 1
        var flipper = true
        var x = b.left
        while (x < b.right) {
            val offset = if (flipper) 0 else thiknessPx
            var y = b.top
            while (y < b.bottom) {
                canvas.drawRect(x.toFloat(), (y + offset).toFloat(), (x + thiknessPx).toFloat(), (y + offset + thiknessPx).toFloat(), fgPaint)
                y += twiceThickness
            }
            flipper = !flipper
            x += thiknessPx
        }
    }
    override fun setColorFilter(colorFilter: ColorFilter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAlpha(i: Int) {
        // just ignore
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    fun setLightColor(lightColor: Int) {
        myLightColor = lightColor
        bgPaint.setColor(myLightColor)
    }

    fun setDarkColor(darkColor: Int) {
        myDarkColor = darkColor
        fgPaint.setColor(myDarkColor)
    }

    val LIGHT_COLOR = 0x70000000
    val DARK_COLOR = 0x30000000

    private var myLightColor = LIGHT_COLOR
    private var myDarkColor = DARK_COLOR

    private val bgPaint = Paint()
    private val fgPaint = Paint()
}