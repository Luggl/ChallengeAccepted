# app/repositories/group_repository.py

from app.database.models import Gruppe, Beitrag, Aufgabenerfuellung
from app.database.database import SessionLocal
from utils.serialize import serialize_beitrag


def find_group_by_id(gruppe_id):
    """Finde eine Gruppe anhand der ID."""
    with SessionLocal() as session:
        gruppe = session.query(Gruppe).filter_by(gruppe_id=gruppe_id).first()
    return gruppe


def find_group_by_invite_code(einladungscode):
    """Finde eine Gruppe anhand des Einladungscodes."""
    with SessionLocal() as session:
        return session.query(Gruppe).filter_by(einladungscode=einladungscode).first()

def create_group(gruppe):
    """Erstelle und speichere eine neue Gruppe."""
    with SessionLocal() as session:
        session.add(gruppe)
        session.flush()
        session.commit()
        session.refresh(gruppe)
    return gruppe


def delete_group_by_id(gruppe_id):
    """Lösche eine Gruppe anhand ihrer ID."""
    gruppe = find_group_by_id(gruppe_id)
    if gruppe:
        with SessionLocal() as session:
            session.delete(gruppe)
            session.commit()
            return True
    return False

def update_group(gruppe):
    """Aktualisiere eine bestehende Gruppe."""
    with SessionLocal() as session:
        session.merge(gruppe)
        session.commit()
        return gruppe


def get_group_feed_by_group_id(group_id):
    with (SessionLocal() as session):
        #Beiträge inkl Aufgabenerfüllung holen, damit serialize_beitrag die benötigten Werte erhält
        # beitraege_mit_erfuellung = session.query(Beitrag, Aufgabenerfuellung)\
        #                             .join(Aufgabenerfuellung, Beitrag.erfuellung_id == Aufgabenerfuellung.erfuellung_id)\
        #                             .filter(Aufgabenerfuellung.gruppe_id == group_id)\
        #                             .order_by(Beitrag.erstellDatum.des())\
        #                             .all()
        #
        # result = [serialize_beitrag(beitrag, erfuellung) for beitrag, erfuellung in beitraege_mit_erfuellung]
        # return result

        beitraege = session.query(Beitrag)\
                    .join(Beitrag.erfuellung)\
                    .filter(Aufgabenerfuellung.gruppe_id == group_id)\
                    .order_by(Beitrag.erstellDatum.desc())\
                    .all()

        result = [serialize_beitrag(b) for b in beitraege]
        return result

def get_groups_by_user_id(user_id):
    with SessionLocal() as session:
        return session.query(Gruppe).filter_by(user_id=user_id).all()
