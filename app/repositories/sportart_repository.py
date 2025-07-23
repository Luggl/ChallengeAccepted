from app.database.models import Sportart, SportartIntervall
from app.database.database import SessionLocal


def find_sportart_by_id(sportart_id):
    """Finde eine Sportart anhand der ID."""
    with SessionLocal() as session:
        sportart = session.query(Sportart).filter_by(sportart_id=sportart_id).first()
        return sportart

def find_intervall_by_sportart_and_schwierigkeit(sportart_id, schwierigkeitsgrad):
    """Findet das Intervall zu einer Sportart und Schwierigkeitsgrad."""
    with SessionLocal() as session:
        return session.query(SportartIntervall).filter_by(
            sportart_id=sportart_id,
            schwierigkeitsgrad=schwierigkeitsgrad
        ).first()