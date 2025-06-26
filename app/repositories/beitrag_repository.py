from app.database.database import SessionLocal
from app.database.models import BeitragVotes, Beitrag


def find_beitrag_by_id(beitrag_id):
    with SessionLocal() as session:
        return session.query(Beitrag).filter(beitrag_id=beitrag_id).first()

def find_gruppe_by_beitrag(beitrag):
    return beitrag.gruppe_id

def find_beitrag_vote_by_user_beitrag(user_id, beitrag_id):
    with SessionLocal() as session:
        return session.query(BeitragVotes).filter(beitrag_id=beitrag_id,
                                                  user_id=user_id).first()

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
