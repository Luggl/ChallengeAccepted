from app.database.database import engine
from app.database import models

models.Base.metadata.create_all(bind=engine)

print("Datenbanktabellen wurden erfolgreich erstellt.")