# from app.repositories.feed_repository import (
#     get_feed_entries_for_user,
#     update_post_for_user
# )
from app.utils.response import response


# Feed abrufen (z.B. alle Gruppen-Posts des Users)
def get_feed_logic(user_id):
    feed_entries = get_feed_entries_for_user(user_id)
    if feed_entries is None:
        return response(False, error="Feed konnte nicht geladen werden.")

    return response(True, data=feed_entries)


# Post aktualisieren (Beschreibung oder Bild ändern)
def update_post_logic(data, post_id, user_id):
    new_text = data.get("text")
    new_image_url = data.get("image_url")  # optional

    if not new_text and not new_image_url:
        return response(False, error="Keine Änderungen übermittelt.")

    success = update_post_for_user(post_id, user_id, new_text, new_image_url)
    if not success:
        return response(False, error="Bearbeitung nicht erlaubt oder fehlgeschlagen.")

    return response(True, data="Post wurde aktualisiert.")
