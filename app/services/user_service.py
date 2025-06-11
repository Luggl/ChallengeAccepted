from werkzeug.security import generate_password_hash, check_password_hash
from app import db
from app.utils.response import response
from app.database.models import User
from app.repositories.user_repository import (
    find_user_by_email,
    save_user,
    delete_user_by_id
)

import uuid


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
        id=str(uuid.uuid4()),
        username=username,
        email=email,
        password_hash=hashed_pw
        # Weitere Felder wie Profilbild oder Rolle später ergänzen
    )

    # User speichern
    saved_user = save_user(user)

    # Erfolgreiche Rückgabe
    return response(True, data={
        "id": saved_user.id,
        "username": saved_user.username,
        "email": saved_user.email
    })

# Login eines bestehenden Users
def login_user_logic(email, password):
    user = find_user_by_email(email)
    if not user:
        return response(False, error="User nicht gefunden.")

    # Passwort prüfen (gegen gehashtes PW aus DB)
    if not check_password_hash(user.password_hash, password):
        return response(False, error="Passwort ist falsch.")

    # Login erfolgreich → Daten zurückgeben
    return response(True, data={
        "id": user.id,
        "username": user.username,
        "email": user.email
    })

# Passwort vergessen (Platzhalter – Funktion wird später richtig umgesetzt)
def forgot_password_logic(email):
    # TODO: Hier kommt später Logik mit Token, Mailversand etc.
    return response(True, data="Diese Funktion ist noch in Arbeit.")

# User löschen (wird über ID angesprochen)
def delete_user_logic(user_id):
    result = delete_user_by_id(user_id)
    if not result:
        return response(False, error="User konnte nicht gelöscht werden oder existiert nicht.")
    return response(True, data="User erfolgreich gelöscht.")
