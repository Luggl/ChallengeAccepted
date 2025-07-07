package de.thws.challengeaccepted.models

import de.thws.challengeaccepted.data.entities.User

// Wandelt das API-Model in das Datenbank-Model (Entity) um
fun UserApiModel.toRoomUser(): User {
    return User(
        userId = this.id,
        username = this.username,
        email = this.email,
        streak = this.streak,
        profilbild = this.profilbildUrl
    )
}