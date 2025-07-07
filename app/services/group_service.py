import uuid
from datetime import timedelta

from sqlalchemy.exc import SQLAlchemyError

from app.utils.time import now_berlin, date_today
from repositories.membership_repository import find_membership, delete_membership, create_membership
from utils.auth_utils import get_uuid_formated_id
from utils.response import response
from app.database.models import Membership
from app.repositories.group_repository import (
    SessionLocal,
    Gruppe,
    find_group_by_id,
    update_group,
    find_group_by_invite_code,
    delete_group_by_id,
    get_group_feed_by_group_id
)

# Gruppe erstellen
def create_group_logic(name, beschreibung, gruppenbild, created_by):
    try:
        invite_link = str(uuid.uuid4())  # einfacher Invite-Code
    except ValueError:
        return response(False, error="Fehler beim Erzeugen des Invite-Links")

    try:
        user_id_uuid = get_uuid_formated_id(created_by)
    except ValueError:
        return response(False, error="User-ID konnte nicht umformatiert werden")

    # Hier der DB-Zugriff direkt in Services, da Gruppe und Membership durch FK-Bedingung in einer Session comitted werden müssen!
    try:

        with SessionLocal() as session:
            group = Gruppe(
                gruppenname=name,
                beschreibung=beschreibung,
                gruppenbild=gruppenbild,
                einladungscode=invite_link,
                einladungscode_gueltig_bis=now_berlin() + timedelta(hours=4),
                erstellungsDatum=date_today()
            )


            session.add(group)
            session.flush()

            membership = Membership(
                user_id=user_id_uuid,
                gruppe_id=group.gruppe_id,
                isAdmin=True
            )
            session.add(membership)
            session.commit()
            session.refresh(group)

            return response(True, {
                "id": str(uuid.UUID(bytes=group.gruppe_id)),
                "name": group.gruppenname,
                "beschreibung": group.beschreibung,
                "invite_link": group.einladungscode
            })

    except SQLAlchemyError as e:
        # Fehler bei DB-Zugriff
        return response(False, error=f"Datenbankfehler: {str(e)}")

    except Exception as e:
        # Fallback für unerwartete Fehler
        return response(False, error=f"Unbekannter Fehler: {str(e)}")



# Einladung erstellen
def invitation_link_logic(group_id, user_id):
    try:
        group_id = get_uuid_formated_id(group_id)
        user_id = get_uuid_formated_id(user_id)
    except ValueError:
        return response(False, error="Ungültige Group oder User-ID")

    group = find_group_by_id(group_id)
    if not group:
        return response(False, error="Gruppe nicht gefunden.")

    membership = find_membership(user_id, group.gruppe_id)
    if not membership:
        return response(False, "User nicht Member der Gruppe!")
    try:
        group.einladungscode = str(uuid.uuid4())
        group.einladungscode_gueltig_bis = now_berlin() + timedelta(hours=4)  # Link ist 4 Std gültig
        updated = update_group(group)
        if not updated:
            return response(False, error="Link konnte nicht aktualisiert werden.")
    except ValueError as e:
        return response(False, error=f"Fehler beim Aktualisieren: {str(e)}")

    return response(True, group.einladungscode)

# Gruppe beitreten per Link
def join_group_via_link_logic(user_id, invitation_link):
    group = find_group_by_invite_code(invitation_link)
    if not group:
        return response(False, error="Ungültiger Einladungslink.")

    # Ist Ablaufdatum kleiner als aktuelles
    if group.einladungscode_gueltig_bis.date() < now_berlin().date():
        return response(False, error="Einladungslink ist abgelaufen.")

    try:
        user_id_uuid = get_uuid_formated_id(user_id)
    except ValueError:
        return response(False, error="User-ID ungültig")

    # Failcheck, falls User bereits Gruppenmitglied!
    membershipcheck = find_membership(user_id_uuid, group.gruppe_id)
    if membershipcheck:
        return response(False, "User bereits Mitglied der Gruppe")

    membership = Membership(
        user_id=user_id_uuid,
        gruppe_id=group.gruppe_id,
        isAdmin=False)

    result = create_membership(membership)
    if not result:
        return response(False, "User konnte nicht zur Gruppe hinzugefügt werden.")

    return response(True, "Gruppe beigetreten.")

# Gruppe löschen
def delete_group_logic(group_id, user_id):

    try:
        user_id_bytes = get_uuid_formated_id(user_id)
        group_id_bytes = get_uuid_formated_id(group_id)
    except ValueError:
        return response(False, "User oder Group-ID ungültig")

    membership = find_membership(user_id_bytes, group_id_bytes)
    if not membership:
        return response(False, "Membership existiert nicht - Entweder Gruppe falsch oder User nicht berechtigt")

    if not membership.isAdmin:
        return response(False, "User darf die Gruppe nicht löschen")

    delete_success = delete_group_by_id(group_id_bytes)
    if not delete_success:
        return response(False, "Löschen nicht erlaubt oder fehlgeschlagen.")

    return response(True, "Gruppe erfolgreich gelöscht.")

# Gruppenfeed abrufen
def get_group_feed_logic(group_id, user_id):
    try:
        group_id_uuid = get_uuid_formated_id(group_id)
        user_id_uuid = get_uuid_formated_id(user_id)
    except ValueError:
        return response(False, error="User oder Group-ID ungültig")

    membership = find_membership(user_id_uuid, group_id_uuid)
    if not membership:
        return response(False, "User ist kein Gruppenmitglied!")

    feed = get_group_feed_by_group_id(group_id_uuid, user_id_uuid)
    if not feed:
        return response(True, "Keine Daten.")
    return response(True, feed)


def leave_group_logic(user_id, group_id):
    try:
        user_id_str = get_uuid_formated_id(user_id)
        group_id_str = get_uuid_formated_id(group_id)
    except ValueError:
        return response(False, error="User oder Group-ID ungültig")

    group =find_group_by_id(group_id_str)
    if not group:
        return response(False, error="Gruppe nicht gefunden.")

    result = delete_membership(user_id_str, group_id_str)
    if not result:
        return response(False, error="Fehlgeschlagen")

    return response(True, "User entfernt")