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
    find_active_challenge_by_group
)
from app.repositories.sportart_repository import find_sportart_by_id
from app.utils.response import response
from datetime import datetime
import uuid

from utils.auth_utils import get_uuid_formated_id
from utils.time import now_berlin


# ---------- Standard-Challenge erstellen ----------
def create_challenge_standard_logic(user_id, data, group_id):
    """Erstellt eine Standard-Challenge mit Dauer, Startdatum und Sportarten."""
    group_id_uuid = get_uuid_formated_id(group_id)
    if not group_id_uuid:
        return response(False, error="Ungültige Gruppen-ID")

    user_id_uuid = get_uuid_formated_id(user_id)
    if not user_id_uuid:
        return response(False, error="Ungültige User-ID")

    # Nicht mehrere Challenges gleichzeitig erlaubt
    active_challenge_check = find_active_challenge_by_group(group_id_uuid)
    if active_challenge_check:
        return response(False, error="Nur eine aktive Challenge möglich!")

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

    challenge = StandardChallenge(
        challenge_id=uuid.uuid4().bytes,
        gruppe_id=group_id_uuid,
        ersteller_user_id=user_id_uuid,
        startdatum=startdatum.date(),
        enddatum=enddatum.date(),
        dauer=dauer,
        ersteller_gruppe_id=group_id_uuid,
        typ="standard"
    )

    create_challenge(challenge)

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

        sportart = find_sportart_by_id(sportart_id_bytes)

        if not sportart:
            return response(False, error=f"Sportart mit ID {sportart_id_str} nicht gefunden")

        standard_sportart_link = StandardChallengeSportart(
            challenge_id=challenge.challenge_id,
            sportart_id=sportart.sportart_id,
            startintensitaet=start_int,
            zielintensitaet=ziel_int
        )
        save_standard_challenge_sportart(standard_sportart_link)

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
    if not group_id:
        return response(False, error="Ungültige Gruppen-ID")

    # Nicht mehrere Challenges gleichzeitig erlaubt
    active_challenge_check = find_active_challenge_by_group(group_id_uuid)
    if active_challenge_check:
        return response(False, error="Nur eine aktive Challenge möglich!")

    required_fields = ["startdatum", "sportarten"]
    if not all(field in data for field in required_fields):
        return response(False, error="Pflichtfelder fehlen.")

    # Sportarten-Einträge prüfen
    for i, eintrag in enumerate(data["sportarten"]):
        if "sportart_id" not in eintrag or "schwierigkeitsgrad" not in eintrag:
            return response(False,
                            error=f"Sportart #{i + 1} ist unvollständig. Bitte Sportart_ID und Schwierigkeitsgrad angeben.")

    # Startdatum parsen
    try:
        startdatum = datetime.fromisoformat(data["startdatum"])
        startdatum = now_berlin().replace(
            year=startdatum.year, month=startdatum.month, day=startdatum.day
        )
    except ValueError:
        return response(False, error="Ungültiges Datumsformat für das Startdatum")
    # User-ID in bytes umwandeln
    try:
        user_id_uuid = uuid.UUID(user_id).bytes
    except ValueError:
        return response(False, error="Ungültige User-ID (UUID erwartet")
    # Challenge-Objekt anlegen
    challenge = Survivalchallenge(
        challenge_id=uuid.uuid4().bytes,
        gruppe_id=group_id_uuid,
        ersteller_user_id=user_id_uuid,
        ersteller_gruppe_id=group_id_uuid,
        startdatum=startdatum.date(),
        typ="survival"
    )

    create_challenge(challenge)

    # Für jede Sportart: speichern mit Schwierigkeitsgrad
    for i, eintrag in enumerate(data["sportarten"]):
        sportart_id_str = eintrag["sportart_id"]
        schwierigkeitsgrad_raw = eintrag["schwierigkeitsgrad"]

        try:
            sportart_id_bytes = uuid.UUID(sportart_id_str).bytes
        except ValueError:
            return response(False, error=f"Sportart-ID #{i + 1} ist ungültig: {sportart_id_str}")

        # Schwierigkeitsgrad in Enum umwandeln
        try:
            schwierigkeitsgrad = Schwierigkeit(schwierigkeitsgrad_raw.lower())
        except ValueError:
            return response(False, error=f"Ungültiger Schwierigkeitsgrad bei Sportart #{i + 1}: {schwierigkeitsgrad_raw}")

        sportart = find_sportart_by_id(sportart_id_bytes)
        if not sportart:
            return response(False, error=f"Sportart nicht gefunden: {sportart_id_str}")

        survival_link = SurvivalChallengeSportart(
            challenge_id=challenge.challenge_id,
            sportart_id=sportart.sportart_id,
            schwierigkeitsgrad=schwierigkeitsgrad
        )
        save_survival_challenge_sportart(survival_link)


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

    success = delete_challenge_by_id(challenge_id)
    if not success:
        return response(False, error="Challenge konnte nicht gelöscht werden oder existiert nicht.")

    return response(True, data="Challenge erfolgreich gelöscht.")