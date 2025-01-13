package com.mrdarip.tasdks.data

import android.content.Context
import com.mrdarip.tasdks.data.database.TasdksDatabase

object Graph {

    private lateinit var db: TasdksDatabase
    val repository by lazy{
        TasdksRepository(
            db.taskDao(),
            db.activatorDao(),
            db.executionDao(),
            db.taskWithTasksDao()
        )
    }

    fun provide(context: Context){
        db = TasdksDatabase.getDatabase(context)
    }
}