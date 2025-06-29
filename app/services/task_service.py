import os

from werkzeug.utils import secure_filename
from app.utils.response import response
from app.repositories.task_repository import (
    save_aufgabe,
    find_task_by_challenge_and_date,
    find_task_by_challenge_and_date_and_typ,
    mark_task_as_complete,
    has_user_already_voted,
    create_user_vote,
    find_aufgabenerfuellung_by_user_id,
    find_aufgabenerfuellung_by_challenge_and_date, update_task_by_video_url,
    add_streak
)
from app.database.models import BeitragVotes, Beitrag, AufgabeTyp, StandardAufgabe
from repositories.beitrag_repository import (
    find_beitrag_by_id,
    find_gruppe_by_beitrag,
    create_beitrag,
    find_beitrag_by_erfuellung_id
)
from repositories.challenge_repository import (
    find_standard_challenge_by_id,
    find_standard_challenge_sportarten_by_challenge_id,
    find_all_survival_challenges,
    find_active_challenge_by_group
)
import uuid
import random
from collections import defaultdict
from datetime import datetime, time, timedelta

from repositories.group_repository import find_group_by_id
from repositories.membership_repository import find_memberships_by_user
from services.schedule import schedule_deadline_job
from utils.auth_utils import get_uuid_formated_id
from utils.serialize import serialize_aufgabenerfuellung
from utils.time import now_berlin, date_today
from app.repositories.sportart_repository import find_sportart_by_id, find_intervall_by_sportart_and_schwierigkeit
from app.database.models import SurvivalAufgabe


UPLOAD_ROOT = "media/aufgabenerfuellung"

# Alle Tasks für einen User abfragen
def get_task_logic(user_id):
    # Survival Tasks erzeugen, falls neue Vorhanden!
    result = generate_survival_tasks_for_all_challenges()
    if not result["success"]:
        return result

    #Alle Memberships des Users holen
    memberships = find_memberships_by_user(get_uuid_formated_id(user_id))
    datum = date_today()
    if not memberships:
        return response(False, error="User in keiner Gruppe!")

    #Liste an Aufgabenerfüllungen erzeugen
    aufgabenerfuellungen = []
    inaktive_Challenges = []
    #Für alle Memberships die jeweiligen Tasks laden
    for membership in memberships:
        challenge = find_active_challenge_by_group(membership.gruppe_id)
        if not challenge:
            try:
                gruppe_id = str(uuid.UUID(bytes=membership.gruppe_id))
                gruppe_name = find_group_by_id(membership.gruppe_id).gruppenname
                inaktive_Challenges.append({
                    "gruppe-id": gruppe_id,
                    "gruppe-name": gruppe_name,
                    "status": "Keine aktive Challenge gefunden"
                })
            except Exception:
                inaktive_Challenges.append({
                    "status": "Fehler beim Laden der Gruppe aus der Membership!"
                })
            continue
        aufgabenerfuellung = find_aufgabenerfuellung_by_challenge_and_date(challenge.challenge_id, datum)
        if aufgabenerfuellung:
            aufgabenerfuellungen.append(serialize_aufgabenerfuellung(aufgabenerfuellung))

    return response(True, data={
        "Aufgaben": aufgabenerfuellungen,
        "Hinweise": inaktive_Challenges,
    })


# Aufgabe als erledigt markieren
def complete_task_logic(erfuellung_id, user_id, description, video_file):
    user_id_uuid = get_uuid_formated_id(user_id)
    erfuellung_id_uuid = get_uuid_formated_id(erfuellung_id)
    #Prüfen, ob User diese Task überhaupt hat
    usercheck = find_aufgabenerfuellung_by_user_id(user_id_uuid)

    if not any(e.erfuellung_id == erfuellung_id_uuid for e in usercheck):
        return response(False, error="User hält diese Aufgabe nicht!")

    if not video_file:
        return response(False, error="Video ist erforderlich!")

    #Video speichern
    success = safe_video_logic(erfuellung_id_uuid, video_file)
    if not success["success"]:
        return success

    videopath = success["data"]
    # Videopfad in Aufgabenerfüllung speichern
    success = update_task_by_video_url(erfuellung_id_uuid, videopath)
    #Aufgabenerfüllung Status updaten
    success = mark_task_as_complete(erfuellung_id_uuid, description)
    if not success:
        return response(False, error="Aufgabe nicht gefunden oder konnte nicht abgeschlossen werden.")

    # Check ob Beitrag bereits vorhanden!
    beitrag_check = find_beitrag_by_erfuellung_id(erfuellung_id_uuid)
    if beitrag_check:
        return response(False, error="Beitrag bereits vorhanden!")
    #Beitrag erstellen
    beitrag = Beitrag(
        erstellDatum=date_today(),
        erfuellung_id=erfuellung_id_uuid
    )

    success = create_beitrag(beitrag)
    if not success:
        return response(False, error="Beitrag konnte nicht erstellt werden")

    # Streak wird nach Abschluss entsprechend erhöht
    success = add_streak(user_id_uuid)
    if not success:
        return response(False, error="Streak konnte nicht erfolgreich erhöht werden!")

    return response(True, data="Aufgabe als erledigt markiert und Beitrag erstellt.")

def safe_video_logic(task_id, video_file):
    filename = secure_filename(f"{uuid.uuid4()}.mp4")
    task_id_str = str(uuid.UUID(bytes=task_id))
    upload_path = os.path.join(UPLOAD_ROOT, task_id_str)
    os.makedirs(upload_path, exist_ok=True)

    full_path = os.path.join(upload_path, filename)
    try:
        video_file.save(full_path)
        return response(True, data=full_path)
    except Exception as e:
        return response(False, error="Fehler beim Speichern des Videos!")


# Vote abgeben für ein Task-Ergebnis
def vote_logic(user_id, beitrag_id, vote):
    # Optional: prüfen, ob User schon abgestimmt hat
    beitrag_id_uuid = get_uuid_formated_id(beitrag_id)
    user_id_uuid = get_uuid_formated_id(user_id)
    if has_user_already_voted(user_id_uuid, beitrag_id_uuid):
        return response(False, error="Du hast bereits abgestimmt.")

    gruppe_id = find_gruppe_by_beitrag(find_beitrag_by_id(beitrag_id))

    vote = BeitragVotes(
        beitrag_id=beitrag_id,
        user_id=user_id,
        vote=vote,
        gruppe_id=gruppe_id,
    )

    result = create_user_vote(vote)
    if not result:
        return response(False, error="Stimme konnte nicht gespeichert werden.")

    return response(True, data="Stimme erfolgreich abgegeben.")



def generate_standard_tasks_for_challenge_logic(challenge_id: bytes):
    """
    Generiert automatisch Aufgaben für eine Standard-Challenge mit mehreren Sportarten.
    Jede Sportart beginnt bei ihrer Startintensität und endet bei ihrer Zielintensität,
    unabhängig davon, wie oft sie im rotierenden Plan verwendet wird.
    """

    challenge = find_standard_challenge_by_id(challenge_id)
    if not challenge:
        return response(False, error="Challenge nicht gefunden")

    sportarten_links = find_standard_challenge_sportarten_by_challenge_id(challenge_id)
    if not sportarten_links:
        return response(False, error="Keine Sportarten zugeordnet!")

    duration = (challenge.enddatum - challenge.startdatum).days + 1
    if duration <= 0:
        return response(False, error="Ungültige Challenge-Dauer")

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
        schedule_deadline_job(aufgabe)  # Hier wird der Hintergrundjob initialisiert, der den Status nach Deadline verändert


    return response(True, data=f"{duration} Aufgaben erstellt")

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
    return response(False, error="Keine Aufgabe für dieses Datum gefunden.")


def generate_survival_tasks_for_all_challenges():
    """Generiert täglich Survival-Aufgaben für alle Survival-Challenges, falls noch nicht vorhanden."""
    today = now_berlin().date()
    erfolge = []
    typ = "survival"
    challenges = find_all_survival_challenges()
    for challenge in challenges:
        existing = find_task_by_challenge_and_date_and_typ(challenge.challenge_id, today, typ)
        if existing:
            erfolge.append({"challenge_id": str(uuid.UUID(bytes=challenge.challenge_id)), "status": "bereits vorhanden"})
            continue

        sportarten_links = challenge.sportarten_links
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

        aufgabe_id = save_aufgabe(aufgabe)
        erfolge.append({"challenge_id": str(uuid.UUID(bytes=challenge.challenge_id)), "status": "erstellt", "task_id": str(uuid.UUID(bytes=aufgabe_id))})

    return response(True, data=erfolge)