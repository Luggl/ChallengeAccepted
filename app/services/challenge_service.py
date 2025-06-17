from app.database.models import StandardChallenge, Survivalchallenge, StandardChallengeSportart
from app.repositories.challenge_repository import (
    create_challenge,
    delete_challenge_by_id,
#    is_user_allowed_to_delete
)
from app.repositories.sportart_repository import find_sportart_by_id
from app.utils.response import response
from datetime import datetime
import uuid

# ---------- Standard-Challenge erstellen ----------
def create_challenge_standard_logic(user_id, data, group_id):
    """Erstellt eine Standard-Challenge mit Dauer, Startdatum und Sportarten."""
    # Pflichtfelder prüfen
    required_fields = ["startdatum", "enddatum", "sportarten"]
    if not all(field in data for field in required_fields):
        return response(False, error="Pflichtfelder fehlen.")

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

    if enddatum <= startdatum:
        return response(False, error="Enddatum muss nach Startdatum liegen.")

    dauer = (enddatum - startdatum).days

    # Challenge-Objekt anlegen
    challenge = StandardChallenge(
        challenge_id=uuid.uuid4().bytes,
        gruppe_id=group_id,
        ersteller_user_id=user_id,
        startdatum=startdatum.date(),
        enddatum=enddatum.date(),
        dauer=dauer
    )

    create_challenge(challenge)

    for eintrag in data["sportarten"]:
        sportart_id_str = eintrag.get("sportart_id")
        start_int = eintrag.get("startintensität")
        ziel_int = eintrag.get("zielintensität")

        if not sportart_id_str or start_int is None or ziel_int is None:
            return response(False, error="sportart_id, startintensität und zielintensität erforderlich.")

        try:
            sportart_uuid = uuid.UUID(sportart_id_str)
            start_int = int(start_int)
            ziel_int = int(ziel_int)
        except ValueError:
            return response(False, error="Ungültige Sportart-ID oder Intensitäten.")

        sportart = find_sportart_by_id(sportart_uuid.bytes)
        if not sportart:
            return response(False, error=f"Sportart mit ID {sportart_id_str} nicht gefunden.")

        standard_sportart_link = StandardChallengeSportart(
            challenge_id=challenge.challenge_id,
            sportart_id=sportart.sportart_id,
            startintensitaet=start_int,
            zielintensitaet=ziel_int
        )
        save_standard_challenge_sportart(standard_sportart_link)

    return response(True, data={"id": challenge.challenge_id.hex})


# ---------- Survival-Challenge erstellen ----------
def create_challenge_survival_logic(user_id, data):
    """Erstellt eine Survival-Challenge mit Sportart und Schwierigkeitsgrad."""
    required_fields = ["group_id", "startdatum", "sportart_id", "schwierigkeitsgrad"]
    if not all(field in data for field in required_fields):
        return response(False, error="Pflichtfelder fehlen.")

    try:
        startdatum = datetime.fromisoformat(data["startdatum"])
    except ValueError:
        return response(False, error="Ungültiges Datumsformat für Startdatum.")

    challenge = Survivalchallenge(
        challenge_id=uuid.uuid4().bytes,
        gruppe_id=data["group_id"],
        ersteller_user_id=user_id,
        ersteller_gruppe_id=data["group_id"],
        startdatum=startdatum
    )

    sportart = find_sportart_by_id(data["sportart_id"])
    if sportart:
        challenge.sportarten.append(sportart)

    created = create_challenge(challenge)
    if not created:
        return response(False, error="Challenge konnte nicht gespeichert werden.")

    return response(True, data={"id": created.challenge_id.hex()})


# ---------- Challenge löschen ----------
def delete_challenge_logic(challenge_id, user_id):
    if not is_user_allowed_to_delete(challenge_id, user_id):
        return response(False, error="Keine Berechtigung, diese Challenge zu löschen.")

    success = delete_challenge_by_id(challenge_id)
    if not success:
        return response(False, error="Löschen fehlgeschlagen.")

    return response(True, data="Challenge gelöscht.")
