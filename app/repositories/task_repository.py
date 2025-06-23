from sqlalchemy import func
from sqlalchemy.orm import joinedload

from app.database.database import SessionLocal
from app.database.models import Aufgabe, AufgabeStatus, Aufgabenerfuellung, StandardAufgabe, SurvivalAufgabe
from repositories.challenge_repository import find_challenge_by_id
from repositories.membership_repository import find_memberships_by_group


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

def find_tasks_by_user_id(user_id):
    """Finde Aufgaben anhand der User ID"""
    with SessionLocal() as session:
        return session.query(Aufgabenerfuellung).filter_by(user_id=user_id).all()



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

        # Alle User zur Challenge holen
        memberships = find_memberships_by_group(find_challenge_by_id(aufgabe.challenge_id).gruppe_id)

        # Für jeden User eine Aufgabenerfüllung anlegen
        for user in memberships:
            erfuellung = Aufgabenerfuellung(
                aufgabe_id=aufgabe.aufgabe_id,
                user_id=user.user_id,
                gruppe_id=user.gruppe_id,
                status="offen"
            )
            session.add(erfuellung)

        session.commit()
        session.flush()
        return aufgabe.aufgabe_id

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


def mark_task_as_complete(aufgabenerfuellung_id):
    with SessionLocal() as session:
        aufgabenerfuellung = session.query(Aufgabenerfuellung).filter_by(aufgabenerfuellung_id=aufgabenerfuellung_id).first()
        aufgabenerfuellung.status=AufgabeStatus.abgeschlossen
        session.commit()
        return aufgabenerfuellung

def find_aufgabenerfuellung_by_challenge_and_date(challenge_id, date):
    with SessionLocal() as session:
        return session.query(Aufgabenerfuellung).options(joinedload(Aufgabenerfuellung.aufgabe)).join(Aufgabe, Aufgabenerfuellung.aufgabe_id == Aufgabe.aufgabe_id).filter(
            Aufgabe.challenge_id == challenge_id,
            Aufgabe.datum == date
        ).first()
