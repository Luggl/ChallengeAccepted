from sqlalchemy.orm import joinedload

from app.database.database import SessionLocal
from app.database.models import ResetToken
from app.utils.time import now_berlin

def find_token_by_string(token_str):
    """Finde einen ResetToken anhand des Tokens und lade den User direkt mit."""
    with SessionLocal() as session:
        return session.query(ResetToken)\
            .options(joinedload(ResetToken.user))\
            .filter_by(token=token_str)\
            .first()


def save_token(token):
    with SessionLocal() as session:
        session.add(token)
        session.commit()


def delete_token(token):
    with SessionLocal() as session:
        session.delete(token)
        session.commit()


def delete_token_by_user_id(user_id):
    with SessionLocal() as session:
        session.query(ResetToken).filter_by(user_id=user_id).delete()
        session.commit()
