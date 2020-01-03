package yo.radar.util

import kotlin.math.*


object TileUtil {

    fun tile2lon(x: Int, z: Int): Double {
        return x.toDouble() / 2.0.pow(z.toDouble())*360.0 - 180.0
    }

    fun tile2lat(y: Int, z: Int): Double {
        val n = Math.PI - 2.0*Math.PI*y.toDouble() / 2.0.pow(z.toDouble())
        return Math.toDegrees(atan(sinh(n)))
    }

    fun lon2tile(lon: Double, zoom: Int): Int {
        var xtile = floor(
            (lon + 180.0) / 360.0*(1 shl zoom).toDouble()
        ).toInt()

        if (xtile < 0)
            xtile = 0
        if (xtile >= 1 shl zoom)
            xtile = (1 shl zoom) - 1
        return xtile
    }

    fun lat2tile(lat: Double, zoom: Int): Int {
        var ytile =
            floor(
                (1.0 - ln(tan(Math.toRadians(lat)) + 1.0 / cos(Math.toRadians(lat))) / Math.PI) / 2.0*(1 shl zoom)
            ).toInt()

        if (ytile < 0)
            ytile = 0
        if (ytile >= 1 shl zoom)
            ytile = (1 shl zoom) - 1
        return ytile
    }
}