from datetime import date
from sqlalchemy.orm import joinedload
from app.database.models import Challenge, StandardChallengeSportart, Sportart, SurvivalChallengeSportart, \
    StandardChallenge, Survivalchallenge
from app.database.database import SessionLocal
from app.database.models import ChallengeParticipation

def find_active_challenge_by_group(gruppe_id):
    with SessionLocal() as session:
        return session.query(Challenge).filter(Challenge.gruppe_id==gruppe_id,
                                               Challenge.active==True).first()


def find_all_survival_challenges():
    with SessionLocal() as session:
        return session.query(Survivalchallenge).options(
            joinedload(Survivalchallenge.sportarten_links)
        ).filter(
            Survivalchallenge.startdatum <= date.today()
        ).all()


def find_challenge_by_id(challenge_id):
    """Finde eine Challenge anhand der ID."""
    with SessionLocal() as session:
        return session.query(Challenge).filter_by(challenge_id=challenge_id).first()

def find_standard_challenge_by_id(challenge_id):
    """Finde eine Standard-Challenge anhand der ID."""
    with SessionLocal() as session:
        return session.query(StandardChallenge).filter_by(challenge_id=challenge_id).first()


def find_survival_challenge_by_id(challenge_id):
    """Finde eine Standard-Challenge anhand der ID."""
    with SessionLocal() as session:
        return session.query(Survivalchallenge).filter_by(challenge_id=challenge_id).first()


def find_standard_challenge_sportarten_by_challenge_id(challenge_id):
    """Finde alle Sportarten einer Standard-Challenge anhand der Challenge-ID."""
    with SessionLocal() as session:
        return session.query(StandardChallengeSportart).filter_by(challenge_id=challenge_id).all()

def create_challenge(challenge: Challenge):
    """Speichere eine neue Challenge in der Datenbank."""
    with SessionLocal() as session:
        session.add(challenge)
        session.commit()
        session.refresh(challenge)
        return challenge

def delete_challenge_by_id(challenge_id):
    """Lösche eine Challenge anhand der ID (UUID.bytes)."""
    with SessionLocal() as session:
        challenge = session.query(Challenge).filter_by(challenge_id=challenge_id).first()
        if challenge:
            session.delete(challenge)
            session.commit()
            return True
        return False


def save_standard_challenge_sportart(sportart_link: StandardChallengeSportart):
    """Speichert einen Eintrag in der standard_challenge_sportart-Tabelle."""
    with SessionLocal() as session:
        session.add(sportart_link)
        session.commit()
        session.refresh(sportart_link)

def save_survival_challenge_sportart(sportart_link: SurvivalChallengeSportart):
    """Speichert einen Eintrag in der survival_challenge_sportart-Tabelle."""
    with SessionLocal() as session:
        session.add(sportart_link)
        session.commit()
        session.refresh(sportart_link)


def is_user_allowed_to_delete(challenge_id, user_id):
    """Prüft, ob der User der Ersteller der Challenge ist (Löschberechtigung)."""
    with SessionLocal() as session:
        challenge = session.query(Challenge)\
            .filter_by(challenge_id=challenge_id, ersteller_user_id=user_id)\
            .first()
        return challenge is not None


def find_teilnehmer_and_user_by_challenge(challenge_id):
    with SessionLocal() as session:
        return session.query(ChallengeParticipation).options(
            joinedload(ChallengeParticipation.user)
        ).filter(ChallengeParticipation.challenge_id == challenge_id).all()

def get_dead_teilnehmer(challenge_id):
    with SessionLocal() as session:
        return session.query(ChallengeParticipation).filter(ChallengeParticipation.challenge_id == challenge_id,
                                                            ChallengeParticipation.aktiv == False).all()

def save_challenge_participation(challenge_participation):
    with SessionLocal() as session:
        session.add(challenge_participation)
        session.commit()
        session.refresh(challenge_participation)
