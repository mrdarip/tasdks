package com.mrdarip.tasdks.data

import androidx.lifecycle.LiveData
import com.mrdarip.tasdks.data.entity.DAOs
import com.mrdarip.tasdks.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasdksRepository(
    private val TaskDAO: DAOs.TaskDAO,
    private val PlaceDAO: DAOs.PlaceDAO,
    private val ObjectDAO: DAOs.ObjectDAO,
    private val ActivatorDAO: DAOs.ActivatorDAO,
    private val ExecutionDAO: DAOs.ExecutionDAO,
    private val ResourceDAO: DAOs.ResourceDAO,
    private val TaskWithTaskDAO: DAOs.TaskWithTasksDAO
) {

    //Todo add other DAOs, video 5/7
    val tasks = TaskDAO.getAll()
    val tasksOrderByLastDone = TaskDAO.getAllOrderByLastDone()
    val tasksOrderByUsuallyAtThisTime = TaskDAO.getAllOrderByUsuallyAtThisTime()
    val places = PlaceDAO.getAllPlaces()
    val objects = ObjectDAO.getAllObjects()
    val activators = ActivatorDAO.getAllActivators()
    val executions = ExecutionDAO.getAllExecutions()
    val resources = ResourceDAO.getAllResources()

    //val getTaskWithTasks = TaskWithTaskDAO.getTasksWithTasks()
    fun getTaskById(taskId: Long) = TaskDAO.getById(taskId)
    suspend fun insertTask(task: Task) {
        TaskDAO.insert(task)
    }

    suspend fun updateTask(task: Task) {
        TaskDAO.update(task)
    }

    suspend fun deleteTask(task: Task) {
        withContext(Dispatchers.IO) {
            TaskDAO.delete(task)
        }
    }

}