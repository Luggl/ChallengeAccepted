package de.thws.challengeaccepted.data.repository

import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.BeitragApiService
import android.content.Context
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class BeitragRepository(private val context: Context) {

    private val api = ApiClient.getRetrofit(context).create(BeitragApiService::class.java)



    suspend fun uploadBeitrag(
        erfuellungId: String,
        beschreibung: String,
        videoFile: File
    ): Boolean {
        Log.d("BeitragRepository", "uploadBeitrag called with erfuellungId=$erfuellungId, beschreibung=$beschreibung, videoFile=${videoFile.name}")

        val beschreibungBody = beschreibung.toRequestBody("text/plain".toMediaTypeOrNull())
        val videoRequestBody = videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
        val videoPart = MultipartBody.Part.createFormData("verification", videoFile.name, videoRequestBody)

        val response = api.uploadBeitrag(
            erfuellungId = erfuellungId,
            beschreibung = beschreibungBody,
            video = videoPart)

        return response.isSuccessful
    }
}