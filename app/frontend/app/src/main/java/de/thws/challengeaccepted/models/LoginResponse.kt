package de.thws.challengeaccepted.models

data class LoginResponse(
    val access_token: String?,
    val message: String?,
    val user: UserApiModel?
)

data class UserApiModel(
    val id: String,
    val email: String,
    val username: String
)