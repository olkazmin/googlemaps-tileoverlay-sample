package org.kazminov.mygooglemapsdemo

import android.app.Application
import android.util.Log

import com.google.android.gms.maps.MapsInitializer

class MyApplication: Application() {

    private var mapInitialized = false
    private var mapInitCallback: (() -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        instance = this

        MapsInitializer.initialize(getApplicationContext(),
            MapsInitializer.Renderer.LATEST
        ) { renderer ->
            when (renderer) {
                MapsInitializer.Renderer.LATEST -> Log.d(
                    "MapsInitializer",
                    "The latest version of the renderer is used."
                )
                MapsInitializer.Renderer.LEGACY -> Log.d(
                    "MapsInitializer",
                    "The legacy version of the renderer is used."
                )
            }
            mapInitialized = true
        };
    }

    fun requestMapInitialization(callback: (() -> Unit)) {
        if (!mapInitialized) {
            mapInitCallback = callback
            return
        }

        callback?.invoke()
    }

    companion object {
        lateinit var instance: MyApplication
    }
}