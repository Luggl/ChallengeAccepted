import uuid
import sqlalchemy
from sqlalchemy import Column, String, Integer, Boolean, Date, ForeignKey, Table, ForeignKeyConstraint, DateTime, Float, Enum as SQLEnum, and_
from sqlalchemy.dialects.mysql import DATETIME
from sqlalchemy.dialects.sqlite import BLOB
from sqlalchemy.orm import relationship, foreign, configure_mappers
from .database import Base
from enum import Enum
configure_mappers()


class StatusUnit(Enum):
    anzahl="anzahl"
    dauer="dauer"

class AchievementKategorie(Enum):
    bonusaufgaben="Bonusaufgaben"
    community="Community"
    gruppenaktivitaet="Gruppenaktivität"
    spezial="Spezial"
    sportarten="Sportarten"
    standard="Standard-Aufgaben"
    streaks="Streaks"
    survival="Survival-Aufgaben"
    survivalChallenges="Survival Challenges"

class AchievementStufe(Enum):
    bronze="Bronze"
    silber="Silber"
    gold="Gold"

class Schwierigkeit(Enum):
    easy="easy"
    medium="medium"
    hard="hard"

class AufgabeStatus(Enum):
    offen="offen"
    abgeschlossen="abgeschlossen"
    nicht_gemacht="nicht gemacht"

class AufgabeTyp(Enum):
    standard = "standard"
    survival = "survival"
    bonus = "bonus"

class Vote(Enum):
    akzeptiert = "akzeptiert"
    abgelehnt = "abgelehnt"
    offen = "offen"

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

    achievement_links=relationship(
        "UserAchievement",
        back_populates="user",
        cascade="all, delete-orphan"
    )
    token=relationship(
        "ResetToken",
        back_populates="user",
        uselist=False,
        cascade="all, delete-orphan")
    membership=relationship(
        "Membership",
        back_populates="user",
        cascade="all, delete-orphan")

class Achievement (Base):
    __tablename__="achievement"
    achievement_id=Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    kategorie=Column(SQLEnum(AchievementKategorie), nullable=False)
    stufe=Column(SQLEnum(AchievementStufe), nullable=False)
    titel=Column(String, nullable=False)
    beschreibung= Column(String)

    condition_type=Column(String, nullable=False) # Kondition z.B. standard_tasks_completed, streak_days, etc.
    condition_value=Column(String, nullable=False) # Konditionswert z.B. 5, 10, 20

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
    gruppe_id=Column(BLOB, ForeignKey("gruppe.gruppe_id", ondelete="CASCADE"), primary_key=True)
    isAdmin=Column(Boolean, default=False)

    user=relationship("User", back_populates="membership")
    gruppe=relationship("Gruppe", back_populates="memberships")

class Gruppe(Base):
    __tablename__="gruppe"
    gruppe_id=Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    gruppenname=Column(String, nullable=False, unique=False)
    beschreibung=Column(String)
    gruppenbild=Column(String)
    einladungscode=Column(String, nullable=False)
    einladungscode_gueltig_bis=Column(DATETIME)
    erstellungsDatum=Column(Date)

    memberships= relationship("Membership", back_populates="gruppe", cascade="all, delete-orphan", passive_deletes=True)
    challenges=relationship("Challenge", back_populates="gruppe", cascade="all, delete-orphan", passive_deletes=True)

class Sportart(Base):
    __tablename__ = "sportart"

    sportart_id = Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    bezeichnung = Column(String, nullable=False)
    unit = Column(SQLEnum(StatusUnit), nullable=False)
    steigerungsfaktor = Column(Float, nullable=False, default=1.0)


    # Neu: separate Beziehungen für beide Challenge-Typen
    standard_links = relationship("StandardChallengeSportart", back_populates="sportart")
    survival_links = relationship("SurvivalChallengeSportart", back_populates="sportart")
    intervalle = relationship("SportartIntervall", back_populates="sportart", cascade="all, delete-orphan")

class StandardChallengeSportart(Base):
    __tablename__ = "standard_challenge_sportart"

    challenge_id = Column(BLOB, ForeignKey("standard_challenge.challenge_id", ondelete="CASCADE"), primary_key=True)
    sportart_id = Column(BLOB, ForeignKey("sportart.sportart_id"), primary_key=True)

    startintensitaet = Column(Integer, nullable=False)
    zielintensitaet = Column(Integer, nullable=False)

    challenge = relationship("StandardChallenge", back_populates="sportarten_links", passive_deletes=True)
    sportart = relationship("Sportart", back_populates="standard_links")

class SurvivalChallengeSportart(Base):
    __tablename__ = "survival_challenge_sportart"

    challenge_id = Column(BLOB, ForeignKey("survival_challenge.challenge_id", ondelete="CASCADE"), primary_key=True)
    sportart_id = Column(BLOB, ForeignKey("sportart.sportart_id"), primary_key=True)

    schwierigkeitsgrad = Column(
        sqlalchemy.Enum(Schwierigkeit), nullable=False
    )

    challenge = relationship("Survivalchallenge", back_populates="sportarten_links", passive_deletes=True)
    sportart = relationship("Sportart", back_populates="survival_links")

class SportartIntervall(Base):
    __tablename__ = "sportart_intervall"

    id = Column(Integer, primary_key=True)
    sportart_id = Column(BLOB, ForeignKey("sportart.sportart_id"))
    schwierigkeitsgrad = Column(SQLEnum(Schwierigkeit), nullable=False)
    min_wert = Column(Integer, nullable=False)
    max_wert = Column(Integer, nullable=False)

    sportart = relationship("Sportart", back_populates="intervalle")

class Challenge(Base):
    __tablename__="challenge"

    challenge_id=Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    startdatum=Column(Date)
    typ=Column(String(50))
    active=Column(Boolean, default=True)

    gruppe_id=Column(BLOB, ForeignKey("gruppe.gruppe_id"))
    gruppe=relationship("Gruppe", back_populates="challenges")

    ersteller_user_id = Column(BLOB)
    ersteller_gruppe_id = Column(BLOB)

    __table_args__ = (
        ForeignKeyConstraint(
            ["ersteller_user_id", "ersteller_gruppe_id"],
            ["membership.user_id", "membership.gruppe_id"],
            ondelete="CASCADE"
        ),
    )

    ersteller=relationship(
        "Membership",
        primaryjoin=and_(
            foreign(ersteller_user_id)==Membership.user_id,
            foreign(ersteller_gruppe_id)==Membership.gruppe_id
        ),
        foreign_keys=[Membership.user_id, Membership.gruppe_id],
    )

    __mapper_args_={
        "polymorphic_identity":"challenge",
        "polymorphic_on":typ
    }

    aufgaben=relationship("Aufgabe",
                          back_populates="challenge",
                          cascade="all, delete-orphan",
                          single_parent=True,
                          )

class StandardChallenge(Challenge):
    __tablename__ = "standard_challenge"
    challenge_id = Column(BLOB, ForeignKey("challenge.challenge_id", ondelete="CASCADE"),primary_key=True)
    dauer=Column(Integer)
    enddatum = Column(Date)

    __mapper_args_ = {
        "polymorphic_identity":"standard"
    }

    sportarten_links = relationship("StandardChallengeSportart", back_populates="challenge", cascade="all, delete-orphan")

class Survivalchallenge(Challenge):
    __tablename__ = "survival_challenge"
    challenge_id = Column(BLOB, ForeignKey("challenge.challenge_id", ondelete="CASCADE"), primary_key=True)
    __mapper_args__ = {
        "polymorphic_identity": AufgabeTyp.survival.value
    }

    sportarten_links = relationship("SurvivalChallengeSportart", back_populates="challenge", cascade="all, delete-orphan")

class Aufgabe(Base):
    __tablename__="aufgabe"


    aufgabe_id=Column(BLOB, primary_key=True, default=lambda : uuid.uuid4().bytes)
    beschreibung=Column(String)
    zielwert=Column(Integer)
    dauer=Column(Integer)
    deadline = Column(DateTime, nullable=True)
    datum = Column(Date, nullable=True)
    unit=Column(SQLEnum(StatusUnit), nullable=False)
    typ = Column(SQLEnum(AufgabeTyp, nullable=False))

    challenge_id=Column(BLOB, ForeignKey("challenge.challenge_id"))
    challenge=relationship("Challenge",back_populates="aufgaben")

    sportart_id=Column(BLOB, ForeignKey("sportart.sportart_id"))
    sportart=relationship("Sportart")

    erfuellungen=relationship("Aufgabenerfuellung",back_populates="aufgabe")

    __mapper_args__ = {
            "polymorphic_on": typ,
            # Für alle Unterklassen - Standard / Survival / Bonus
            "with_polymorphic": "*"  # erlaubt JOIN Abfragen über alle Unterklassen hinweg
        }

class StandardAufgabe(Aufgabe):
    __tablename__ = "standard_aufgabe"
    aufgabe_id=Column(BLOB, ForeignKey("aufgabe.aufgabe_id"), primary_key=True)
    __mapper_args__ = {"polymorphic_identity": AufgabeTyp.standard}


class SurvivalAufgabe(Aufgabe):
    __tablename__ = "survival_aufgabe"


    aufgabe_id = Column(BLOB, ForeignKey("aufgabe.aufgabe_id"), primary_key=True)
    startzeit = Column(DATETIME)
    tag_index = Column(Integer)

    __mapper_args__ = {"polymorphic_identity": AufgabeTyp.survival}

class BonusAufgabe(Aufgabe):
    __tablename__ ="bonus_aufgabe"

    aufgabe_id = Column(BLOB, ForeignKey("aufgabe.aufgabe_id"),primary_key=True)
    bonus_punkte=Column(Integer, default=0)
    ist_freiwillig=Column(Boolean, default=True)

    __mapper_args__ = {
        "polymorphic_identity":AufgabeTyp.bonus
    }

class Aufgabenerfuellung (Base):
    __tablename__="aufgabenerfuellung"

    erfuellung_id=Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    status=Column(SQLEnum(AufgabeStatus), nullable=False)
    video_url=Column(String)
    datum=Column(Date)
    beschreibung=Column(String)
    aufgabe_id= Column(BLOB, ForeignKey("aufgabe.aufgabe_id"))
    aufgabe=relationship("Aufgabe", back_populates="erfuellungen")

    user_id = Column(BLOB)
    gruppe_id = Column(BLOB)

    __table_args__ = (
        ForeignKeyConstraint(
            ["user_id", "gruppe_id"],
            ["membership.user_id", "membership.gruppe_id"],
            ondelete="CASCADE"
        ),
    )

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
    erstellDatum=Column(Date)
    erfuellung_id=Column(BLOB, ForeignKey("aufgabenerfuellung.erfuellung_id"), unique=True)

    erfuellung=relationship("Aufgabenerfuellung", back_populates="beitrag")

    votes = relationship(
        "BeitragVotes",
        back_populates="beitrag",
        cascade="all, delete-orphan",
    )


class BeitragVotes(Base):
    __tablename__ = "beitrag_votes"
    beitragvote_id = Column(BLOB, primary_key=True, default=lambda: uuid.uuid4().bytes)
    beitrag_id = Column(BLOB, ForeignKey("beitrag.beitrag_id"))
    user_id = Column(BLOB)
    gruppe_id = Column(BLOB)
    __table_args__ = (
        ForeignKeyConstraint(
            ["user_id", "gruppe_id"],
            ["membership.user_id", "membership.gruppe_id"],
            ondelete="CASCADE"
        ),
    )
    vote = Column(SQLEnum(Vote))

    mitglied = relationship(
        "Membership",
        primaryjoin=and_(
            foreign(user_id) == Membership.user_id,
            foreign(gruppe_id) == Membership.gruppe_id
        )
    )
    beitrag = relationship(
        "Beitrag",
        back_populates="votes",
    )

