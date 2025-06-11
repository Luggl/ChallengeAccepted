
from app.database.models import Challenge
from app import db

def find_challenge_by_id(challenge_id):
    """Finde eine Challenge anhand der ID."""
    return db.session.query(Challenge).filter_by(challenge_id=challenge_id).first()

def find_challenges_by_group(gruppe_id):
    """Finde alle Challenges einer Gruppe."""
    return db.session.query(Challenge).filter_by(gruppe_id=gruppe_id).all()

def find_challenges_by_creator(user_id, gruppe_id):
    """Finde alle Challenges, die von einem bestimmten Mitglied einer bestimmten Gruppe erstellt wurden."""
    return db.session.query(Challenge).filter_by(
        ersteller_user_id=user_id,
        ersteller_gruppe_id=gruppe_id
    ).all()

def find_challenges_by_type(typ):
    """Finde alle Challenges eines bestimmten Typs (z.B. 'standard' oder 'survival')."""
    return db.session.query(Challenge).filter_by(typ=typ).all()

def create_challenge(challenge):
    """Speichere eine neue Challenge."""
    db.session.add(challenge)
    db.session.commit()
    return challenge

def delete_challenge_by_id(challenge_id):
    """Lösche eine Challenge anhand der ID."""
    challenge = find_challenge_by_id(challenge_id)
    if challenge:
        db.session.delete(challenge)
        db.session.commit()
        return True
    return False

def update_challenge(challenge):
    """Aktualisiere eine bestehende Challenge."""
    db.session.commit()
    return challenge

def get_all_challenges():
    """Gibt alle Challenges zurück."""
    return db.session.query(Challenge).all()
