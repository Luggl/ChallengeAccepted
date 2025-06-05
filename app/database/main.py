from database.database import engine
import database.models
from .models import Base
database.models.Base.metadata.create_all(bind=engine)

print("Datenbanktabellen wurden erfolgreich erstellt.")