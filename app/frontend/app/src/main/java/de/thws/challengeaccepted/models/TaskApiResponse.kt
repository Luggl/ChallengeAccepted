// TaskApiResponse.kt
package de.thws.challengeaccepted.models

data class TaskApiResponse(
    val message: TaskMessage
)

data class TaskMessage(
    val Aufgaben: List<AufgabeApiModel>,
    val Hinweise: List<GruppeHinweis>
)

data class AufgabeApiModel(
    val aufgabe_id: String,
    val beschreibung: String,
    val datum: String,
    val dauer: String?,
    val deadline: String,
    val erfuellung_id: String,
    val gruppe_id: String,
    val status: String,
    val unit: String,
    val user_id: String,
    val zielwert: Int
)

data class GruppeHinweis(
    val `gruppe-id`: String,
    val `gruppe-name`: String,
    val status: String
)
