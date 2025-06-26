package de.thws.challengeaccepted.network

import de.thws.challengeaccepted.models.LoginRequest
import de.thws.challengeaccepted.models.LoginResponse
import de.thws.challengeaccepted.models.PasswordResetConfirmRequest
import de.thws.challengeaccepted.models.PasswordResetConfirmResponse
import de.thws.challengeaccepted.models.PasswordResetRequest
import de.thws.challengeaccepted.models.PasswordResetResponse
import de.thws.challengeaccepted.models.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path

interface UserService {
    //registerUser
    @POST("user")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Void

    //loginUser
    @POST("login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): LoginResponse

    //requestPasswordReset
    @POST("password-reset")
    suspend fun requestPasswordReset(@Body request: PasswordResetRequest): PasswordResetResponse

    //confirmPasswordReset
    @POST("password-reset/confirm")
    suspend fun confirmPasswordReset(@Body request: PasswordResetConfirmRequest): PasswordResetConfirmResponse


    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: String)

}
