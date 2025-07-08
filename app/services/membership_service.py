from app.repositories.membership_repository import (
    delete_membership,
    is_user_admin,
    find_memberships_by_user
)
from app.utils.response import response
from app.database.models import AufgabeStatus
from repositories.challenge_repository import find_active_challenge_by_group
from repositories.group_repository import find_group_by_id
from repositories.task_repository import find_aufgabenerfuellung_by_challenge_and_date_and_user
from utils.auth_utils import get_uuid_formated_id
from utils.serialize import serialize_gruppe
from utils.time import date_today


# User aus Gruppe entfernen
def remove_user_logic(group_id, target_user_uuid, requester_id):
    if not is_user_admin(group_id, requester_id):
        return response(False, error="Nur Admins können Mitglieder entfernen.")

    delete_success = delete_membership(group_id, target_user_uuid)
    if not delete_success:
        return response(False, error="Entfernen fehlgeschlagen oder nicht erlaubt.")

    return response(True, data="User wurde aus der Gruppe entfernt.")

# Gruppenübersicht entspricht den Memberships
def get_membership_overview_logic(user_id):
    user_id_uuid = get_uuid_formated_id(user_id)
    memberships = find_memberships_by_user(user_id_uuid)
    if not memberships:
        return response(False, error="User in keiner Gruppe enthalten")

    groups = []
    for membership in memberships:
        group = find_group_by_id(membership.gruppe_id)
        serialized_group = serialize_gruppe(group)

        # Prüfung auf offene Aufgabe
        aufgabe_offen = False
        challenge = find_active_challenge_by_group(group.gruppe_id)
        if challenge:
            erfuellung = find_aufgabenerfuellung_by_challenge_and_date_and_user(
                challenge.challenge_id,
                date_today(),
                user_id_uuid
            )

            if (
                    erfuellung
                    and erfuellung.status == AufgabeStatus.offen
                    and erfuellung.gruppe_id == group.gruppe_id
            ):
                aufgabe_offen = True

        serialized_group["aufgabe"] = aufgabe_offen
        groups.append(serialized_group)

    return response(True, groups)
