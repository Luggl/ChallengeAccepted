from app.repositories.membership_repository import (
    promote_member,
    degrade_member,
    remove_member,
    is_user_admin_of_group
)
from app.utils.response import response


# User zum Admin befördern
def promote_user_logic(group_id, target_user_uuid, requester_id):
    # Prüfen, ob der ausführende User Admin ist
    if not is_user_admin_of_group(group_id, requester_id):
        return response(False, error="Nur Admins können befördern.")

    success = promote_member(group_id, target_user_uuid)
    if not success:
        return response(False, error="Beförderung fehlgeschlagen.")

    return response(True, data="User wurde zum Admin befördert.")


# User degradieren (Admin → Mitglied)
def degrade_user_logic(group_id, target_user_uuid, requester_id):
    if not is_user_admin_of_group(group_id, requester_id):
        return response(False, error="Nur Admins können degradieren.")

    success = degrade_member(group_id, target_user_uuid)
    if not success:
        return response(False, error="Degradierung fehlgeschlagen.")

    return response(True, data="User wurde zum Mitglied degradiert.")


# User aus Gruppe entfernen
def remove_user_logic(group_id, target_user_uuid, requester_id):
    if not is_user_admin_of_group(group_id, requester_id):
        return response(False, error="Nur Admins können Mitglieder entfernen.")

    success = remove_member(group_id, target_user_uuid)
    if not success:
        return response(False, error="Entfernen fehlgeschlagen oder nicht erlaubt.")

    return response(True, data="User wurde aus der Gruppe entfernt.")
