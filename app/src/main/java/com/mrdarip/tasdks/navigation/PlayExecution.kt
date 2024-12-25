import android.os.Bundle
import androidx.navigation.NavType
import com.mrdarip.tasdks.data.entity.ActivatorWithTask
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.ExecutionWithTask
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PlayExecution(val execution: ExecutionWithTask)

fun fromActivatorWithTaskNewExecution(activatorWithTask: ActivatorWithTask): PlayExecution {
    return PlayExecution(
        ExecutionWithTask(
            Execution.of(activatorWithTask.activator),
            activatorWithTask.task
        )
    )
}

fun fromTaskNewExecution(task: Task): PlayExecution {
    return PlayExecution(ExecutionWithTask(Execution.of(task), task))
}

object PlayExecutionNavType : NavType<PlayExecution>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): PlayExecution? {
        return bundle.getString(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): PlayExecution {
        return Json.decodeFromString(PlayExecution.serializer(), value)
    }

    override fun put(bundle: Bundle, key: String, value: PlayExecution) {
        bundle.putString(key, Json.encodeToString(PlayExecution.serializer(), value))
    }
}