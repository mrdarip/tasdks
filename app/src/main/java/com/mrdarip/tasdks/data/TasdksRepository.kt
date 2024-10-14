package com.mrdarip.tasdks.data

import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.DAOs
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.Resource
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class TasdksRepository(
    private val taskDAO: DAOs.TaskDAO,
    private val activatorDAO: DAOs.ActivatorDAO,
    private val executionDAO: DAOs.ExecutionDAO,
    private val resourceDAO: DAOs.ResourceDAO,
    private val taskWithTasksDAO: DAOs.TaskWithTasksDAO
) {

    //Todo add other DAOs, video 5/7
    val activeTasks = taskDAO.getActive()
    val overdueTasks = activatorDAO.getOverdue()
    val pendingTasks = activatorDAO.getPending()
    val tasksOrderByLastDone = taskDAO.getAllOrderByLastDone()
    val tasksOrderByUsuallyAtThisTime = taskDAO.getAllOrderByUsuallyAtThisTime()
    val activators = activatorDAO.getAllActivators()
    val activeActivators = activatorDAO.getActiveActivators()
    val allResources = resourceDAO.getAllResources()
    val runningExecutions = activatorDAO.getParentRunningExecutions()

    //val getTaskWithTasks = TaskWithTaskDAO.getTasksWithTasks()


    fun getTaskByIdAsFlow(taskId: Long?): Flow<Task> {
        if (taskId == null) return emptyFlow()
        return taskDAO.getByIdAsFlow(taskId).mapNotNull { it }
    }

    fun getMaxActivatorETA(taskId: Long): Flow<Long> {
        return taskDAO.maxActivatorETA(taskId, 9, 10)
    }

    fun getResourceByIdAsFlow(taskId: Long?): Flow<Resource> {
        if (taskId == null) return emptyFlow()
        return resourceDAO.getByIdAsFlow(taskId).mapNotNull { it }
    }

    fun getNotSubtasksOfTask(taskId:Long): Flow<List<Task>> {
        return taskWithTasksDAO.getTasksNotSubTasks(taskId)
    }

    fun getTaskById(taskId: Long): Task {
        return taskDAO.getById(taskId)
    }

    suspend fun upsertTask(task: Task) {
        taskDAO.upsert(task)
    }

    suspend fun insertTask(task: Task): Long {
        return taskDAO.insert(task)
    }


    suspend fun deleteTask(task: Task) {
        withContext(Dispatchers.IO) {
            taskDAO.delete(task)
        }
    }

    fun upsertActivator(activator: Activator) {
        activatorDAO.upsert(activator)
    }

    suspend fun deleteActivator(activator: Activator) {
        withContext(Dispatchers.IO) {
            activatorDAO.delete(activator)
        }
    }


    fun getSubTasksOfTask(taskId: Long): Flow<List<Task>> {
        return taskDAO.getSubTasks(taskId)
    }

    fun getSubTasksOfTaskAsList(taskId: Long): List<Task> {
        return taskDAO.getSubTasksAsList(taskId)
    }

    fun getParentTasksOfTask(taskId: Long): Flow<List<Task>> {
        return taskDAO.getParentTasks(taskId)
    }

    fun addTaskAsLastSubTask(taskId: Long, parentTaskId: Long) {
        taskWithTasksDAO.addTaskAsLastSubTask(taskId, parentTaskId)
    }

    fun increaseTaskPosition(position: Long, parentId: Long) {
        taskDAO.increaseTaskPosition(position, parentId)
    }

    fun decreaseTaskPosition(position: Long, parentId: Long) {
        taskDAO.decreaseTaskPosition(position, parentId)
    }

    fun removeSubTask(parentTaskId: Long,position: Long){
        taskDAO.removeSubTask(parentTaskId,position)
    }
    fun getActivatorById(activatorId: Long): Activator {
        return activatorDAO.getActivatorById(activatorId)
    }

    fun getActivatorByIdAsFlow(activatorId: Long): Flow<Activator> {
        return activatorDAO.getActivatorByIdAsFlow(activatorId)
    }

    fun insertExecution(execution: Execution): Long {
        return executionDAO.insert(execution)
    }

    fun getExecutionById(executionId: Long): Execution {
        return executionDAO.getById(executionId)
    }

    fun updateExecution(execution: Execution) {
        executionDAO.update(execution)
    }

    fun updateExecution(
        executionId: Long,
        end: Int,
        successfullyEnded: Boolean
    ) {
        executionDAO.update(executionId, end, successfullyEnded)
    }

    suspend fun upsertResource(resource: Resource): Long {
        return resourceDAO.upsert(resource)
    }

    fun insertActivator(activator: Activator): Long {
        return activatorDAO.insert(activator)
    }
}