from app.database.models import (
    StandardChallenge,
    Survivalchallenge,
    StandardChallengeSportart,
    SurvivalChallengeSportart,
    Schwierigkeit
)
from app.repositories.challenge_repository import (
    create_challenge,
    delete_challenge_by_id,
    save_standard_challenge_sportart,
    save_survival_challenge_sportart,
    is_user_allowed_to_delete,
    find_active_challenge_by_group, find_teilnehmer_and_user_by_challenge, save_challenge_participation
)
from app.repositories.sportart_repository import find_sportart_by_id
from app.utils.response import response
from datetime import datetime
import uuid

from app.database.models import ChallengeParticipation
from app.repositories.challenge_repository import find_standard_challenge_by_id, find_survival_challenge_by_id, \
    get_dead_teilnehmer
from app.repositories.group_repository import find_group_by_id
from app.repositories.membership_repository import find_memberships_by_group
from app.repositories.task_repository import find_task_by_challenge_and_date, \
    find_aufgabenerfuellung_by_aufgabe_and_user_and_group
from app.utils.auth_utils import get_uuid_formated_id, get_uuid_formated_string
from app.utils.serialize import serialize_user
from app.utils.time import now_berlin, date_today


# ---------- Standard-Challenge erstellen ----------
def create_challenge_standard_logic(user_id, data, group_id):
    """Erstellt eine Standard-Challenge mit Dauer, Startdatum und Sportarten."""

    # Validierung und Konvertierung der Gruppen-ID
    group_id_uuid = get_uuid_formated_id(group_id)
    if not group_id_uuid:
        return response(False, error="Ungültige Gruppen-ID")

    # Validierung und Konvertierung der User-ID
    user_id_uuid = get_uuid_formated_id(user_id)
    if not user_id_uuid:
        return response(False, error="Ungültige User-ID")

    # Es darf nur eine aktive Challenge pro Gruppe geben
    active_challenge_check = find_active_challenge_by_group(group_id_uuid)
    if active_challenge_check:
        return response(False, error="Nur eine aktive Challenge möglich!")

    # Überprüfung auf vollständige Pflichtdaten
    required_fields = ["startdatum", "enddatum", "sportarten"]
    if not all(field in data for field in required_fields):
        return response(False, error="Pflichtfelder fehlen.")

    # Konvertierung und Validierung der Datumsangaben
    try:
        startdatum = datetime.fromisoformat(data["startdatum"])
        enddatum = datetime.fromisoformat(data["enddatum"])
        startdatum = now_berlin().replace(
            year=startdatum.year, month=startdatum.month, day=startdatum.day
        )
        enddatum = now_berlin().replace(
            year=enddatum.year, month=enddatum.month, day=enddatum.day
        )
    except ValueError:
        return response(False, error="Ungültiges Datumsformat.")

    # Überprüfung, ob das Enddatum nach dem Startdatum liegt
    if enddatum <= startdatum:
        return response(False, error="Enddatum muss nach Startdatum liegen.")

    # Erstellung des Challenge-Objekts
    challenge = StandardChallenge(
        challenge_id=uuid.uuid4().bytes,
        gruppe_id=group_id_uuid,
        ersteller_user_id=user_id_uuid,
        startdatum=startdatum.date(),
        enddatum=enddatum.date(),
        typ="standard",
        active=True
    )

    # Speichern der Challenge in der Datenbank
    create_challenge(challenge)

    # Verarbeitung der zugewiesenen Sportarten mit den jeweiligen Intensitäten
    for eintrag in data["sportarten"]:
        sportart_id_str = eintrag.get("sportart_id")
        start_int = eintrag.get("startintensität")
        ziel_int = eintrag.get("zielintensität")

        if not sportart_id_str or start_int is None or ziel_int is None:
            return response(False, error="sportart_id, startintensität und zielintensität erforderlich")

        try:
            sportart_id_bytes = get_uuid_formated_id(sportart_id_str)
            start_int = int(start_int)
            ziel_int = int(ziel_int)
        except ValueError:
            return response(False, error="Ungültige Sportart-ID oder Intensitäten")

        # Abruf der Sportart aus der Datenbank
        sportart = find_sportart_by_id(sportart_id_bytes)
        if not sportart:
            return response(False, error=f"Sportart mit ID {sportart_id_str} nicht gefunden")

        # Verknüpfung der Sportart mit der Challenge
        standard_sportart_link = StandardChallengeSportart(
            challenge_id=challenge.challenge_id,
            sportart_id=sportart.sportart_id,
            startintensitaet=start_int,
            zielintensitaet=ziel_int
        )
        save_standard_challenge_sportart(standard_sportart_link)

    # Anmeldung aller Gruppenmitglieder zur Challenge
    memberships = find_memberships_by_group(group_id_uuid)
    for m in memberships:
        challenge_participation = ChallengeParticipation(
            user_id=m.user_id,
            challenge_id=challenge.challenge_id,
            aktiv=True,
        )
        save_challenge_participation(challenge_participation)

    # Rückgabe der erfolgreich erstellten Challenge-Daten
    return response(True,
                    data={
                        "challenge_id": str(uuid.UUID(bytes=challenge.challenge_id)),
                        "typ": challenge.typ,
                        "startdatum": challenge.startdatum.isoformat(),
                        "enddatum": challenge.enddatum.isoformat()
                    }
                )


# ---------- Survival-Challenge erstellen ----------
def create_challenge_survival_logic(user_id, data, group_id):
    """Erstellt eine Survival-Challenge mit mehreren Sportarten und Schwierigkeitsgraden."""

    group_id_uuid = get_uuid_formated_id(group_id)
    if not group_id_uuid:
        return response(False, error="Ungültige Gruppen-ID")

    # Sicherstellen, dass die Gruppe existiert
    result = find_group_by_id(group_id_uuid)
    if not result:
        return response(False, error="Gruppe wurde nicht gefunden")

    active_challenge_check = find_active_challenge_by_group(group_id_uuid)
    if active_challenge_check:
        return response(False, error="Nur eine aktive Challenge möglich!")

    # Nur Startdatum und Sportarten sind erforderlich (kein Enddatum)
    required_fields = ["startdatum", "sportarten"]
    if not all(field in data for field in required_fields):
        return response(False, error="Pflichtfelder fehlen.")

    # Validierung der Sportarten-Einträge inkl. Schwierigkeitsgrad
    for i, eintrag in enumerate(data["sportarten"]):
        if "sportart_id" not in eintrag or "schwierigkeitsgrad" not in eintrag:
            return response(False,
                            error=f"Sportart #{i + 1} ist unvollständig. Bitte Sportart_ID und Schwierigkeitsgrad angeben.")

        sportart_uuid = get_uuid_formated_id(eintrag["sportart_id"])
        sportart = find_sportart_by_id(sportart_uuid)
        if not sportart:
            return response(False, error=f"Sportart #{i + 1} mit ID {eintrag['sportart_id']} existiert nicht.")

    try:
        # Es wird nur ein Startdatum gesetzt, kein Enddatum
        startdatum = datetime.fromisoformat(data["startdatum"])
        startdatum = now_berlin().replace(
            year=startdatum.year, month=startdatum.month, day=startdatum.day
        )
    except ValueError:
        return response(False, error="Ungültiges Datumsformat für das Startdatum")

    try:
        user_id_uuid = get_uuid_formated_id(user_id)
    except ValueError:
        return response(False, error="Ungültige User-ID (UUID erwartet)")

    # Erstellung eines Challenge-Objekts vom Typ 'survival'
    challenge = Survivalchallenge(
        challenge_id=uuid.uuid4().bytes,
        startdatum=startdatum.date(),
        typ="survival",
        active=True,
        gruppe_id=group_id_uuid,
        ersteller_user_id=user_id_uuid
    )

    create_challenge(challenge)

    # Sportarten mit Schwierigkeitsgrad verknüpfen
    for i, eintrag in enumerate(data["sportarten"]):
        sportart_id_str = eintrag["sportart_id"]
        schwierigkeitsgrad_raw = eintrag["schwierigkeitsgrad"]

        try:
            sportart_id_bytes = uuid.UUID(sportart_id_str).bytes
        except ValueError:
            return response(False, error=f"Sportart-ID #{i + 1} ist ungültig: {sportart_id_str}")

        try:
            # Schwierigkeitsgrad wird in Enum umgewandelt
            schwierigkeitsgrad = Schwierigkeit(schwierigkeitsgrad_raw.lower())
        except ValueError:
            return response(False, error=f"Ungültiger Schwierigkeitsgrad bei Sportart #{i + 1}: {schwierigkeitsgrad_raw}")

        sportart = find_sportart_by_id(sportart_id_bytes)
        if not sportart:
            return response(False, error=f"Sportart nicht gefunden: {sportart_id_str}")

        # Speichern der Verknüpfung Challenge und Sportart mit Schwierigkeitsgrad
        survival_link = SurvivalChallengeSportart(
            challenge_id=challenge.challenge_id,
            sportart_id=sportart.sportart_id,
            schwierigkeitsgrad=schwierigkeitsgrad
        )
        save_survival_challenge_sportart(survival_link)

    memberships = find_memberships_by_group(group_id_uuid)
    for m in memberships:
        challenge_participation = ChallengeParticipation(
            user_id=m.user_id,
            challenge_id=challenge.challenge_id,
            aktiv=True,
        )
        save_challenge_participation(challenge_participation)

    # Rückgabe enthält nur das Startdatum
    return response(True,
                    data={
                        "challenge_id": str(uuid.UUID(bytes=challenge.challenge_id)),
                        "typ": challenge.typ,
                        "startdatum": challenge.startdatum.isoformat()
                    }
                )


# ---------- Challenge löschen ----------
def delete_challenge_logic(challenge_id, user_id):
    if not is_user_allowed_to_delete(challenge_id, user_id):
        return response(False, error="Keine Berechtigung, diese Challenge zu löschen.")

    delete_success = delete_challenge_by_id(challenge_id)
    if not delete_success:
        return response(False, error="Challenge konnte nicht gelöscht werden oder existiert nicht.")

    return response(True, data="Challenge erfolgreich gelöscht.")


def challenge_overview_logic(challenge_id, user_id):
    challenge = find_survival_challenge_by_id(challenge_id) or find_standard_challenge_by_id(challenge_id)
    if not challenge:
        return response(False, error="Challenge konnte nicht gefunden werden.")


    teilnehmer = find_teilnehmer_and_user_by_challenge(challenge_id)
    #Prüfen, ob User in Challenge (Sonst nicht erlaubt, die Informationen zur Challenge zu erhalten
    usercheck = False
    for t in teilnehmer:
        if t.user_id == user_id:
            usercheck = True

    if not usercheck:
        return response(False, error="Nur Challenge-Teilnehmer dürfen sich die Challenge ansehen")

    #Hole die aktuelle Aufgabe
    today = date_today()
    aufgabe = find_task_by_challenge_and_date(challenge_id, today)
    if not aufgabe:
        return response(False, error="Keine Aufgabe gefunden")

    overview_data ={
        "challenge_mode": challenge.typ,
        "challenge_days": (today - challenge.startdatum).days + 1,
        "players": [],
        "current_task_time_left": aufgabe.deadline,
        "task_completed_by": [],
        "dead_users": [],
    }

    #Spieler-Daten + Abgeschlossene Aufgabenerfüllungen sammeln
    for t in teilnehmer:
        erfuellung = find_aufgabenerfuellung_by_aufgabe_and_user_and_group(aufgabe.aufgabe_id, t.user_id, challenge.gruppe_id)

        overview_data["players"].append({
            "user_id": get_uuid_formated_string(t.user_id),
            "username": t.user.username,
            "profilbild_url": t.user.profilbild_url,
            "status": erfuellung.status.value
        })

        if erfuellung.status.value == "abgeschlossen":
            overview_data["task_completed_by"].append(serialize_user(t.user))

    if challenge.typ == "survival":
        overview_data["dead_users"] = get_dead_teilnehmer(challenge.challenge_id)

    return response(True, data=overview_data)

