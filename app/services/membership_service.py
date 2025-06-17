from app.repositories.membership_repository import (
    remove_member,
    is_user_admin_of_group
)
from app.utils.response import response


# User aus Gruppe entfernen
def remove_user_logic(group_id, target_user_uuid, requester_id):
    if not is_user_admin_of_group(group_id, requester_id):
        return response(False, error="Nur Admins können Mitglieder entfernen.")

    success = remove_member(group_id, target_user_uuid)
    if not success:
        return response(False, error="Entfernen fehlgeschlagen oder nicht erlaubt.")

    return response(True, data="User wurde aus der Gruppe entfernt.")
