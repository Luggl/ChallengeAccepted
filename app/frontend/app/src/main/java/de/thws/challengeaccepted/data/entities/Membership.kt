package de.thws.challengeaccepted.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "memberships",
    primaryKeys = ["userId", "gruppeId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Gruppe::class,
            parentColumns = ["gruppeId"],
            childColumns = ["gruppeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["gruppeId"])  // genau hier behebst du die Warnung
    ]
)
data class Membership(
    val userId: String,
    val gruppeId: String,
    val isAdmin: Boolean
)
