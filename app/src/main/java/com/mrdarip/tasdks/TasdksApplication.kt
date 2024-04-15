package com.mrdarip.tasdks

import android.app.Application
import com.mrdarip.tasdks.data.Graph

class TasdksApplication: Application(){
    override fun onCreate(){
        super.onCreate()
        Graph.provide(this)
    }
}