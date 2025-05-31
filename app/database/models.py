import uuid
from sqlalchemy import Column, String, Integer,Boolean, Date, ForeignKey, Table
from sqlalchemy.dialects.sqlite import BLOB
from sqlalchemy.orm import relationship
from database import Base


class UserAchievement(Base):
    __tablename__="user_achievement"
    user_id= Column(BLOB,ForeignKey("user.user_id"), primary_key=True)
    achievement_id=Column(BLOB,ForeignKey("achievement.achievement_id"), primary_key=True)
    erhalten=Column(Boolean, default=False)
    erhaltenAm=Column(Date)

    user=relationship("User", back_populates="achievement_links")
    achievement=relationship("Achievement", back_populates="user_links")


class User(Base):
    __tablename__ = "user"

    user_id=Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    username=Column(String, nullable=False, unique=True)
    email=Column(String, nullable=False, unique=True)
    passwordHash=Column(String)
    profilbild=Column(String)
    streak=Column(Integer, default=0)

    achievement_links=relationship("UserAchievement", back_populates="user")
    token=relationship("ResetToken",back_populates="user", uselist=False)

class Achievement (Base):
    __tablename__="achievement"
    achievement_id=Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    titel=Column(String, nullable=False)
    beschreibung= Column(String)

    user_links=relationship("UserAchievement", back_populates="achievement")


class ResetToken(Base):
    __tablename__="resettoken"

    token=Column(String, primary_key=True)
    gueltigBis=Column(Date)

    user_id=Column(BLOB, ForeignKey("user.user_id"), unique=True)
    user=relationship("User", back_populates="token")