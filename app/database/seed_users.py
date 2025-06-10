from database.database import SessionLocal
#from database.database import SessionLocal
import database
from database.models import User
import uuid
from werkzeug.security import generate_password_hash

# Datenbank-Session starten
session = SessionLocal()

# Neuen User erstellen
user = User(
    user_id=uuid.uuid4().bytes,  # Wichtig: du nutzt UUID als BLOB
    username="tobi",
    email="tobi@test.de",
    passwordHash=generate_password_hash("123456")
)

# Speichern
session.add(user)
session.commit()
print("✅ Benutzer 'tobi' wurde erfolgreich eingefügt.")
