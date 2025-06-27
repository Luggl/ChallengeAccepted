# um Videoformat per JSON über Flask zurückzugeben
import uuid

from repositories.task_repository import find_aufgabenerfuellung_by_id
from utils.auth_utils import get_uuid_formated_string


def serialize_beitrag(beitrag):
    #Da die Aufgabenerfüllung alle relevanten Informationen hält, muss diese hier erstmal geladen werden
    erfuellung = find_aufgabenerfuellung_by_id(beitrag.erfuellung.id)

    return{
        "beitrag_id": get_uuid_formated_string(beitrag.beitrag_id),
        "beschreibung": beitrag.erfuellung.beschreibung if beitrag.erfuellung.beschreibung else None,
        "erstellt_am": beitrag.erfuellung.datum.isoformat(),
        "user_id": get_uuid_formated_string(beitrag.erfuellung.user_id),
        "gruppe_id": get_uuid_formated_string(beitrag.erfuellung.gruppe_id),
        "video_url": f"/media/{beitrag.erfuellung.video_path}" if beitrag.video_path else None
    }

def serialize_gruppe(gruppe):
    return{
        "gruppe_id": gruppe.gruppe_id.hex(),
        "gruppenname": gruppe.gruppenname,
        "beschreibung": gruppe.beschreibung,
        "gruppenbild": gruppe.gruppenbild,
        "erstellt_am": gruppe.erstellungsDatum.isoformat(),
    }

def serialize_aufgabenerfuellung(aufgabenerfuellung):
    return{
        "aufgabe_id": get_uuid_formated_string(aufgabenerfuellung.aufgabe_id),
        "user_id": get_uuid_formated_string(aufgabenerfuellung.user_id),
        "gruppe_id": get_uuid_formated_string(aufgabenerfuellung.gruppe_id),
        "status": aufgabenerfuellung.status.name,
        "beschreibung": aufgabenerfuellung.aufgabe.beschreibung,
        "zielwert": aufgabenerfuellung.aufgabe.zielwert,
        "unit": aufgabenerfuellung.aufgabe.unit.name,
        "dauer": aufgabenerfuellung.aufgabe.dauer,
        "deadline": aufgabenerfuellung.aufgabe.deadline,
        "datum": aufgabenerfuellung.aufgabe.datum,
    }

def serialize_achievements(achievement):
    return{
        "achievement_id": achievement.achievement_id.hex(),
        "kategorie": achievement.kategorie.name,
        "stufe": achievement.stufe.name,
        "beschreibung": achievement.beschreibung,
        "condition_type": achievement.condition_type,
        "condition_value": achievement.condition_value,
    }

def serialize_membership(membership):
    return{
        "user_id": membership.user_id.hex(),
        "gruppe_id": membership.gruppe_id.hex(),
        "isAdmin": membership.isAdmin,
    }

def serialize_user(user):
    return{
        "user_id": user.user_id.hex(),
        "username": user.username,
        "email": user.email
    }