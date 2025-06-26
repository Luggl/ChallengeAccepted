from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, sessionmaker
from sqlalchemy import event

DATABASE_URL = r"sqlite:////root/ChallengeAccepted/mydatabase.db"

Base=declarative_base()
engine=create_engine(DATABASE_URL, echo=True)
SessionLocal= sessionmaker(bind=engine)


#SQLAlchemy deaktiviert FK-Constraits standardmäßig - Hier FK-Support aktivieren:
@event.listens_for(engine, "connect")
def set_sqlite_pragma(dbapi_connection, connection_record):
    cursor = dbapi_connection.cursor()
    cursor.execute("PRAGMA foreign_keys=ON")
    cursor.close()
