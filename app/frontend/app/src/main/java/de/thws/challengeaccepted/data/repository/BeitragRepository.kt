package de.thws.challengeaccepted.data.repository

import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.BeitragApiService
import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class BeitragRepository(private val context: Context) {

    private val api = ApiClient.getRetrofit(context).create(BeitragApiService::class.java)

    suspend fun uploadBeitrag(
        userId: String,
        erfuellungId: String,
        beschreibung: String,
        videoFile: File
    ): Boolean {
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
        val erfuellungIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), erfuellungId)
        val beschreibungBody = RequestBody.create("text/plain".toMediaTypeOrNull(), beschreibung)

        val videoRequestBody = RequestBody.create("video/mp4".toMediaTypeOrNull(), videoFile)
        val videoPart = MultipartBody.Part.createFormData("video", videoFile.name, videoRequestBody)

        val response = api.uploadBeitrag(userIdBody, erfuellungIdBody, beschreibungBody, videoPart)

        return response.isSuccessful
    }
}