# app/repositories/user_repository.py

from app.database.models import User, Aufgabenerfuellung, Beitrag

from app.database.database import SessionLocal
from utils.serialize import serialize_beitrag


def find_user_by_email(email):
    """Finde einen User anhand seiner E-Mail."""
    with SessionLocal() as session:
        return session.query(User).filter_by(email=email).first()

def find_user_by_username(username):
    with SessionLocal() as session:
        return session.query(User).filter_by(username=username).first()

def find_user_by_id(user_id):
    """Finde einen User anhand seiner UUID (user_id)."""
    with SessionLocal() as session:
        return session.query(User).filter_by(user_id=user_id).first()

def save_user(user):
    """Speichere einen neuen User in die Datenbank."""
    with SessionLocal() as session:
        session.add(user)
        session.commit()
        session.refresh(user)
    return user

def delete_user_by_id(user_id):
    """Lösche einen User anhand seiner user_id."""
    user = find_user_by_id(user_id)
    if user:
        with SessionLocal() as session:
            session.delete(user)
            session.commit()
        return True
    return False

def update_user(user):
    """Aktualisiere einen existierenden User (alle Felder, die geändert wurden)."""
    with SessionLocal() as session:
        session.merge(user)
        session.commit()
    return user

def find_user_activities(user):
    with SessionLocal() as session:
        return session.query(Aufgabenerfuellung).filter_by(user_id=user.user_id).first()

def get_user_feed(user_id):
    with SessionLocal() as session:
        beitraege = session.query(Beitrag).filter_by(user_id=user_id).order_by(Beitrag.erstellDatum.desc()).all()
        result = [serialize_beitrag(b) for b in beitraege]
        return result