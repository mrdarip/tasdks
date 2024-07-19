package com.mrdarip.tasdks.data

import android.content.Context
import com.mrdarip.tasdks.data.database.TasdksDatabase

object Graph {

    lateinit var db:TasdksDatabase
        private set
    val repository by lazy{
        TasdksRepository(
            db.taskDao(),
            db.activatorDao(),
            db.executionDao(),
            db.resourceDao(),
            db.taskWithTasksDao()
        )
    }

    fun provide(context: Context){
        db = TasdksDatabase.getDatabase(context)
    }
}