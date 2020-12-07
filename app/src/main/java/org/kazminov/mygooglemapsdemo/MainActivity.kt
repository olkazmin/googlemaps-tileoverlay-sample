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

        val bitmapGenerator = MarkerBitmapGenerator(drawable)
        bitmapGenerator.text = text
        bitmapGenerator.bottomIcon = drawable
        val bitmap = bitmapGenerator.create()

        val iconDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)

        val markerOptions = MarkerOptions()
        markerOptions.icon(iconDescriptor)
        markerOptions.alpha(1f)
        markerOptions.position(LatLng(59.934280, 30.335099))

        val marker = googleMap.addMarker(markerOptions)

//        BubleIcon
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
