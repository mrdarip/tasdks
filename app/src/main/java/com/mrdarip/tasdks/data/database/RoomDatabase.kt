package com.mrdarip.tasdks.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mrdarip.tasdks.data.DAOs
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Converters
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.Object
import com.mrdarip.tasdks.data.entity.Place
import com.mrdarip.tasdks.data.entity.Resource
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.data.entity.TaskObjectCR
import com.mrdarip.tasdks.data.entity.TaskTaskCR

@Database(entities = arrayOf(Task::class, Place::class, Object::class, Activator::class,Execution::class,Resource::class,TaskTaskCR::class, TaskObjectCR::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
public abstract class TasdksDatabase : RoomDatabase() {


        abstract fun taskDao(): DAOs.TaskDAO
        abstract fun placeDao(): DAOs.PlaceDAO
        abstract fun objectDao(): DAOs.ObjectDAO
        abstract fun activatorDao(): DAOs.ActivatorDAO
        abstract fun executionDao(): DAOs.ExecutionDAO
        abstract fun resourceDao(): DAOs.resourceDAO
        abstract fun taskWithTasksDao(): DAOs.TaskWithTasksDAO
        abstract fun taskWithObjectsDao(): DAOs.TaskWithObjectsDAO

    companion object {
        @Volatile
        private var Instance: TasdksDatabase? = null

        fun getDatabase(context: Context): TasdksDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TasdksDatabase::class.java, "item_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}