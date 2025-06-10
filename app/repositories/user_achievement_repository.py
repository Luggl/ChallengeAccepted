# app/repositories/user_achievement_repository.py

from app.models.models import UserAchievement
from app import db

def find_user_achievement(user_id, achievement_id):
    """Finde die Verknüpfung User-Achievement anhand der beiden IDs."""
    return db.session.query(UserAchievement).filter_by(
        user_id=user_id,
        achievement_id=achievement_id
    ).first()

def find_achievements_by_user(user_id):
    """Finde alle Achievements eines Users."""
    return db.session.query(UserAchievement).filter_by(user_id=user_id).all()

def find_users_by_achievement(achievement_id):
    """Finde alle User, die ein bestimmtes Achievement haben."""
    return db.session.query(UserAchievement).filter_by(achievement_id=achievement_id).all()

def create_user_achievement(user_achievement):
    """Erstelle einen User-Achievement-Eintrag."""
    db.session.add(user_achievement)
    db.session.commit()
    return user_achievement

def delete_user_achievement(user_id, achievement_id):
    """Lösche einen User-Achievement-Eintrag."""
    user_achievement = find_user_achievement(user_id, achievement_id)
    if user_achievement:
        db.session.delete(user_achievement)
        db.session.commit()
        return True
    return False

def update_user_achievement(user_achievement):
    """Aktualisiere einen bestehenden User-Achievement-Eintrag."""
    db.session.commit()
    return user_achievement

def get_all_user_achievements():
    """Gibt alle User-Achievement-Einträge zurück."""
    return db.session.query(UserAchievement).all()
