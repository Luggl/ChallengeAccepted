package de.thws.challengeaccepted.data.dao

import androidx.room.*
import de.thws.challengeaccepted.data.entities.Membership
import de.thws.challengeaccepted.data.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface MembershipDao {

    // NEU: Gibt alle Mitgliedschaften für eine Gruppe als Flow zurück.
    // Das ist die Funktion, die im ViewModel gefehlt hat.
    @Query("SELECT * FROM memberships WHERE gruppeId = :gruppeId")
    fun getMembershipsForGroupAsFlow(gruppeId: String): Flow<List<Membership>>

    // NEU: Fügt eine ganze Liste von Mitgliedschaften auf einmal ein.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(memberships: List<Membership>)

    // --- Bestehende Funktionen ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembership(membership: Membership)

    @Delete
    suspend fun deleteMembership(membership: Membership)

    @Query("""
        SELECT u.* FROM users u
        INNER JOIN memberships m ON u.userId = m.userId
        WHERE m.gruppeId = :gruppeId
    """)
    suspend fun getUsersInGruppe(gruppeId: String): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMemberships(memberships: List<Membership>)

    @Query("DELETE FROM memberships")
    suspend fun clearAll()
}