import pytz
from datetime import datetime, date
import calendar

# Deutsche Zeitzone deklarieren
BERLIN = pytz.timezone("Europe/Berlin")

# Aktuelle deutsche Zeit erhalten
def now_berlin():
    return datetime.now(BERLIN)

def date_today():
    return date.today()

def get_all_days(year, month):
    _, num_days = calendar.monthrange(year, month)          #da der return von monthrange zuerst den ersten Tag des Monats zurückgibt wird die Variable _ benannt (Nicht benötigt)
    return [date(year, month, day) for day in range(1, num_days + 1)]