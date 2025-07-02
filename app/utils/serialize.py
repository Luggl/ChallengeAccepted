# um Videoformat per JSON über Flask zurückzugeben
import uuid

from app.utils.auth_utils import get_uuid_formated_string


def serialize_beitrag(beitrag, user_id):
    user_vote = None
    for vote in beitrag.votes:
        if vote.user_id == user_id:
            user_vote = vote.vote.value
            break

    return{
        "beitrag_id": get_uuid_formated_string(beitrag.beitrag_id),
        "aufgabe_sportart": beitrag.erfuellung.aufgabe.sportart.bezeichnung,
        "aufgabe_anzahl": beitrag.erfuellung.aufgabe.zielwert,
        "aufgabe_unit": beitrag.erfuellung.aufgabe.unit.value,
        "beschreibung": beitrag.erfuellung.beschreibung if beitrag.erfuellung.beschreibung else None,
        "erstellt_am": beitrag.erfuellung.erfuellungsdatum.isoformat() if beitrag.erfuellung.erfuellungsdatum else None,
        "user_id": get_uuid_formated_string(beitrag.erfuellung.user_id),
        "gruppe_id": get_uuid_formated_string(beitrag.erfuellung.gruppe_id),
        "video_url": f"/media/{beitrag.erfuellung.video_url}" if beitrag.erfuellung.video_url else None,
        "user_vote": user_vote
    }

def serialize_gruppe(gruppe):
    return{
        "gruppe_id": get_uuid_formated_string(gruppe.gruppe_id),
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
        "user_id": get_uuid_formated_string(membership.user_id),
        "gruppe_id": membership.gruppe_id.hex(),
        "isAdmin": membership.isAdmin,
    }

def serialize_user(user):
    return{
        "user_id": get_uuid_formated_string(user.user_id),
        "username": user.username,
        "email": user.email
    }