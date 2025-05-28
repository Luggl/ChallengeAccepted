import uuid
from datetime import datetime, timedelta
from app.utils.time import now_berlin
from utils.response import response
from app.models.group import Group
from app.repositories.group_repository import (
    save_group,
    find_group_by_invite_link,
    add_user_to_group,
    delete_group_by_id,
    get_group_feed_data,
    get_groups_for_user
)



# Gruppe erstellen
def create_group_logic(name, beschreibung, gruppenbild):
    invite_link = str(uuid.uuid4())  # einfacher Invite-Code
    group = Group(
        name=name,
        beschreibung=beschreibung,
        gruppenbild=gruppenbild,
        invite_link=invite_link
    )

    saved_group = save_group(group)

    return response(True, {
        "id": saved_group.id,
        "name": saved_group.name,
        "beschreibung": saved_group.beschreibung,
        "invite_link": saved_group.invite_link
    })

# Einladung erstellen
def invitation_link_logic(group_id):
    group = find_group_by_id(group_id)
    if not group:
        return response(False, "Gruppe nicht gefunden.")

    group.invite_link = str(uuid.uuid4())
    group.invite_expires_at = now_berlin() + timedelta(minutes=30)  # Link ist 30 min gültig

    updated = save_group(group)
    if not updated:
        return response(False, "Link konnte nicht aktualisiert werden.")

    return response(True, group.invite_link)

# Gruppe beitreten per Link
def join_group_via_link_logic(user_id, invitation_link):
    group = find_group_by_invite_link(invitation_link)
    if not group:
        return response(False, "Ungültiger Einladungslink.")

    # Ist Ablaufdatum kleiner als aktuelles
    if group.invite_expires_at < now_berlin():
        return response(False, "Einladungslink ist abgelaufen.")

    result = add_user_to_group(user_id, group.id)
    if not result:
        return response(False, "User konnte nicht zur Gruppe hinzugefügt werden.")

    return response(True, "Gruppe beigetreten.")

# Gruppe löschen
def delete_group_logic(group_id, user_id):
    # Optional: prüfen, ob user der Admin ist → kommt später
    result = delete_group_by_id(group_id, user_id)
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
