import uuid
from sqlalchemy import Column, String, Integer,Boolean, Date, ForeignKey, Table
from sqlalchemy.dialects.sqlite import BLOB
from sqlalchemy.orm import relationship
from database import Base
from enum import Enum
from sqlalchemy import Enum as SQLEnum

class StatusUnit(Enum):
    anzahl="anzahl"
    dauer="dauer"

class Schwierigkeit(Enum):
    easy="easy"
    medium="medium"
    hard="hard"

class AufgabeStatus(Enum):
    offen="offen"
    abgeschlossen="abgeschlossen"
    nicht_gemacht="nicht gemacht"


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

class Membership(Base):
    __tablename__ = "membership"
    user_id=Column(BLOB, ForeignKey("user.user_id"), primary_key=True)
    gruppe_id=Column(BLOB, ForeignKey("gruppe.gruppe_id"), primary_key=True)
    isAdmin=Column(Boolean, default=False)

    user=relationship("User", back_populates="membership")
    gruppe=relationship("Gruppe", back_populates="memberships")
class Gruppe(Base):
    __tablename__="gruppe"
    gruppe_id=Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    gruppenname=Column(String, nullable=False, unique=True)
    beschreibung=Column(String)
    gruppenbild=Column(String)
    einladungscode=Column(String, nullable=False)
    einladungscodeDatum=Column(Date)
    erstellungsDatum=Column(Date)

    memberships= relationship("Membership", back_populates="gruppe")
    challenges=relationship("Challenge", back_populates="gruppe")

class Sportart(Base):
    __tablename__="sportart"

    sportart_id=Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    bezeichnung= Column(String, nullable=False)
    unit=Column(SQLEnum(StatusUnit), nullable=False)

    challenges=relationship("Challenge", secondary="challenge_sportart", back_populates="sportarten")
