from sqlalchemy.orm import joinedload
from app.database.models import User, Aufgabenerfuellung, Beitrag, Membership, Aufgabe
from app.database.database import SessionLocal
from app.utils.serialize import serialize_beitrag


def find_user_by_email(email):
    """Finde einen User anhand seiner E-Mail."""
    with SessionLocal() as session:
        return session.query(User).filter_by(email=email).first()

def find_user_by_username(username):
    with SessionLocal() as session:
        return session.query(User).filter_by(username=username).first()

def find_user_by_id(user_id):
    """Finde einen User anhand seiner UUID (user_id)."""
    with SessionLocal() as session:
        return session.query(User).filter_by(user_id=user_id).first()

def save_user(user):
    """Speichere einen neuen User in die Datenbank."""
    with SessionLocal() as session:
        session.add(user)
        session.commit()
        session.refresh(user)
    return user

def delete_user_by_id(user_id):
    """Lösche einen User anhand seiner user_id."""
    user = find_user_by_id(user_id)
    if user:
        with SessionLocal() as session:
            session.delete(user)
            session.commit()
        return True
    return False

def update_user(user):
    """Aktualisiere einen existierenden User (alle Felder, die geändert wurden)."""
    with SessionLocal() as session:
        session.merge(user)
        session.commit()
    return user


def find_user_activities_and_erfuellungen(user):
    with SessionLocal() as session:
        return session.query(Aufgabenerfuellung).options(joinedload(Aufgabenerfuellung.aufgabe)).filter_by(user_id=user.user_id).all()

def get_user_feed(user_id):
    with SessionLocal() as session:
        #Gruppen des Users holen - dafür müssen die Memberships abgefragt werden
        subquery = session.query(Membership.gruppe_id).filter(Membership.user_id == user_id).subquery()

        #Die Beiträge setzen sich zusammen aus den Aufgabenerfüllungen, den Aufgaben, den Sportarten und den Votes
        #Werden dann entsprechend der Gruppenzugehörigkeit des Users gefiltert
        beitraege = session.query(Beitrag) \
            .join(Beitrag.erfuellung) \
            .join(Aufgabenerfuellung.aufgabe) \
            .join(Aufgabe.sportart) \
            .options(
                joinedload(Beitrag.erfuellung) \
                    .joinedload(Aufgabenerfuellung.aufgabe)\
                    .joinedload(Aufgabe.sportart),
                joinedload(Beitrag.votes)
        ) \
            .filter(Aufgabenerfuellung.gruppe_id.in_(subquery)) \
            .order_by(Beitrag.erstellDatum.desc()) \
            .all()

        result = [serialize_beitrag(b, user_id) for b in beitraege]
        return result