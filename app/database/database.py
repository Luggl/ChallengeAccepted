from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, sessionmaker

DATABASE_URL= r"sqlite:///C:/Users/User/DatenbankFitnessApp/myrepo/gruppe-14---challenge-accepted/mydatabase.db"
Base=declarative_base()
engine=create_engine(DATABASE_URL, echo=True)
SessionLocal= sessionmaker(bind=engine)

