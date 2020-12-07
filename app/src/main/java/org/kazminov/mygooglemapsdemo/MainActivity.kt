package org.kazminov.mygooglemapsdemo

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.kazminov.mygooglemapsdemo.databinding.ActivityMainBinding
import yo.radar.Tile
import yo.radar.util.TileUtil


class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private val tileOverlayMap = HashMap<String, GroundOverlay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val mapFragment = SupportMapFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(viewBinding.map.id, mapFragment)
            .commitAllowingStateLoss()

        mapFragment.getMapAsync {
            it?.let {
                onMapReady(it)
            }
        }
    }

    private fun onMapReady(googleMap: GoogleMap) {
        viewBinding.progress.visibility = View.GONE

        // tampere - 61.497753, 23.760954
        // spb - 59.934280   30.335099
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(59.934280, 30.335099),
                START_ZOOM.toFloat()
            )
        )
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true

//        loadFromAssets(googleMap)
        addTextMarker(googleMap)
    }

    private fun addTextMarker(googleMap: GoogleMap) {

//        val markerBitmap = drawableToBitmap(ContextCompat.getDrawable(this, R.drawable.ic_baseline_location_on_s24)!!) ?: return

//        val iconDescriptor = BitmapDescriptorFactory.fromBitmap(markerBitmap)

        val view = layoutInflater.inflate(R.layout.marker_alyout, viewBinding.root, false)

//        val iconGenerator = IconGenerator(this)
//        iconGenerator.setContentView(view)
//        iconGenerator.setBackground(null)
//        iconGenerator.s
//        val iconDescriptor = BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())s

        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_baseline_location_on_24) ?: return
//        bitmap.eraseColor(Color.WHITE)
//
        val text = "Title"
//        val bounds = Rect()

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

        val margin = 0f
        val iconBitmap = drawableToBitmap(drawable) ?: return

        val markerWidth = iconBitmap.width.coerceAtLeast(textBitmap.width)
        val markerHeight = iconBitmap.height.plus(textBitmap.height).plus(margin).toInt()
        val bitmap = Bitmap.createBitmap(markerWidth, markerHeight, Bitmap.Config.ARGB_8888)
//        bitmap.eraseColor(Color.RED)

        canvas.setBitmap(bitmap)
        canvas.drawBitmap(textBitmap, 0f, 0f, null)
        canvas.drawBitmap(
            iconBitmap,
            bitmap.width.div(2).minus(iconBitmap.width.div(2)).toFloat(),
            textBitmap.height.plus(margin),
            null
        )

//        val iconDescriptor = BitmapDescriptorFactory.fromBitmap(textBitmap)
        val iconDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)

        val markerOptions = MarkerOptions()
        markerOptions.icon(iconDescriptor)
        markerOptions.title("Some title")
        markerOptions.alpha(1f)
        markerOptions.position(LatLng(59.934280, 30.335099))

        val marker = googleMap.addMarker(markerOptions)

//        BubleIcon
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
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


    private fun loadFromAssets(googleMap: GoogleMap) {
        val tiles = arrayListOf<Tile>(
            Tile(35, 17, 6),
            Tile(36, 17, 6),
            Tile(35, 18, 6),
            Tile(36, 18, 6)
        )

        for (tile in tiles) {
            val layerId = getLayerId(tile)

            val tileBounds = getTileBounds(tile.x, tile.y, tile.zoom)
            val bitmap = loadBitmapFromAssets(tile)

            val overlayOptions = GroundOverlayOptions()
            overlayOptions.positionFromBounds(tileBounds)

            // TODO: can be replaced with assets path
            overlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap))
            overlayOptions.transparency(0.4f)

            val overlay = googleMap.addGroundOverlay(overlayOptions)
            tileOverlayMap[getLayerId(tile)] = overlay
        }
    }

    private fun loadBitmapFromAssets(tile: Tile): Bitmap {
        val inputStream = assets.open("${tile.x}_${tile.y}_${tile.zoom}.png")
        return BitmapFactory.decodeStream(inputStream) ?: throw Error("Bitmap null")
    }

    private fun getLayerId(tile: Tile): String {
        return "l ${tile.x}, ${tile.y}"
    }

    private fun getTileBounds(x: Int, y: Int, zoom: Int): LatLngBounds {
        val top = TileUtil.tile2lat(y, zoom)
        val left = TileUtil.tile2lon(x, zoom)

        val bottom = TileUtil.tile2lat(y + 1, zoom)
        val right = TileUtil.tile2lon(x + 1, zoom)

        val topLeft = LatLng(top, left)
        val topRight = LatLng(top, right)
        val bottomRight = LatLng(bottom, right)
        val bottomLeft = LatLng(bottom, left)

        return LatLngBounds(bottomLeft, topRight)
    }

    companion object {
        const val START_ZOOM = 8
    }
}
