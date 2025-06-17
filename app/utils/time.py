import pytz
from datetime import datetime, date

# Deutsche Zeitzone deklarieren
BERLIN = pytz.timezone("Europe/Berlin")

# Aktuelle deutsche Zeit erhalten
def now_berlin():
    return datetime.now(BERLIN)

def date_today():
    return date.today()