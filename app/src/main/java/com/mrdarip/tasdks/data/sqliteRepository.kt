package com.mrdarip.tasdks.data

import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.data.entity.TaskDAO
import kotlinx.coroutines.flow.Flow

class sqliteRepository(private val itemDao: TaskDAO): Repository{
    override fun getAllItemsStream(): Flow<List<Task>> = itemDao.getAllItems()

    override fun getItemStream(id: Int): Flow<Task?> = itemDao.getItem(id)

    override suspend fun insertItem(item: Task) = itemDao.insert(item)

    override suspend fun deleteItem(item: Task) = itemDao.delete(item)

    override suspend fun updateItem(item: Task) = itemDao.update(item)
}
