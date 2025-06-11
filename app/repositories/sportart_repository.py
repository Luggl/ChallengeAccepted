# app/repositories/sportart_repository.py

from app.database.models import Sportart
from app import db

def find_sportart_by_id(sportart_id):
    """Finde eine Sportart anhand der ID."""
    return db.session.query(Sportart).filter_by(sportart_id=sportart_id).first()

def find_sportart_by_bezeichnung(bezeichnung):
    """Finde eine Sportart anhand ihrer Bezeichnung (Name)."""
    return db.session.query(Sportart).filter_by(bezeichnung=bezeichnung).first()

def create_sportart(sportart):
    """Erstelle und speichere eine neue Sportart."""
    db.session.add(sportart)
    db.session.commit()
    return sportart

def delete_sportart_by_id(sportart_id):
    """Lösche eine Sportart anhand ihrer ID."""
    sportart = find_sportart_by_id(sportart_id)
    if sportart:
        db.session.delete(sportart)
        db.session.commit()
        return True
    return False

def update_sportart(sportart):
    """Aktualisiere eine bestehende Sportart."""
    db.session.commit()
    return sportart

def get_all_sportarten():
    """Gibt alle Sportarten zurück."""
    return db.session.query(Sportart).all()
