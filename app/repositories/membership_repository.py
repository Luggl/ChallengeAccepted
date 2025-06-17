# app/repositories/membership_repository.py

from app.database.models import Membership
from app import db
from app.database.database import SessionLocal

def find_membership(user_id, gruppe_id):
    """Finde die Mitgliedschaft eines Users in einer Gruppe."""
    with SessionLocal() as session:
        membership = session.query(Membership).filter_by(user_id=user_id, gruppe_id=gruppe_id).first()
    return membership

def find_memberships_by_user(user_id):
    """Finde alle Gruppen-Mitgliedschaften eines Users."""
    return db.session.query(Membership).filter_by(user_id=user_id).all()

def find_memberships_by_group(gruppe_id):
    """Finde alle Mitglieder einer Gruppe."""
    return db.session.query(Membership).filter_by(gruppe_id=gruppe_id).all()

def create_membership(membership):
    """Füge eine Mitgliedschaft hinzu."""
    db.session.add(membership)
    db.session.commit()
    return membership

def delete_membership(user_id, gruppe_id):
    """Lösche eine Mitgliedschaft anhand von User- und Gruppen-ID."""
    membership = find_membership(user_id, gruppe_id)
    if membership:
        db.session.delete(membership)
        db.session.commit()
        return True
    return False

def update_membership(membership):
    """Aktualisiere eine bestehende Mitgliedschaft."""
    db.session.commit()
    return membership

def get_all_memberships():
    """Gibt alle Mitgliedschaften zurück."""
    return db.session.query(Membership).all()
