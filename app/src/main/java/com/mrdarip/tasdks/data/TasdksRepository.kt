package com.mrdarip.tasdks.data

import android.util.Log
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.ActivatorWithTask
import com.mrdarip.tasdks.data.entity.DAOs
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.ExecutionStatus
import com.mrdarip.tasdks.data.entity.ExecutionWithTask
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.data.entity.TaskWithActivator
import com.mrdarip.tasdks.screens.playScreens.ExecutionWithTaskAndActivator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import java.time.Instant

class TasdksRepository(
    private val taskDAO: DAOs.TaskDAO,
    private val activatorDAO: DAOs.ActivatorDAO,
    private val executionDAO: DAOs.ExecutionDAO,
    private val taskWithTasksDAO: DAOs.TaskWithTasksDAO
) {

    //Todo add other DAOs, video 5/7
    val activeTasks = taskDAO.getActive()
    val overdueTasks = activatorDAO.getOverdue()
    val pendingTasks = activatorDAO.getPending()
    val overdueTasksWithStats = activatorDAO.getOverdueWithStats()
    val pendingTasksWithStats = activatorDAO.getPendingWithStats()
    val tasksOrderByLastDone = taskDAO.getAllOrderByLastDone()
    val tasksOrderByUsuallyAtThisTime = taskDAO.getAllOrderByUsuallyAtThisTime()
    val activators = activatorDAO.getAllActivators()
    val activeActivators = activatorDAO.getActiveActivators()
    val runningExecutionsFlow = activatorDAO.getParentRunningExecutions()

    //val getTaskWithTasks = TaskWithTaskDAO.getTasksWithTasks()


    fun getTaskByIdAsFlow(taskId: Long?): Flow<Task> {
        if (taskId == null) return emptyFlow()
        return taskDAO.getByIdAsFlow(taskId).mapNotNull { it }
    }

    fun getMaxActivatorETA(taskId: Long): Flow<Long> {
        return taskDAO.maxActivatorETA(taskId, 9, 10)
    }

    fun getNotSubtasksOfTask(taskId: Long): Flow<List<Task>> {
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

    fun removeSubTask(parentTaskId: Long, position: Long) {
        taskDAO.removeSubTask(parentTaskId, position)
    }

    fun getActivatorById(activatorId: Long): Activator? {
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
        executionStatus: ExecutionStatus
    ) {
        executionDAO.update(executionId, end, executionStatus)
    }

    fun insertActivator(activator: Activator): Long {
        return activatorDAO.insert(activator)
    }

    fun getRunningExecutions(): Flow<List<Execution>> {
        return executionDAO.getRunningExecutions()
    }

    private fun getParentExecution(execution: Execution): Execution? {
        Log.i("GetParentExecution", "Getting parent of: $execution")
        if (execution.executionRoute.route.isEmpty()) return null
        Log.i("GetParentExecution", "Getting parent for route: ${execution.executionRoute.route}")
        return this.getExecutionById(execution.executionRoute.route.last())
    }

    fun getExecution(executionId: Long): Execution {
        return executionDAO.getById(executionId)
    }

    fun getExecutionWithTaskByExeId(executionId: Long): ExecutionWithTask {
        return executionDAO.getExecutionWithTaskByExeId(executionId)
    }

    fun getRunningExecutionChildOf(executionId: Long): ExecutionWithTask {
        return executionDAO.getRunningExecutionChildOf(executionId)
    }

    fun upsertExecution(newExecution: Execution): Long {
        return executionDAO.upsert(newExecution)
    }

    fun getTaskByActivatorId(activatorId: Long): Task {
        return taskDAO.getTaskByActivatorId(activatorId)
    }

    fun getActivatorWithTaskByActivatorId(activatorId: Long): ActivatorWithTask {
        return activatorDAO.getActivatorWithTaskByActivatorId(activatorId)
    }

    /**
     * Starts the execution of the task
     * @return the leaf ExecutionWithTask
     */
    fun startExecution(executionWithTaskAndActivator: ExecutionWithTaskAndActivator): ExecutionWithTaskAndActivator {
        /*
          We are starting task A
              A
          0/ 1| 2\
          B   O   O
          |\  /\  |
          C X X X X

            task A, B and C are started
            We return C
         */

        val startedExecution = executionWithTaskAndActivator.copy(
            execution = executionWithTaskAndActivator.execution.copy(
                start = Instant.now(),
                end = null,
                executionStatus = ExecutionStatus.RUNNING
            )
        )

        val executionToStart = startedExecution.copy(
            execution = startedExecution.execution.copy(
                //we insert it in case it is a new execution
                executionId = executionDAO.upsert(startedExecution.execution),

                //start the execution
                start = Instant.now(),
                end = null,
                executionStatus = ExecutionStatus.RUNNING
            )
        )

        Log.i("StartExecution", "Starting execution: $executionToStart")
        val taskBranch = taskDAO.getBranchOfExclusive(
            taskId = executionToStart.execution.taskId
        )
        Log.i("StartExecution", "Tasks to start: $taskBranch")

        var executionLastExecution: Execution = executionToStart.execution
        taskBranch.forEachIndexed { index, task ->
            val executionToInsert = Execution(
                start = Instant.now(),
                end = null,
                executionStatus = ExecutionStatus.RUNNING,
                activatorId = executionToStart.execution.activatorId,
                taskId = task.taskId,
                tasksRoute = executionLastExecution.tasksRoute.plus(executionLastExecution.taskId),
                executionRoute = executionLastExecution.executionRoute.plus(executionLastExecution.executionId),
                childNumber = 0
            )

            executionLastExecution = executionToInsert.copy(
                executionId = executionDAO.upsert(executionToInsert)
            )
        }

        val allTasks = listOf(startedExecution.task).union(taskBranch)
        Log.i("StartExecution", "returning: ${allTasks.last().name}")

        return ExecutionWithTaskAndActivator(
            executionLastExecution,
            allTasks.last(),
            executionToStart.activator
        )
    }

    /**
     * Completes the execution of the task
     * @return the next leaf ExecutionWithTask or null if there is no next task
     */
    fun completeExecution(
        executionToComplete: ExecutionWithTaskAndActivator,
        reason: ExecutionStatus
    ): ExecutionWithTaskAndActivator? {
        Log.i("SetExecutionAsCompleted", "Completing execution: $executionToComplete")
        var completingExecution = executionToComplete.copy(
            execution = executionToComplete.execution.copy(
                end = Instant.now(),
                executionStatus = ExecutionStatus.mix(
                    executionToComplete.execution.executionStatus,
                    reason
                )
            )
        )
        completingExecution = completingExecution.copy(
            execution = completingExecution.execution.copy(
                executionId = executionDAO.upsert(completingExecution.execution)
            )
        )
        Log.i("SetExecutionAsCompleted", "Execution completed: $completingExecution")

        val execution = completingExecution.execution
        val task = completingExecution.task
        val activator = completingExecution.activator


        //TODO: Use a query that gets completed parents from actual, like in startExecution but with parents instead of children
        val parentExecution = getParentExecution(execution)
        Log.i("SetExecutionAsCompleted", "A: it has parent execution: $parentExecution")
        if (parentExecution != null) {

            val brothers = getSubTasksOfTaskAsList(parentExecution.taskId)
            Log.i(
                "TasdksRepository",
                "execution of ${task.name} has childNumber: ${execution.childNumber} and has ${brothers.size} brothers: ${brothers.map { it.name }}"
            )

            //if it has next brother
            if (parentExecution.childNumber + 1 < brothers.size) {
                val nextBrotherTask = brothers[parentExecution.childNumber + 1]
                Log.i("SetExecutionAsCompleted", "Next brother task: ${nextBrotherTask.name}")

                executionDAO.upsert(parentExecution.copy(childNumber = parentExecution.childNumber + 1))

                val returningExecution =
                    Execution.of(TaskWithActivator(nextBrotherTask, activator)).copy(
                        tasksRoute = parentExecution.tasksRoute.plus(parentExecution.taskId),
                        executionRoute = parentExecution.executionRoute.plus(parentExecution.executionId),
                        childNumber = 0
                    )

                return startExecution(
                    ExecutionWithTaskAndActivator(
                        returningExecution,
                        nextBrotherTask,
                        activator
                    )
                )
            } else {
                //complete parent

                val parentExecutionToComplete = ExecutionWithTaskAndActivator(
                    parentExecution,
                    taskDAO.getById(parentExecution.taskId),
                    activator
                )
                return completeExecution(parentExecutionToComplete, reason)
            }
        }

        return null
    }

    fun getExecutionWithTaskAndActivatorByExeId(executionId: Long): ExecutionWithTaskAndActivator {
        return executionDAO.getExecutionWithTaskAndActivatorByExeId(executionId)
    }

    fun getParentTasksOfExecutionsFlow(actualExecution: ExecutionWithTaskAndActivator): Flow<List<Task>> {
        Log.i(
            "TasdksRepository",
            "Getting tasks for ${actualExecution.execution.executionRoute.route}"
        )
        return executionDAO.getTasksFromExecutionsIDs(actualExecution.execution.executionRoute.route)
    }

}