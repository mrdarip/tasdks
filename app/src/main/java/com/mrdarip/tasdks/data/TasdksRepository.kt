package com.mrdarip.tasdks.data

import com.mrdarip.tasdks.data.entity.DAOs
import com.mrdarip.tasdks.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class TasdksRepository(
    private val taskDAO: DAOs.TaskDAO,
    private val placeDAO: DAOs.PlaceDAO,
    private val objectDAO: DAOs.ObjectDAO,
    private val activatorDAO: DAOs.ActivatorDAO,
    private val executionDAO: DAOs.ExecutionDAO,
    private val resourceDAO: DAOs.ResourceDAO,
    private val taskWithTasksDAO: DAOs.TaskWithTasksDAO
) {

    //Todo add other DAOs, video 5/7
    val tasks = taskDAO.getAll()
    val tasksOrderByLastDone = taskDAO.getAllOrderByLastDone()
    val tasksOrderByUsuallyAtThisTime = taskDAO.getAllOrderByUsuallyAtThisTime()
    val places = placeDAO.getAllPlaces()
    val objects = objectDAO.getAllObjects()
    val activators = activatorDAO.getAllActivators()
    val executions = executionDAO.getAllExecutions()
    val resources = resourceDAO.getAllResources()

    //val getTaskWithTasks = TaskWithTaskDAO.getTasksWithTasks()
    fun getTaskById(taskId: Long?): Flow<Task> {
        if(taskId==null) return emptyFlow()
        return taskDAO.getById(taskId).mapNotNull {it}
    }
    suspend fun upsertTask(task: Task) {
        taskDAO.upsert(task)
    }


    suspend fun deleteTask(task: Task) {
        withContext(Dispatchers.IO) {
            taskDAO.delete(task)
        }
    }

    fun getPlaceName(placeId: Long?): Flow<String> {
        if(placeId==null) return emptyFlow()
        return placeDAO.getPlaceById(placeId).mapNotNull { it?.name ?: "PLACE NOT FOUND" }
    }

}