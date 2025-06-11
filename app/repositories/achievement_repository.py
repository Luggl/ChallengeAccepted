# app/repositories/achievement_repository.py

from app.database.models import Achievement
from app import db

def find_achievement_by_id(achievement_id):
    """Finde ein Achievement anhand der ID."""
    return db.session.query(Achievement).filter_by(achievement_id=achievement_id).first()

def find_achievement_by_title(titel):
    """Finde ein Achievement anhand des Titels."""
    return db.session.query(Achievement).filter_by(titel=titel).first()

def create_achievement(achievement):
    """Erstelle und speichere ein neues Achievement."""
    db.session.add(achievement)
    db.session.commit()
    return achievement

def delete_achievement_by_id(achievement_id):
    """Lösche ein Achievement anhand der ID."""
    achievement = find_achievement_by_id(achievement_id)
    if achievement:
        db.session.delete(achievement)
        db.session.commit()
        return True
    return False

def update_achievement(achievement):
    """Aktualisiere ein bestehendes Achievement."""
    db.session.commit()
    return achievement

def get_all_achievements():
    """Gibt alle Achievements zurück."""
    return db.session.query(Achievement).all()
