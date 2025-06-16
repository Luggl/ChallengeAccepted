from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, sessionmaker

DATABASE_URL = "sqlite:///C:/Users/TS10/PycharmProjects/gruppe-14---challenge-accepted_new/mydatabase.db"

Base=declarative_base()
engine=create_engine(DATABASE_URL, echo=True)
SessionLocal= sessionmaker(bind=engine)

