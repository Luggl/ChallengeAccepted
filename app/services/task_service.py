from app.database.models import Aufgabe
from app.utils.response import response
from app.repositories.task_repository import (
    find_task_by_id,

)


# Einzelne Aufgabe abrufen (nur wenn User dazugehört)
def get_task_logic(task_id, user_id):
    task = find_task_by_id(task_id)
    if not task:
        return response(False, error="Aufgabe nicht gefunden.")

    # Optional: Berechtigungsprüfung – gehört der Task dem User?
    if task.user_id != user_id:
        return response(False, error="Zugriff nicht erlaubt.")

    return response(True, data={
        "id": task.id,
        "titel": task.title,
        "beschreibung": task.description,
        "status": task.status,
        "gruppe": task.group_id
    })


# # Aufgabe als erledigt markieren
# def complete_task_logic(task_id, user_id):
#     success = mark_task_as_complete(task_id, user_id)
#     if not success:
#         return response(False, error="Aufgabe konnte nicht abgeschlossen werden.")
#
#     return response(True, data="Aufgabe als erledigt markiert.")
#
# # Vote abgeben für ein Task-Ergebnis
# def vote_logic(user_id, task_id, vote):
#     # Optional: prüfen, ob User schon abgestimmt hat
#     if has_user_already_voted(user_id, task_id):
#         return response(False, error="Du hast bereits abgestimmt.")
#
#     result = save_vote(user_id, task_id, vote)
#     if not result:
#         return response(False, error="Stimme konnte nicht gespeichert werden.")
#
#     return response(True, data="Stimme erfolgreich abgegeben.")
