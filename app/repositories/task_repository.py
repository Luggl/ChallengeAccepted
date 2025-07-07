from sqlalchemy.orm import joinedload
from app.database.database import SessionLocal
from app.database.models import Aufgabe, AufgabeStatus, Aufgabenerfuellung, User, Beitrag, BeitragVotes
from app.repositories.beitrag_repository import find_beitrag_vote_by_user_beitrag
from app.repositories.challenge_repository import find_challenge_by_id
from app.repositories.membership_repository import find_memberships_by_group
from app.utils.time import date_today


def find_tasks_by_challenge_id(challenge_id):
    """Finde alle Aufgaben, die zu einer Challenge gehören."""
    with SessionLocal() as session:
        return session.query(Aufgabe).filter_by(challenge_id=challenge_id).all()

def find_task_by_challenge_and_date(challenge_id, datum):
    """Finde Aufgabe zu Challenge und Datum (Standard)."""
    with SessionLocal() as session:
        return session.query(Aufgabe).filter_by(challenge_id=challenge_id, datum=datum).first()


def find_aufgabenerfuellung_by_user_id(user_id):
    """Finde Aufgabenerfüllung anhand der User ID"""
    with SessionLocal() as session:
        return session.query(Aufgabenerfuellung).filter_by(user_id=user_id).all()

def find_aufgabenerfuellung_by_aufgabe_and_user_and_group(aufgabe_id, user_id, gruppe_id):
    with SessionLocal() as session:
        aufgabenerfuellug = session.query(Aufgabenerfuellung).filter_by(aufgabe_id=aufgabe_id, user_id=user_id, gruppe_id=gruppe_id).first()
        if not aufgabenerfuellug:
            return None
        return aufgabenerfuellug


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


def update_task_by_video_url(erfuellung_id, videopath):
    with SessionLocal() as session:
        aufgabenerfuellung = session.query(Aufgabenerfuellung).filter_by(erfuellung_id=erfuellung_id).first()
        aufgabenerfuellung.video_url = videopath
        session.commit()
        if not aufgabenerfuellung:
            return None
        return aufgabenerfuellung

def mark_task_as_complete(erfuellung_id, description):
    with SessionLocal() as session:
        aufgabenerfuellung = session.query(Aufgabenerfuellung).filter_by(erfuellung_id=erfuellung_id).first()
        aufgabenerfuellung.status=AufgabeStatus.abgeschlossen
        aufgabenerfuellung.erfuellungsdatum=date_today()
        if description:
            aufgabenerfuellung.beschreibung=description
        session.commit()
        if not aufgabenerfuellung:
            return None
        return aufgabenerfuellung

def find_aufgabenerfuellung_by_challenge_and_date_and_user(challenge_id, date, user_id):
    with SessionLocal() as session:
        return session.query(Aufgabenerfuellung).options(joinedload(Aufgabenerfuellung.aufgabe)).join(Aufgabe,
                                                                                                      Aufgabenerfuellung.aufgabe_id == Aufgabe.aufgabe_id).filter(
            Aufgabe.challenge_id == challenge_id,
            Aufgabe.datum == date,
            Aufgabenerfuellung.user_id == user_id
        ).first()

def has_user_already_voted(user_id, beitrag_id):
    beitrag_vote =  find_beitrag_vote_by_user_beitrag(user_id, beitrag_id)
    if beitrag_vote:
        return True
    return False

def create_user_vote(beitrag_votes):
    try:
        with SessionLocal() as session:
            session.add(beitrag_votes)
            session.flush()
            session.commit()
            return True
    except Exception:
        return False

def add_streak(user_id):
    with SessionLocal() as session:
        user = session.query(User).filter(User.user_id == user_id).first()
        if user is None:
            return None
        user.streak += 1
        session.commit()
        return user

def delete_streak(user_id):
    with SessionLocal() as session:
        user = session.query(User).filter(User.user_id == user_id).first()
        if user is None:
            return None
        user.streak = 0
        session.commit()
        return user



def handle_abgelaufene_aufgabe(aufgabe_id):
    with SessionLocal() as session:
        aufgabe = session.query(Aufgabe).get(aufgabe_id)
        if not aufgabe:
            return

        erfuellungen = session.query(Aufgabenerfuellung).filter_by(aufgabe_id=aufgabe_id).all()

        for erfuellung in erfuellungen:
            # Falls die Aufgabe nicht abgeschlossen wurde, Stats auf "nicht gemacht" und Streak zurücksetzen
            if erfuellung.status != AufgabeStatus.abgeschlossen:
                erfuellung.status = AufgabeStatus.nicht_gemacht
                delete_streak(erfuellung.user_id)
                continue

            beitrag = session.query(Beitrag).filter_by(erfuellung_id=erfuellung.erfuellung_id).first()
            if not beitrag:
                continue # Kein Beitrag = Keine Votes = keinen Status zu verändern

            #alle votes zum Beitrag holen
            votes= session.query(BeitragVotes).filter_by(beitrag_id=beitrag.beitrag_id).all()
            accepted = sum(1 for v in votes if v.vote == "accepted")
            rejected = sum(1 for v in votes if v.vote == "rejected")

            # Entscheidung der Mehrheit
            if rejected > accepted:
                erfuellung.status = AufgabeStatus.nicht_gemacht     #Hier werden zukünftig weitere Stats eingeführt wie "abgelehnt" "bestätigt" etc.
                delete_streak(erfuellung.user_id)


        session.commit()

def update_task_by_thumbnail(erfuellung_id, thumbnail_path):
    with SessionLocal() as session:
        erfuellung = session.query(Aufgabenerfuellung).get(erfuellung_id)
        if not erfuellung:
            return False
        erfuellung.thumbnail_path = thumbnail_path
        session.commit()
        session.refresh(erfuellung)
        return True