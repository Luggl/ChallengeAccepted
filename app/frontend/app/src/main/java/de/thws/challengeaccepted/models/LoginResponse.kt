package de.thws.challengeaccepted.models
import de.thws.challengeaccepted.models.UserApiModel


data class LoginResponse(
    val access_token: String?,
    val message: String?,
    val user: UserApiModel?
)