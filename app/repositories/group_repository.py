# app/repositories/group_repository.py

from app.database.models import Gruppe, Beitrag
from app import db
from app.database.database import SessionLocal
from repositories.membership_repository import find_memberships_by_user
from utils.serialize import serialize_beitrag


def find_group_by_id(gruppe_id):
    """Finde eine Gruppe anhand der ID."""
    with SessionLocal() as session:
        gruppe = session.query(Gruppe).filter_by(gruppe_id=gruppe_id).first()
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
        session.flush()
        session.commit()
    return membership

def delete_group_by_id(gruppe_id):
    """Lösche eine Gruppe anhand ihrer ID."""
    gruppe = find_group_by_id(gruppe_id)
    if gruppe:
        with SessionLocal() as session:
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

def get_group_feed_by_group_id(group_id):
    with SessionLocal() as session:
        beitraege = session.query(Beitrag).filter_by(gruppe_id=group_id).order_by(Beitrag.erstellDatum.desc()).all()
        result = [serialize_beitrag(b) for b in beitraege]
        return result

def get_groups_by_user_id(user_id):
    with SessionLocal() as session:
        memberships = find_memberships_by_user(user_id)
        return session.query(Gruppe).filter_by(user_id=user_id).all()
