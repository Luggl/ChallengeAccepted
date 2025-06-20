from datetime import date

from app.database.models import Challenge, StandardChallengeSportart, Sportart, SurvivalChallengeSportart, \
    StandardChallenge, Survivalchallenge
from app import db
from app.database.database import SessionLocal


#def find_sportart_by_id(sportart_id):
#    """Finde eine Sportart anhand der ID (BLOB/UUID.bytes)."""
#    with SessionLocal() as session:
#       return session.query(Sportart).filter_by(sportart_id=sportart_id).first()



def find_sportart_by_id(sportart_id):
    with SessionLocal() as session:
        result = session.query(Sportart).filter_by(sportart_id=sportart_id).first()
        return result

def find_challenges_by_group(gruppe_id):
    """Finde alle Challenges einer Gruppe."""
    return db.session.query(Challenge).filter_by(gruppe_id=gruppe_id).all()

def find_challenges_by_creator(user_id, gruppe_id):
    """Finde alle Challenges, die von einem bestimmten Mitglied einer bestimmten Gruppe erstellt wurden."""
    return db.session.query(Challenge).filter_by(
        ersteller_user_id=user_id,
        ersteller_gruppe_id=gruppe_id
    ).all()

def find_all_survival_challenges():
    """Liefert alle Survival-Challenges, deren Startdatum heute oder früher ist."""
    with SessionLocal() as session:
        return session.query(Survivalchallenge).filter(
            Survivalchallenge.startdatum <= date.today()
        ).all()


def find_challenges_by_type(typ):
    """Finde alle Challenges eines bestimmten Typs (z.B. 'standard' oder 'survival')."""
    return db.session.query(Challenge).filter_by(typ=typ).all()

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

def update_challenge(challenge):
    """Aktualisiere eine bestehende Challenge."""
    db.session.commit()
    return challenge

def get_all_challenges():
    """Gibt alle Challenges zurück."""
    return db.session.query(Challenge).all()

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