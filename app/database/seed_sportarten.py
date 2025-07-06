import json
import uuid
from app import create_app
from app.database.database import SessionLocal
from app.database.models import Sportart, SportartIntervall, StatusUnit, Schwierigkeit

def seed_sportarten():
    with open(r"/root/ChallengeAccepted/alle_sportarten_mit_faktor.json", "r", encoding="utf-8") as f:
        daten = json.load(f)

    with SessionLocal() as session:
        for eintrag in daten:
            sportart = Sportart(
                sportart_id=uuid.uuid4().bytes,
                bezeichnung=eintrag["bezeichnung"],
                unit=StatusUnit(eintrag["unit"]),
                steigerungsfaktor=eintrag.get("steigerungsfaktor", 1.0)
            )
            session.add(sportart)
            session.flush()  # sportart_id für Intervall speichern

            for schwierigkeit, (min_wert, max_wert) in eintrag["intervalle"].items():
                intervall = SportartIntervall(
                    sportart_id=sportart.sportart_id,
                    schwierigkeitsgrad=Schwierigkeit[schwierigkeit],
                    min_wert=min_wert,
                    max_wert=max_wert
                )
                session.add(intervall)

        session.commit()
        print("✅ Alle Sportarten + Intervalle + Steigerungsfaktor erfolgreich gespeichert.")

if __name__ == "__main__":
    app = create_app()
    with app.app_context():
        seed_sportarten()
