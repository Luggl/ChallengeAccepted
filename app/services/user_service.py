from werkzeug.security import generate_password_hash, check_password_hash
from app import db
from app.utils.response import response
from app.utils.time import now_berlin
from app.utils.mail_service import send_password_reset_mail
from datetime import timedelta
from app.database.models import User
from app.database.models import ResetToken
from app.repositories.token_repository import find_token_by_string, save_token, delete_token, delete_token_by_user_id
from app.repositories.user_repository import (
    find_user_by_email,
    save_user,
    delete_user_by_id,
    find_user_by_id,
    update_user
)

import uuid

ALLOWED_UPDATE_FIELDS = {"username", "email", "profilbild"}


# Registrierung eines neuen Users
def register_user_logic(username, email, password):
    # Wenn E-Mail schon vergeben ist -> abbrechen
    existing_user = find_user_by_email(email)
    if existing_user:
        return response(False, error="E-Mail ist bereits registriert.")

    # Passwort hashen für sichere Speicherung
    hashed_pw = generate_password_hash(password)

    # User-Objekt anlegen (UUID statt Integer-ID)
    user = User(
        user_id=uuid.uuid4().bytes,
        username=username,
        email=email,
        passwordHash=hashed_pw
        # Weitere Felder wie Profilbild oder Rolle später ergänzen
    )

    # User speichern
    saved_user = save_user(user)

    # Erfolgreiche Rückgabe
    return response(True, data={
        "id": str(uuid.UUID(bytes=saved_user.user_id)),
        "username": saved_user.username,
        "email": saved_user.email
    })

# Login eines bestehenden Users
def login_user_logic(email, password):
    user = find_user_by_email(email)
    if not user:
        return response(False, error="User nicht gefunden.")

    # Passwort prüfen (gegen gehashtes PW aus DB)
    if not check_password_hash(user.passwordHash, password):
        return response(False, error="Passwort ist falsch.")

    # Wenn Login erfolgreich, dann Daten zurückgeben
    return response(True, data={
        "id": user.user_id.hex(),
        "username": user.username,
        "email": user.email
    })

def forgot_password_logic(email):
    user = find_user_by_email(email)

    # Sicherheit: gleiche Antwort für bekannte & unbekannte E-Mails
    if not user:
        return response(True, data="Falls diese E-Mail existiert, wurde ein Link gesendet.")

    # alten Token löschen, falls vorhanden
    delete_token_by_user_id(user.user_id)

    # Token generieren
    token_str = str(uuid.uuid4())
    expires = now_berlin().date() + timedelta(days=1)

    # ResetToken erzeugen
    token = ResetToken(
        token=token_str,
        user_id=user.user_id,
        gueltigBis=expires
    )
    save_token(token)

    # Mail senden Dummy
    send_password_reset_mail(user.email, token_str)

    return response(True, data="Falls diese E-Mail existiert, wurde ein Link gesendet.")


def reset_password_logic(token_str, new_password):
    token = find_token_by_string(token_str)

    if not token:
        return response(False, error="Ungültiger oder abgelaufener Token.")

    # Token-Gültigkeit prüfen
    if token.gueltigBis < now_berlin().date():
        return response(False, error="Der Token ist abgelaufen.")

    user = token.user
    if not user:
        return response(False, error="Benutzer zu Token nicht gefunden.")

    # Passwort setzen
    user.passwordHash = generate_password_hash(new_password)
    update_user(user)

    # Token löschen nach erfolgreicher Verwendung
    delete_token(token)

    return response(True, data="Passwort erfolgreich zurückgesetzt.")

def get_user_logic(user_id_str):
    try:
        user_id = uuid.UUID(user_id_str).bytes
    except ValueError:
        return response(False, error="Ungültige Benutzer-ID")

    user = find_user_by_id(user_id)
    if not user:
        return response(False, error="Benutzer nicht gefunden")

    # TODO: Kalender-Daten und Streak dynamisch berechnen, wenn nötig
    user_data = {
        "id": str(uuid.UUID(bytes=user.user_id)),
        "username": user.username,
        "email": user.email,
        "profilbild": user.profilbild,
        "streak": user.streak
    }

    return response(True, data=user_data)


def update_user_logic(user_id_str, update_data):
    try:
        user_id = uuid.UUID(user_id_str).bytes
    except ValueError:
        return response(False, error="Ungültige Benutzer-ID")

    user = find_user_by_id(user_id)
    if not user:
        return response(False, error="Benutzer nicht gefunden")

    updated = False
    for field in ALLOWED_UPDATE_FIELDS:
        if field in update_data and getattr(user, field) != update_data[field]:
            setattr(user, field, update_data[field])
            updated = True

    if not updated:
        return response(False, error="Keine gültigen Änderungen übergeben")

    update_user(user)

    return response(True, data="Benutzer erfolgreich aktualisiert")


def update_password_logic(user_id_str, old_password, new_password):
    try:
        user_id = uuid.UUID(user_id_str).bytes  # DB nutzt BLOB
    except ValueError:
        return response(False, error="Ungültige Benutzer-ID")

    user = find_user_by_id(user_id)
    if not user:
        return response(False, error="Benutzer nicht gefunden")

    if not check_password_hash(user.passwordHash, old_password):
        return response(False, error="Altes Passwort ist nicht korrekt")

    user.passwordHash = generate_password_hash(new_password)
    update_user(user)

    return response(True, data="Passwort erfolgreich geändert")

# User löschen (wird über ID angesprochen)
def delete_user_logic(user_id):
    result = delete_user_by_id(user_id)
    if not result:
        return response(False, error="User konnte nicht gelöscht werden oder existiert nicht.")
    return response(True, data="User erfolgreich gelöscht.")
