from sqlalchemy import func

from app.database.database import SessionLocal
from app.database.models import Aufgabe

def find_task_by_id(aufgabe_id):
    """Finde eine Aufgabe anhand der ID."""
    with SessionLocal() as session:
        return session.query(Aufgabe).filter_by(aufgabe_id=aufgabe_id).first()

def find_tasks_by_challenge_id(challenge_id):
    """Finde alle Aufgaben, die zu einer Challenge gehören."""
    with SessionLocal() as session:
        return session.query(Aufgabe).filter_by(challenge_id=challenge_id).all()

def find_task_by_challenge_and_date(challenge_id, datum):
    """Finde Aufgabe zu Challenge und Datum (Standard)."""
    with SessionLocal() as session:
        return session.query(Aufgabe).filter_by(challenge_id=challenge_id, datum=datum).first()


def find_task_by_challenge_and_date_and_typ(challenge_id, datum, typ):
    """Finde Aufgabe zu Challenge, Datum und Typ."""
    with SessionLocal() as session:
        return session.query(Aufgabe).filter_by(
            challenge_id=challenge_id,
            datum=datum,
            typ=typ
        ).first()

def save_aufgabe(aufgabe):
    with SessionLocal() as session:
        session.add(aufgabe)
        session.commit()
        session.refresh(aufgabe)  # wichtig!
        return aufgabe

def delete_task_by_id(aufgabe_id):
    """Lösche eine Aufgabe anhand ihrer ID."""
    with SessionLocal() as session:
        aufgabe = session.query(Aufgabe).filter_by(aufgabe_id=aufgabe_id).first()
        if aufgabe:
            session.delete(aufgabe)
            session.commit()
            return True
        return False

def get_all_tasks():
    """Gibt alle Aufgaben zurück."""
    with SessionLocal() as session:
        return session.query(Aufgabe).all()

def count_survival_tasks_for_challenge(challenge_id):
    """Zählt alle Survival-Aufgaben einer Challenge."""
    with SessionLocal() as session:
        return session.query(func.count(Aufgabe.aufgabe_id)).filter_by(
            challenge_id=challenge_id,
            typ="survival"
        ).scalar()

# def mark_task_as_complete(task_id, user_id):
#     with SessionLocal() as session:
