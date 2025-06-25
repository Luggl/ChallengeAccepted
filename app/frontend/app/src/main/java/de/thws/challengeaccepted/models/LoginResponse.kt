package de.thws.challengeaccepted.models

data class LoginResponse(
    val access_token: String?,
    val message: String?,
    val user: UserResponse?
)

data class UserResponse(
    val email: String?,
    val id: String?,
    val username: String?
)