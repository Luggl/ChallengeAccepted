from database.database import engine
from database.models import Base

Base.metadata.create_all(bind=engine)

print("Datenbanktabellen wurden erfolgreich erstellt.")