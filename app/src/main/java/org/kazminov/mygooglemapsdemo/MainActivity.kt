package org.kazminov.mygooglemapsdemo

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.kazminov.mygooglemapsdemo.databinding.ActivityMainBinding
import org.kazminov.mymapboxdemo.RadarDrawableUtil

import yo.radar.Tile
import yo.radar.util.TileUtil

typealias GMapTile = com.google.android.gms.maps.model.Tile

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private val tileOverlayMap = HashMap<String, TileOverlay>()
    private val tileProviderMap = HashMap<String, MyTileProvider>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        MyApplication.instance.requestMapInitialization {
            initMap()
        }
    }

    private fun initMap() {
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

        viewBinding.button.setOnClickListener {
            tileOverlayMap["1"]?.clearTileCache()
        }

        viewBinding.button1.setOnClickListener {
//            showChess = !showChess
            tileOverlayMap["1"]?.apply {
                clearTileCache()
                tileProvider1.showChess = !tileProvider1.showChess
            }
        }

        viewBinding.button3.setOnClickListener {
            tileOverlayMap["1"]?.let { tileOverlay ->
                tileOverlay.isVisible = tileOverlay.isVisible.not()
            }
        }
    }

    private fun onMapReady(googleMap: GoogleMap) {
        viewBinding.progress.visibility = View.GONE

        // tampere - 61.497753, 23.760954
        // spb - 59.934280   30.335099
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
//                LatLng(59.934280, 30.335099),
                LatLng(61.497753, 23.760954),
                START_ZOOM.toFloat()
            )
        )
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true

        loadFromAssets(googleMap)
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

        val tileOverlayOptions1 = TileOverlayOptions()
        tileOverlayOptions1.tileProvider(tileProvider1)
        // Problems:
        // initial visibility value is ignored
        // if initially set to invisible than transparency options will also be ignored
        tileOverlayOptions1.visible(false)
        tileOverlayOptions1.transparency(0.5f)
        tileOverlayMap["1"] = checkNotNull(googleMap.addTileOverlay(tileOverlayOptions1))
        // If we set the overlay to invisible later than provider will start
        // fetching tiles which is NOT always ok
        tileOverlayMap["1"]?.isVisible = false
//        tileOverlayMap["1"]?.transparency = 0.5f



//        val tileOverlayOptions2 = TileOverlayOptions()
//        tileOverlayOptions2.tileProvider(tileProvider2)
//        tileProvider2.showChess = true
//        tileOverlayOptions2.visible(false)
//        tileOverlayOptions2.transparency(0.5f)
//        tileOverlayMap["2"] = checkNotNull(googleMap.addTileOverlay(tileOverlayOptions2))
//        tileOverlayMap["2"]?.isVisible = false


        for (tile in tiles) {
//            val layerId = getLayerId(tile)
//
//            val tileBounds = getTileBounds(tile.x, tile.y, tile.zoom)
//            val bitmap = loadBitmapFromAssets(tile)
//
//            val tileOverlayOptions = TileOverlayOptions()
//            tileOverlayOptions.tileProvider(tileProvider)
//            tileOverlayOptions.visible(true)
//            val overlayOptions = GroundOverlayOptions()
//            overlayOptions.positionFromBounds(tileBounds)
//
//            // TODO: can be replaced with assets path
//            overlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap))
//            overlayOptions.transparency(0.4f)
//
//            val overlay = googleMap.addGroundOverlay(overlayOptions)
        }
    }

    private val tileProvider1 = object : MyTileProvider() {
        override fun getTile(x: Int, y: Int, z: Int): com.google.android.gms.maps.model.Tile? {
            Log.v("TileProvider1", "getTile: $x, $y, zoom=$z")
            var bytes = if (showChess) {
                RadarDrawableUtil.getDrawableAsBitmap(this@MainActivity, false)
            } else {
                loadBytesFromAssets(35, 17, 6)
            }
            val tile = GMapTile(256, 256, bytes)
            return tile
        }
    }

    private val tileProvider2 = object : MyTileProvider() {
        override fun getTile(x: Int, y: Int, z: Int): com.google.android.gms.maps.model.Tile? {
            Log.v("TileProvider2", "getTile: $x, $y, zoom=$z")
            var bytes = if (showChess) {
                RadarDrawableUtil.getDrawableAsBitmap(this@MainActivity, false)
            } else {
                loadBytesFromAssets(35, 18, 6)
            }
            val tile = GMapTile(256, 256, bytes)
            return tile
        }
    }

    private abstract class MyTileProvider : TileProvider {
        var showChess = false
    }

    private fun loadBitmapFromAssets(tile: Tile): Bitmap {
        val inputStream = assets.open("${tile.x}_${tile.y}_${tile.zoom}.png")
        return BitmapFactory.decodeStream(inputStream) ?: throw Error("Bitmap null")
    }

    private fun loadBytesFromAssets(x: Int, y: Int, zoom: Int): ByteArray {
        val inputStream = assets.open("${x}_${y}_${zoom}.png")
        try {
            val bytes = ByteArray(inputStream.available())
            inputStream.read(bytes)
            return bytes
        } finally {
            inputStream.close()
        }
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
