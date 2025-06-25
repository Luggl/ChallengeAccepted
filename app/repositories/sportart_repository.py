# app/repositories/sportart_repository.py

from app.database.models import Sportart, SportartIntervall
from app import db
from app.database.database import SessionLocal


def find_sportart_by_id(sportart_id):
    """Finde eine Sportart anhand der ID."""

    with SessionLocal() as session:
        sportart = session.query(Sportart).filter_by(sportart_id=sportart_id).first()
        return sportart

def find_sportart_by_bezeichnung(bezeichnung):
    """Finde eine Sportart anhand ihrer Bezeichnung (Name)."""
    with SessionLocal() as session:
        return session.query(Sportart).filter_by(bezeichnung=bezeichnung).first()


def find_intervall_by_sportart_and_schwierigkeit(sportart_id, schwierigkeitsgrad):
    """Findet das Intervall zu einer Sportart und Schwierigkeitsgrad."""
    with SessionLocal() as session:
        return session.query(SportartIntervall).filter_by(
            sportart_id=sportart_id,
            schwierigkeitsgrad=schwierigkeitsgrad
        ).first()

def create_sportart(sportart):
    """Erstelle und speichere eine neue Sportart."""
    with SessionLocal() as session:
        session.add(sportart)
        session.commit()
    return sportart

def delete_sportart_by_id(sportart_id):
    """Lösche eine Sportart anhand ihrer ID."""
    with SessionLocal() as session:
        sportart = find_sportart_by_id(sportart_id)
        if sportart:
            session.delete(sportart)
            session.commit()
            return True
        return False

def update_sportart(sportart):
    """Aktualisiere eine bestehende Sportart."""
    db.session.commit()
    return sportart

def get_all_sportarten():
    """Gibt alle Sportarten zurück."""
    return db.session.query(Sportart).all()
