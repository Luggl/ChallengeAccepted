from app.database.database import SessionLocal
from app.database.models import BeitragVotes, Beitrag, Gruppe, Aufgabenerfuellung


def find_beitrag_by_id(beitrag_id):
    with SessionLocal() as session:
        return session.query(Beitrag).filter(Beitrag.beitrag_id==beitrag_id).first()

def find_gruppe_by_beitrag(beitrag):
    with SessionLocal() as session:
        erfuellung = session.query(Aufgabenerfuellung).filter_by(erfuellung_id=beitrag.erfuellung_id).first()
        if not erfuellung:
            return None
        gruppe = session.query(Gruppe).filter_by(gruppe_id=erfuellung.gruppe_id).first()
        return gruppe

def find_beitrag_vote_by_user_beitrag(user_id, beitrag_id):
    with SessionLocal() as session:
        return session.query(BeitragVotes).filter(BeitragVotes.beitrag_id==beitrag_id,
                                                  BeitragVotes.user_id==user_id).first()

def update_beitrag(beitrag_id, beschreibung):
    with SessionLocal() as session:
        beitrag = find_beitrag_by_id(beitrag_id)
        beitrag.beschreibung = beschreibung
        session.merge(beitrag)
        session.commit()
        return beitrag

def find_beitrag_by_erfuellung_id(erfuellung_id):
    with SessionLocal() as session:
        return session.query(Beitrag).filter(Beitrag.erfuellung_id == erfuellung_id).first()

def create_beitrag(beitrag):
    with SessionLocal() as session:
        session.add(beitrag)
        session.commit()
        return beitrag

def is_user_beitrag_ersteller(user_id, beitrag_id):
    with SessionLocal() as session:
        beitrag = session.query(Beitrag).get(beitrag_id)
        aufgabenerfuellung = session.query(Aufgabenerfuellung).get(beitrag.erfuellung_id)
        if aufgabenerfuellung.user_id == user_id:
            return True
        else:
            return False