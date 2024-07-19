package com.mrdarip.tasdks.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Converters
import com.mrdarip.tasdks.data.entity.DAOs
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.Object
import com.mrdarip.tasdks.data.entity.Resource
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.data.entity.TaskObjectCR
import com.mrdarip.tasdks.data.entity.TaskTaskCR

@Database(
    entities = [
        Task::class,
        Object::class,
        Activator::class,
        Execution::class,
        Resource::class,
        TaskTaskCR::class,
        TaskObjectCR::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TasdksDatabase : RoomDatabase() {
    abstract fun taskDao(): DAOs.TaskDAO
    abstract fun objectDao(): DAOs.ObjectDAO
    abstract fun activatorDao(): DAOs.ActivatorDAO
    abstract fun executionDao(): DAOs.ExecutionDAO
    abstract fun resourceDao(): DAOs.ResourceDAO
    abstract fun taskWithTasksDao(): DAOs.TaskWithTasksDAO
    abstract fun taskWithObjectsDao(): DAOs.TaskWithObjectsDAO
    companion object {
        @Volatile
        private var INSTANCE: TasdksDatabase? = null

        fun getDatabase(context: Context): TasdksDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    TasdksDatabase::class.java,
                    "tasdks_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}