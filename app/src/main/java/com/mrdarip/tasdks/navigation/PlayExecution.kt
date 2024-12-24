import com.mrdarip.tasdks.data.entity.ActivatorWithTask
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.ExecutionWithTask
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.serialization.Serializable

@Serializable
sealed class PlayExecution {
    abstract fun getExecution(): ExecutionWithTask

    @Serializable
    data class FromActivatorWithTaskNewExecution(val activatorWithTask: ActivatorWithTask) :
        PlayExecution() {
        override fun getExecution(): ExecutionWithTask =
            ExecutionWithTask(Execution.of(activatorWithTask.activator), activatorWithTask.task)
    }

    @Serializable
    data class FromTaskNewExecution(val task: Task) : PlayExecution() {
        override fun getExecution(): ExecutionWithTask = ExecutionWithTask(Execution.of(task), task)
    }

    @Serializable
    data class FromExecutionWithTask(val executionWithTask: ExecutionWithTask) : PlayExecution() {
        override fun getExecution(): ExecutionWithTask = executionWithTask
    }
}