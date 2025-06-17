import uuid
import app.repositories.group_repository
from datetime import datetime, timedelta
from app.utils.time import now_berlin, date_today
from repositories.membership_repository import find_membership
from utils.response import response
from app.database.models import Gruppe, Membership
from app.repositories.group_repository import *


# Gruppe erstellen
def create_group_logic(name, beschreibung, gruppenbild, created_by):
    invite_link = str(uuid.uuid4())  # einfacher Invite-Code
    group = Gruppe(
        gruppenname=name,
        beschreibung=beschreibung,
        gruppenbild=gruppenbild,
        einladungscode=invite_link,
        einladungscode_gueltig_bis=date_today(),
        erstellungsDatum=date_today()
    )
    created_group = create_group(group)

    membership = Membership(
        user_id=uuid.UUID(created_by).bytes,
        gruppe_id=created_group.gruppe_id,
        isAdmin=True
    )


    create_membership(membership)

    return response(True, {
        "id": str(uuid.UUID(bytes=created_group.gruppe_id)),
        "name": created_group.gruppenname,
        "beschreibung": created_group.beschreibung,
        "invite_link": created_group.einladungscode
    })

# Einladung erstellen
def invitation_link_logic(group_id):
    group = find_group_by_id(group_id)
    if not group:
        return response(False, "Gruppe nicht gefunden.")

    group.einladungscode = str(uuid.uuid4())
    group.einladungscode_gueltig_bis = now_berlin() + timedelta(hours=4)  # Link ist 4 Std gültig

    updated = update_group(group)
    if not updated:
        return response(False, "Link konnte nicht aktualisiert werden.")

    return response(True, group.einladungscode)

# Gruppe beitreten per Link
def join_group_via_link_logic(user_id, invitation_link):
    group = find_group_by_invite_code(invitation_link)
    if not group:
        return response(False, "Ungültiger Einladungslink.")

    # Ist Ablaufdatum kleiner als aktuelles
    if group.einladungscode_gueltig_bis.date() < now_berlin().date():
        return response(False, "Einladungslink ist abgelaufen.")

    membership = Membership(
        user_id=uuid.UUID(user_id).bytes,
        gruppe_id=group.gruppe_id,
        isAdmin=False)

    result = create_membership(membership)
    if not result:
        return response(False, "User konnte nicht zur Gruppe hinzugefügt werden.")

    return response(True, "Gruppe beigetreten.")

# Gruppe löschen
def delete_group_logic(group_id, user_id):
    # Optional: prüfen, ob user der Admin ist → kommt später
    user_id_bytes = uuid.UUID(user_id).bytes
    group_id_bytes = uuid.UUID(group_id).bytes
    membership = find_membership(user_id_bytes, group_id_bytes)

    if not membership.isAdmin:
        return response(False, "User darf die Gruppe nicht löschen")

    result = delete_group_by_id(group_id)
    if not result:
        return response(False, "Löschen nicht erlaubt oder fehlgeschlagen.")
    return response(True, "Gruppe erfolgreich gelöscht.")

# Gruppenfeed abrufen
def get_group_feed_logic(group_id, user_id):
    feed = get_group_feed_data(group_id, user_id)
    if not feed:
        return response(False, "Zugriff verweigert oder keine Daten.")
    return response(True, feed)

# Gruppenübersicht für User abrufen
def get_group_overview_logic(user_id):
    groups = get_groups_for_user(user_id)
    if not groups:
        return response(False, "Keine Gruppen gefunden.")
    return response(True, groups)
