package com.mrdarip.tasdks.data

import android.content.Context
import com.mrdarip.tasdks.data.database.TasdksDatabase

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val itemsRepository: Repository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val itemsRepository: Repository by lazy {
        sqliteRepository(TasdksDatabase.getDatabase(context).taskDao())
    }
}