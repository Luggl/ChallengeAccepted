# app/repositories/user_repository.py

from app.database.models import User  # falls User direkt in models/__init__.py steht, sonst: from app.models.user import User
from app import db  # Das ist die SQLAlchemy-Instanz (db.session)

def find_user_by_email(email):
    """Finde einen User anhand seiner E-Mail."""
    return db.session.query(User).filter_by(email=email).first()

def find_user_by_username(username):
    """Finde einen User anhand seines Usernames."""
    return db.session.query(User).filter_by(username=username).first()

def find_user_by_id(user_id):
    """Finde einen User anhand seiner UUID (user_id)."""
    return db.session.query(User).filter_by(user_id=user_id).first()

def save_user(user):
    """Speichere einen neuen User in die Datenbank."""
    db.session.add(user)
    db.session.commit()
    return user

def delete_user_by_id(user_id):
    """Lösche einen User anhand seiner user_id."""
    user = find_user_by_id(user_id)
    if user:
        db.session.delete(user)
        db.session.commit()
        return True
    return False

def update_user(user):
    """Aktualisiere einen existierenden User (alle Felder, die geändert wurden)."""
    db.session.commit()
    return user
