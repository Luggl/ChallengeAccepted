from app.database.database import engine
from app.database.models import Base

Base.metadata.create_all(bind=engine)

print("Datenbanktabellen wurden erfolgreich erstellt.")