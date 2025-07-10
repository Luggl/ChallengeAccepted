// Datei: de/thws.challengeaccepted.network.TaskService.kt
package de.thws.challengeaccepted.network

import de.thws.challengeaccepted.models.TaskApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TaskService {
    @GET("task")
    suspend fun getTasksForUser(): TaskApiResponse
}
