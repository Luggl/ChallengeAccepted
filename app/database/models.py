import uuid
from sqlalchemy import Column, String, Integer,Boolean, Date, ForeignKey, Table
from sqlalchemy.dialects.sqlite import BLOB
from sqlalchemy.orm import relationship
from .database import Base
from enum import Enum
from sqlalchemy import Enum as SQLEnum
from sqlalchemy import and_
from sqlalchemy.orm import foreign


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
    membership=relationship("Membership", back_populates="user")

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

challenge_sportart=Table(
    "challenge_sportart",
    Base.metadata,
    Column("challenge_id",BLOB,ForeignKey("challenge.challenge_id"),primary_key=True),
    Column("sportart_id",BLOB,ForeignKey("sportart.sportart_id"),primary_key=True)
)

class Challenge(Base):
    __tablename__="challenge"

    challenge_id=Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    startdatum=Column(Date)
    typ=Column(String(50))

    gruppe_id=Column(BLOB, ForeignKey("gruppe.gruppe_id"))
    gruppe=relationship("Gruppe", back_populates="challenges")

    ersteller_user_id=Column(BLOB, ForeignKey("membership.user_id"))
    ersteller_gruppe_id=Column(BLOB, ForeignKey("membership.gruppe_id"))
    ersteller=relationship(
        "Membership",
        primaryjoin=and_(
            foreign(ersteller_user_id)==Membership.user_id,
            foreign(ersteller_gruppe_id)==Membership.gruppe_id
        )
    )

    __mapper_args_={
        "polymorphic_identity":"challenge",
        "polymorphic_on":typ
    }

    sportarten=relationship("Sportart", secondary=challenge_sportart,back_populates="challenges")
    aufgaben=relationship("Aufgabe", back_populates="challenge")

class StandardChallenge(Challenge):
    __tablename__ = "standard_challenge"
    challenge_id = Column(BLOB, ForeignKey("challenge.challenge_id"),primary_key=True)
    dauer=Column(Integer)

    __mapper_args_ = {
        "polymorphic_identity":"standard"
    }

class Survivalchallenge(Challenge):
    __tablename__ = "survival_challenge"
    challenge_id = Column(BLOB, ForeignKey("challenge.challenge_id"), primary_key=True)
    __mapper_args_ = {
        "polymorphic_identity":"survival"
    }
class Aufgabe(Base):
    __tablename__="aufgabe"

    aufgabe_id=Column(BLOB, primary_key=True, default=lambda : uuid.uuid4().bytes)
    beschreibung=Column(String)
    zielwert=Column(Integer)
    dauer=Column(Integer)
    schwierigkeit=Column(SQLEnum(Schwierigkeit), nullable=False)
    unit=Column(SQLEnum(StatusUnit), nullable=False)
    typ=Column(String(50))

    challenge_id=Column(BLOB, ForeignKey("challenge.challenge_id"))
    challenge=relationship("Challenge",back_populates="aufgaben")

    sportart_id=Column(BLOB, ForeignKey("sportart.sportart_id"))
    sportart=relationship("Sportart")

    erfuellungen=relationship("Aufgabenerfuellung",back_populates="aufgabe")
    __mapper_args__={
        "polymorphic_identity":"normal",
        "polymorphic_on": typ
    }

class BonusAufgabe(Aufgabe):
    __tablename__ ="bonus_aufgabe"

    aufgabe_id = Column(BLOB, ForeignKey("aufgabe.aufgabe_id"),primary_key=True)
    bonus_punkte=Column(Integer, default=0)
    ist_freiwillig=Column(Boolean, default=True)

    __mapper_args__ = {
        "polymorphic_identity":"bonus"
    }

class Aufgabenerfuellung (Base):
    __tablename__="aufgabenerfuellung"

    erfuellung_id=Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    status=Column(SQLEnum(AufgabeStatus), nullable=False)
    bild=Column(String)
    datum=Column(Date)

    aufgabe_id= Column(BLOB, ForeignKey("aufgabe.aufgabe_id"))
    aufgabe=relationship("Aufgabe", back_populates="erfuellungen")

    user_id=Column(BLOB, ForeignKey("membership.user_id"))
    gruppe_id=Column(BLOB, ForeignKey("membership.gruppe_id"))

    mitglied=relationship(
        "Membership",
                     primaryjoin=and_(
                              foreign(user_id)==Membership.user_id,
                              foreign(gruppe_id)==Membership.gruppe_id
                          )
                    )
    beitrag=relationship("Beitrag", back_populates="erfuellung", uselist=False)
class Beitrag (Base):
    __tablename__="beitrag"

    beitrag_id=Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    video=Column(String, nullable=False)
    beschreibung=Column(String)
    erstellDatum=Column(Date)

    user_id=Column(BLOB, ForeignKey("membership.user_id"))
    gruppe_id=Column (BLOB, ForeignKey("membership.gruppe_id"))
    erfuellung_id=Column(BLOB, ForeignKey("aufgabenerfuellung.erfuellung_id"), unique=True)

    mitglied=relationship(
        "Membership",
        primaryjoin=and_(
            foreign(user_id)==Membership.user_id,
            foreign(gruppe_id)==Membership.gruppe_id
        )
    )
    erfuellung=relationship("Aufgabenerfuellung", back_populates="beitrag")

from sqlalchemy.orm import configure_mappers
configure_mappers()





