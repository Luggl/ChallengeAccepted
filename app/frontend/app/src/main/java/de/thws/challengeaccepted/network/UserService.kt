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

interface UserService {
    //registerUser
    @POST("user")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<Void>

    //loginUser
    @POST("login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    //requestPasswordReset
    @POST("password-reset")
    fun requestPasswordReset(@Body request: PasswordResetRequest): Call<PasswordResetResponse>

    //confirmPasswordReset
    @POST("password-reset/confirm")
    fun confirmPasswordReset(@Body request: PasswordResetConfirmRequest): Call<PasswordResetConfirmResponse>

}
