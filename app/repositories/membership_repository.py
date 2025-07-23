from sqlalchemy.orm import joinedload

from app.database.models import Membership
from app.database.database import SessionLocal

def find_membership(user_id, gruppe_id):
    """Finde die Mitgliedschaft eines Users in einer Gruppe."""
    with SessionLocal() as session:
        membership = session.query(Membership).filter_by(user_id=user_id, gruppe_id=gruppe_id).first()
    return membership

def find_memberships_by_user(user_id):
    """Finde alle Gruppen-Mitgliedschaften eines Users."""
    with SessionLocal() as session:
        return session.query(Membership).filter_by(user_id=user_id).all()

def find_memberships_by_group(gruppe_id):
    """Finde alle Mitglieder einer Gruppe."""
    with SessionLocal() as session:
        return session.query(Membership) \
            .options(joinedload(Membership.user)) \
            .filter_by(gruppe_id=gruppe_id)\
            .all()

def create_membership(membership):
    """Erstelle und speichere eine neue Membership."""
    with SessionLocal() as session:
        session.add(membership)
        session.flush()
        session.commit()
    return membership

def delete_membership(user_id, gruppe_id):
    """Lösche eine Mitgliedschaft anhand von User- und Gruppen-ID."""
    membership = find_membership(user_id, gruppe_id)
    with SessionLocal() as session:
        if membership:
            session.delete(membership)
            session.commit()
            return True
        return False


def is_user_admin(group_id, user_id):
    with SessionLocal() as session:
        membership = find_membership(group_id, user_id)
        if membership.isAdmin:
            return True
        return False