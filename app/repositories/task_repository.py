# app/repositories/task_repository.py

from app.models.models import Aufgabe  # falls Aufgabe im __init__.py ist, sonst von app.models.aufgabe import Aufgabe
from app import db

def find_task_by_id(aufgabe_id):
    """Finde eine Aufgabe anhand der ID."""
    return db.session.query(Aufgabe).filter_by(aufgabe_id=aufgabe_id).first()

def find_tasks_by_challenge(challenge_id):
    """Finde alle Aufgaben, die zu einer Challenge gehören."""
    return db.session.query(Aufgabe).filter_by(challenge_id=challenge_id).all()

def create_task(aufgabe):
    """Erstelle und speichere eine neue Aufgabe."""
    db.session.add(aufgabe)
    db.session.commit()
    return aufgabe

def delete_task_by_id(aufgabe_id):
    """Lösche eine Aufgabe anhand ihrer ID."""
    aufgabe = find_task_by_id(aufgabe_id)
    if aufgabe:
        db.session.delete(aufgabe)
        db.session.commit()
        return True
    return False

def update_task(aufgabe):
    """Aktualisiere eine bestehende Aufgabe."""
    db.session.commit()
    return aufgabe

def get_all_tasks():
    """Gibt alle Aufgaben zurück."""
    return db.session.query(Aufgabe).all()
#Kommentar