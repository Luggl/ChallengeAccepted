from app.repositories.membership_repository import (
    delete_membership,
    is_user_admin,
    find_memberships_by_user
)
from app.utils.response import response
from repositories.group_repository import find_group_by_id
from utils.auth_utils import get_uuid_formated_id
from utils.serialize import serialize_gruppe


# User aus Gruppe entfernen
def remove_user_logic(group_id, target_user_uuid, requester_id):
    if not is_user_admin(group_id, requester_id):
        return response(False, error="Nur Admins können Mitglieder entfernen.")

    success = delete_membership(group_id, target_user_uuid)
    if not success:
        return response(False, error="Entfernen fehlgeschlagen oder nicht erlaubt.")

    return response(True, data="User wurde aus der Gruppe entfernt.")

# Gruppenübersicht entspricht den Memberships
def get_membership_overview_logic(user_id):
    user_id_uuid = get_uuid_formated_id(user_id)
    memberships = find_memberships_by_user(user_id_uuid)
    if not memberships:
        return response(False, "User in keiner Gruppe enthalten")

    groups = []
    for membership in memberships:
        group = find_group_by_id(membership.gruppe_id)
        groups.append(serialize_gruppe(group))
    return response(True, groups)
