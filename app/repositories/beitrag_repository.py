from app.database.database import SessionLocal
from app.database.models import Beitrag


def find_beitrag_by_id(beitrag_id):
    with SessionLocal() as session:
        return session.query(Beitrag).filter(beitrag_id=beitrag_id).first()

def update_beitrag(beitrag_id, user_id, beschreibung):
    with SessionLocal() as session:
        beitrag = find_beitrag_by_id(beitrag_id)
        beitrag.beschreibung = beschreibung
        session.merge(beitrag)
        session.commit()
        return beitrag