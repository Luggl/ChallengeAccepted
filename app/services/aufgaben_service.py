import uuid
import random
from collections import defaultdict
from datetime import datetime, time, timedelta
from app.database.models import AufgabeTyp, StandardAufgabe
from utils.time import now_berlin, date_today
from repositories.challenge_repository import (
    find_standard_challenge_by_id,
    find_standard_challenge_sportarten_by_challenge_id, find_survival_challenge_by_id, find_all_survival_challenges
)
from repositories.task_repository import save_aufgabe, find_task_by_challenge_and_date, find_task_by_challenge_and_date_and_typ
from app.repositories.sportart_repository import find_sportart_by_id, find_intervall_by_sportart_and_schwierigkeit
from app.database.models import SurvivalAufgabe

def generate_standard_tasks_for_challenge_logic(challenge_id: bytes):
    """
    Generiert automatisch Aufgaben für eine Standard-Challenge mit mehreren Sportarten.
    Jede Sportart beginnt bei ihrer Startintensität und endet bei ihrer Zielintensität,
    unabhängig davon, wie oft sie im rotierenden Plan verwendet wird.
    """

    challenge = find_standard_challenge_by_id(challenge_id)
    if not challenge:
        return {"success": False, "error": "Challenge nicht gefunden."}

    sportarten_links = find_standard_challenge_sportarten_by_challenge_id(challenge_id)
    if not sportarten_links:
        return {"success": False, "error": "Keine Sportarten zugeordnet."}

    duration = (challenge.enddatum - challenge.startdatum).days + 1
    if duration <= 0:
        return {"success": False, "error": "Ungültige Challenge-Dauer."}

    # 1. Zähle, wie oft jede Sportart vorkommt
    sportart_id_list = [link.sportart_id for link in sportarten_links]
    einsatzplan = [sportart_id_list[i % len(sportart_id_list)] for i in range(duration)]

    sportart_einsatz_zaehlung = defaultdict(int)
    for sportart_id in einsatzplan:
        sportart_einsatz_zaehlung[sportart_id] += 1

    # 2. Initialisiere Zähler pro Sportart
    einsatz_index = defaultdict(int)

    # 3. Aufgabe pro Tag generieren
    for i in range(duration):
        tag_datum = challenge.startdatum + timedelta(days=i)
        sportart_id = einsatzplan[i]
        einsatz_index[sportart_id] += 1

        sportart_link = next(filter(lambda x: x.sportart_id == sportart_id, sportarten_links))
        sportart = find_sportart_by_id(sportart_id)

        total_einsaetze = sportart_einsatz_zaehlung[sportart_id]
        aktueller_index = einsatz_index[sportart_id] - 1

        if total_einsaetze == 1:
            zielwert = sportart_link.zielintensitaet
        elif aktueller_index == 0:
            zielwert = sportart_link.startintensitaet
        elif aktueller_index == total_einsaetze - 1:
            zielwert = sportart_link.zielintensitaet
        else:
            faktor = aktueller_index / (total_einsaetze - 1)
            zielwert = round(
                sportart_link.startintensitaet +
                (sportart_link.zielintensitaet - sportart_link.startintensitaet) * faktor
            )

        deadline = datetime.combine(tag_datum, time(23, 59))
        beschreibung = f"Erfülle heute {zielwert} {sportart.unit.value} {sportart.bezeichnung}."

        aufgabe = StandardAufgabe(
            challenge_id=challenge_id,
            sportart_id=sportart_id,
            datum=tag_datum,
            zielwert=zielwert,
            unit=sportart.unit,
            typ=AufgabeTyp.standard.name,
            deadline=deadline,
            beschreibung=beschreibung
        )

        save_aufgabe(aufgabe)

    return {"success": True, "message": f"{duration} Aufgaben erstellt."}


def get_task_by_date(challenge_id: uuid.UUID, datum: str = None):
    """
    Gibt eine Aufgabe für ein bestimmtes Datum zurück.
    Wenn kein Datum übergeben wird, wird automatisch das heutige verwendet (Europe/Berlin).
    """
    if not datum:
        datum = date_today().isoformat()

    aufgabe = find_task_by_challenge_and_date(challenge_id, datum)
    if aufgabe:
        return {
            "success": True,
            "task": {
                "id": str(aufgabe.aufgabe_id),
                "datum": aufgabe.datum.isoformat(),
                "zielwert": aufgabe.zielwert,
                "unit": aufgabe.unit,
                "sportart_id": str(aufgabe.sportart_id),
                "typ": aufgabe.typ
            }
        }
    return {"success": False, "error": "Keine Aufgabe für dieses Datum gefunden."}


def generate_survival_tasks_for_all_challenges():
    """Generiert täglich Survival-Aufgaben für alle Survival-Challenges, falls noch nicht vorhanden."""
    today = now_berlin().date()
    erfolge = []

    challenges = find_all_survival_challenges()
    for challenge in challenges:
        existing = find_task_by_challenge_and_date_and_typ(challenge.challenge_id, today, AufgabeTyp.survival.name)
        if existing:
            erfolge.append({"challenge_id": str(uuid.UUID(bytes=challenge.challenge_id)), "status": "bereits vorhanden"})
            continue

        sportarten_links = challenge.survival_sportarten
        if not sportarten_links:
            erfolge.append({"challenge_id": str(uuid.UUID(bytes=challenge.challenge_id)), "status": "keine Sportarten"})
            continue

        zufalls_sportart_link = random.choice(sportarten_links)
        sportart = find_sportart_by_id(zufalls_sportart_link.sportart_id)
        schwierigkeitsgrad = zufalls_sportart_link.schwierigkeitsgrad

        tag_index = (today - challenge.startdatum).days
        steigerungsstufe = tag_index // 7
        steigerungsfaktor = sportart.steigerungsfaktor or 1.0

        intervall = find_intervall_by_sportart_and_schwierigkeit(sportart.sportart_id, schwierigkeitsgrad)
        min_val, max_val = intervall.min_wert, intervall.max_wert
        diff = max_val - min_val

        zielwert = min_val + round((diff / 10) * steigerungsstufe * steigerungsfaktor)
        zielwert = min(zielwert, max_val)

        jetzt = now_berlin()
        startzeit = jetzt.replace(hour=random.randint(8, 21), minute=random.randint(0, 59), second=0, microsecond=0)
        endzeit = startzeit + timedelta(hours=2)

        beschreibung = f"Erfülle in 2h: {zielwert} {sportart.unit.value} {sportart.bezeichnung}."

        aufgabe = SurvivalAufgabe(
            aufgabe_id=uuid.uuid4().bytes,
            challenge_id=challenge.challenge_id,
            sportart_id=sportart.sportart_id,
            zielwert=zielwert,
            unit=sportart.unit,
            typ=AufgabeTyp.survival,
            startzeit=startzeit,
            datum=today,
            deadline=endzeit,
            tag_index=tag_index,
            beschreibung=beschreibung
        )

        save_aufgabe(aufgabe)
        erfolge.append({"challenge_id": str(uuid.UUID(bytes=challenge.challenge_id)), "status": "erstellt", "task_id": str(uuid.UUID(bytes=aufgabe.aufgabe_id))})

    return {"success": True, "results": erfolge}
