package com.mrdarip.tasdks.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.database.TasdksDatabase
import com.mrdarip.tasdks.data.entity.DAOs
import com.mrdarip.tasdks.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasdksViewModel(
    aplication: Application,
    private val TaskDAO: DAOs.TaskDAO,
    private val PlaceDAO: DAOs.PlaceDAO,
    private val ObjectDAO: DAOs.ObjectDAO,
    private val ActivatorDAO: DAOs.ActivatorDAO,
    private val ExecutionDAO: DAOs.ExecutionDAO,
    private val ResourceDAO: DAOs.ResourceDAO,
    private val TaskWithTasksDAO: DAOs.TaskWithTasksDAO,
    private val TaskWithObjectsDAO: DAOs.TaskWithObjectsDAO
) : ViewModel() {
    private val readAllTasks: LiveData<List<Task>>
    private val readAllPlaces: LiveData<List<Place>>
    private val readAllObjects: LiveData<List<Object>>
    private val readAllActivators: LiveData<List<Activator>>
    private val readAllExecutions: LiveData<List<Execution>>
    private val readAllResources: LiveData<List<Resource>>

    private val repository: TasdksRepository


    init {
        val taskDAO = TasdksDatabase.getDatabase(aplication).taskDao()
        val placeDAO = TasdksDatabase.getDatabase(aplication).placeDao()
        val objectDAO = TasdksDatabase.getDatabase(aplication).objectDao()
        val activatorDAO = TasdksDatabase.getDatabase(aplication).activatorDao()
        val executionDAO = TasdksDatabase.getDatabase(aplication).executionDao()
        val resourceDAO = TasdksDatabase.getDatabase(aplication).resourceDao()

        repository =
            TasdksRepository(taskDAO, placeDAO, objectDAO, activatorDAO, executionDAO, resourceDAO)

        readAllTasks = repository.readAllTasks
        readAllPlaces = repository.readAllPlaces
        readAllObjects = repository.readAllObjects
        readAllActivators = repository.readAllActivators
        readAllExecutions = repository.readAllExecutions
        readAllResources =
            repository.readAllResources//TODO: Remember adding taskwithtasks and taskswithobjects
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }

    fun addPlace(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPlace(place)
        }
    }

    fun addObject(obj: Object) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addObject(obj)
        }
    }

    fun addActivator(activator: Activator) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addActivator(activator)
        }
    }

    fun addExecution(execution: Execution) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addExecution(execution)
        }
    }

    fun addResource(resource: Resource) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addResource(resource)
        }
    }




}