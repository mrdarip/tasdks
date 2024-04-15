package com.mrdarip.tasdks.data

import androidx.lifecycle.LiveData
import com.mrdarip.tasdks.data.entity.DAOs
import com.mrdarip.tasdks.data.entity.*

class TasdksRepository(
    private val TaskDAO: DAOs.TaskDAO,
    private val PlaceDAO: DAOs.PlaceDAO,
    private val ObjectDAO: DAOs.ObjectDAO,
    private val ActivatorDAO: DAOs.ActivatorDAO,
    private val ExecutionDAO: DAOs.ExecutionDAO,
    private val ResourceDAO: DAOs.ResourceDAO,
) {

}