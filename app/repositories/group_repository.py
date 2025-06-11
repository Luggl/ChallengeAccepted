# app/repositories/group_repository.py

from app.database.models import Gruppe
from app import db

def find_group_by_id(gruppe_id):
    """Finde eine Gruppe anhand der ID."""
    return db.session.query(Gruppe).filter_by(gruppe_id=gruppe_id).first()

def find_group_by_name(gruppenname):
    """Finde eine Gruppe anhand des Gruppennamens."""
    return db.session.query(Gruppe).filter_by(gruppenname=gruppenname).first()

def find_group_by_invite_code(einladungscode):
    """Finde eine Gruppe anhand des Einladungscodes."""
    return db.session.query(Gruppe).filter_by(einladungscode=einladungscode).first()

def create_group(gruppe):
    """Erstelle und speichere eine neue Gruppe."""
    db.session.add(gruppe)
    db.session.commit()
    return gruppe

def delete_group_by_id(gruppe_id):
    """Lösche eine Gruppe anhand ihrer ID."""
    gruppe = find_group_by_id(gruppe_id)
    if gruppe:
        db.session.delete(gruppe)
        db.session.commit()
        return True
    return False

def update_group(gruppe):
    """Aktualisiere eine bestehende Gruppe."""
    db.session.commit()
    return gruppe

def get_all_groups():
    """Gibt alle Gruppen zurück."""
    return db.session.query(Gruppe).all()
