from app.utils.response import response
from repositories.beitrag_repository import update_beitrag, find_beitrag_by_id
from repositories.user_repository import get_user_feed
from utils.auth_utils import get_uuid_formated_id


# Feed abrufen (z.B. alle Gruppen-Posts des Users)
def get_feed_logic(user_id):
    user_id_uuid = get_uuid_formated_id(user_id)
    feed_entries = get_user_feed(user_id_uuid)
    if feed_entries is None:
        return response(False, error="Feed konnte nicht geladen werden.")

    return response(True, data=feed_entries)


# Beschreibung aktualisieren
def update_post_logic(data, beitrag_id, user_id):
    new_beschreibung = data.get("beschreibung")
    beitrag_id_uuid = get_uuid_formated_id(beitrag_id)

    if not new_beschreibung:
        return response(False, error="Keine Änderungen übermittelt.")

    # Check ob User Beitrags-Eigner
    ersteller = find_beitrag_by_id(beitrag_id_uuid).user_id
    if ersteller is not user_id:
        return response(False, error="User nicht berechtigt!")

    update_success = update_beitrag(beitrag_id_uuid, new_beschreibung)
    if not update_success:
        return response(False, error="Bearbeitung fehlgeschlagen.")

    return response(True, data="Post wurde aktualisiert.")
