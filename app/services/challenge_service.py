from app.database.models import Challenge
from app.utils.response import response
from app.repositories.challenge_repository import (
    save_challenge,
    delete_challenge_by_id,
    is_user_allowed_to_delete
)

from datetime import datetime
import uuid


# Challenge erstellen
def create_challenge_logic(user_id, data):
    title = data.get("title")
    description = data.get("description")
    group_id = data.get("group_id")
    deadline = data.get("deadline")  # Optional, falls gesetzt

    if not title or not group_id:
        return response(False, error="Titel und Gruppen-ID sind erforderlich.")

    # Optional: Datum parsen
    deadline_dt = None
    if deadline:
        try:
            deadline_dt = datetime.fromisoformat(deadline)
        except ValueError:
            return response(False, error="Ungültiges Datumsformat für Deadline.")

    challenge = Challenge(
        id=str(uuid.uuid4()),
        title=title,
        description=description,
        group_id=group_id,
        created_by=user_id,
        deadline=deadline_dt
    )

    saved = save_challenge(challenge)
    if not saved:
        return response(False, error="Challenge konnte nicht gespeichert werden.")

    return response(True, data={
        "id": challenge.id,
        "title": challenge.title,
        "group_id": challenge.group_id,
        "description": challenge.description
    })


# Challenge löschen
def delete_challenge_logic(challenge_id, user_id):
    if not is_user_allowed_to_delete(challenge_id, user_id):
        return response(False, error="Keine Berechtigung, diese Challenge zu löschen.")

    success = delete_challenge_by_id(challenge_id)
    if not success:
        return response(False, error="Löschen fehlgeschlagen.")

    return response(True, data="Challenge gelöscht.")
