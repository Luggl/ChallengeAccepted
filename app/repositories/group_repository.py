# app/repositories/group_repository.py

from app.database.models import Gruppe
from app import db
from database.database import SessionLocal
import uuid

def find_group_by_id(gruppe_id):
    """Finde eine Gruppe anhand der ID."""

    try:
        gruppe_uuid_bytes = uuid.UUID(gruppe_id).bytes      # Die übergebene Gruppe_id ist ein String - Muss in UUID geparsed werden
    except ValueError:
        return None  # ungültiges UUID-Format


    with SessionLocal() as session:
       gruppe = session.query(Gruppe).filter_by(gruppe_id=gruppe_uuid_bytes).first()
    return gruppe

def find_group_by_name(gruppenname):
    """Finde eine Gruppe anhand des Gruppennamens."""
    return db.session.query(Gruppe).filter_by(gruppenname=gruppenname).first()

def find_group_by_invite_code(einladungscode):
    """Finde eine Gruppe anhand des Einladungscodes."""
    with SessionLocal() as session:
        return session.query(Gruppe).filter_by(einladungscode=einladungscode).first()

def create_group(gruppe):
    """Erstelle und speichere eine neue Gruppe."""

    with SessionLocal() as session:
        session.add(gruppe)
        session.flush()
        session.commit()
        session.refresh(gruppe)
    return gruppe

def create_membership(membership):
    """Erstelle und speichere eine neue Membership."""
    with SessionLocal() as session:
        session.add(membership)
        session.commit()
    return membership

def delete_group_by_id(gruppe_id):
    """Lösche eine Gruppe anhand ihrer ID."""
    gruppe = find_group_by_id(gruppe_id)

    with SessionLocal() as session:
        if gruppe:
            session.delete(gruppe)
            session.commit()
            return True
        return False

def update_group(gruppe):
    """Aktualisiere eine bestehende Gruppe."""
    with SessionLocal() as session:
        session.merge(gruppe)
        session.commit()
    return gruppe

def get_all_groups():
    """Gibt alle Gruppen zurück."""
    return db.session.query(Gruppe).all()
