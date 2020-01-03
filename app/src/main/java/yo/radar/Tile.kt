package yo.radar

data class Tile(val x: Int,
                val y: Int,
                val zoom: Int,
                val state: TileState = TileState.DEFAULT,
                val bytes: ByteArray? = null) {
    // TODO: equals

    override fun toString(): String {
        return "x=$x, y=$y, z=$zoom, s=$state"
    }
}