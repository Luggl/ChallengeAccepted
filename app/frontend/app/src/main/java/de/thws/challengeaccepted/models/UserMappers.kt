package de.thws.challengeaccepted.models

import de.thws.challengeaccepted.data.entities.User

fun UserApiModel.toRoomUser(): User = User(
    userId = this.id,
    username = this.username,
    email = this.email
    // Optional: streak = 0, profilbild = null
)
