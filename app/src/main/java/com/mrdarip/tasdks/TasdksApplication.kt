package com.mrdarip.tasdks

import android.app.Application
import com.mrdarip.tasdks.data.AppContainer
import com.mrdarip.tasdks.data.AppDataContainer


class TasdksApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}