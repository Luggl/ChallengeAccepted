import json
import uuid
from app import create_app
from app.database.database import SessionLocal
from app.database.models import Sportart, SportartIntervall, StatusUnit, Schwierigkeit, Geschlecht

def seed_sportarten():
    with open("C:/Users/TS10/PycharmProjects/gruppe-14---challenge-accepted/alle_sportarten_intervall.json", "r", encoding="utf-8") as f:
        daten = json.load(f)

    with SessionLocal() as session:
        for eintrag in daten:
            sportart = Sportart(
                sportart_id=uuid.uuid4().bytes,
                bezeichnung=eintrag["bezeichnung"],
                unit=StatusUnit(eintrag["unit"])
            )
            session.add(sportart)
            session.flush()  # um sportart_id zu bekommen

            for geschl, stufen in eintrag["intervalle"].items():
                for schwierigkeit, (min_wert, max_wert) in stufen.items():
                    intervall = SportartIntervall(
                        sportart_id=sportart.sportart_id,
                        geschlecht=Geschlecht[geschl],
                        schwierigkeitsgrad=Schwierigkeit[schwierigkeit],
                        min_wert=min_wert,
                        max_wert=max_wert
                    )
                    session.add(intervall)

        session.commit()
        print("✅ Alle Sportarten + Intervalle erfolgreich gespeichert.")

if __name__ == "__main__":
    app = create_app()
    with app.app_context():
        seed_sportarten()
