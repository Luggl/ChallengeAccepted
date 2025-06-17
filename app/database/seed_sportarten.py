from app import create_app, db
from app.database.models import Sportart
from app.repositories.sportart_repository import create_sportart
import uuid

def seed_sportarten():
    sportarten = [
        ("Liegestütze", "anzahl"),
        ("Seilspringen", "dauer"),
        ("Situps", "anzahl"),
        ("Burpees", "anzahl"),
    ]

    for name, unit in sportarten:
        s = Sportart(
            sportart_id=uuid.uuid4().bytes,
            bezeichnung=name,
            unit=unit
        )
        create_sportart(s)

print("Sportarten erfolgreich angelegt.")
if __name__ == "__main__":
    app = create_app()  # falls du eine Factory-Methode verwendest
    with app.app_context():
        seed_sportarten()