import datetime
import uuid
import random

from flask_sqlalchemy.session import Session
from werkzeug.security import generate_password_hash
from app.database.database import SessionLocal
from app.database.models import User, Gruppe, Membership


def gen_uuid():
    return uuid.uuid4().bytes

def create_seed_data(db: Session):
    usernames = ["alice", "bob", "carla", "dave", "eva", "felix", "greta", "hans", "ines", "jonas"]
    users = []
    logins = []

    for name in usernames:
        password = f"{name}_pass123"
        hashed_pw = generate_password_hash(password)
        user = User(
            user_id=gen_uuid(),
            username=name,
            email=f"{name}@example.com",
            passwordHash=hashed_pw,
            profilbild_url=f"https://example.com/profiles/{name}.jpg",
            streak=random.randint(0, 10)
        )
        db.add(user)
        users.append(user)
        logins.append((name, password))

    gruppen = []
    for i in range(3):
        gruppe = Gruppe(
            gruppe_id=gen_uuid(),
            gruppenname=f"Gruppe {i + 1}",
            beschreibung=f"Beschreibung für Gruppe {i + 1}",
            gruppenbild="https://example.com/group.png",
            einladungscode=f"CODE{i + 1}",
            einladungscode_gueltig_bis=datetime.datetime.now() + datetime.timedelta(days=30),
            erstellungsDatum=datetime.date.today()
        )
        db.add(gruppe)
        gruppen.append(gruppe)

    memberships = []
    for user in users:
        gruppe = random.choice(gruppen)
        membership = Membership(
            user_id=user.user_id,
            gruppe_id=gruppe.gruppe_id,
            isAdmin=random.choice([True, False])
        )
        db.add(membership)
        memberships.append(membership)

    db.commit()

    print("\n✅ Datenbank erfolgreich mit Testdaten gefüllt.")
    print("🔐 Test-Login-Daten (Klartext):")
    print("-" * 40)
    for username, password in logins:
        print(f"👤 Benutzername: {username:<10} | 🔑 Passwort: {password}")
    print("-" * 40)

    # Direkt ausführbar
if __name__ == "__main__":
    db = SessionLocal()
    try:
        create_seed_data(db)
    finally:
        db.close()