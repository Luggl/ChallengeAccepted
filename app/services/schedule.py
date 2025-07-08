from apscheduler.triggers.date import DateTrigger
from app.repositories.task_repository import handle_abgelaufene_aufgabe


def schedule_deadline_job(scheduler, aufgabe):
    """
    Job, um nach Ablauf der Deadline einer Aufgabe den Status entsprechend zu ändern
    Relevant für die korrekte Streak und um zu entscheiden, ob ein User aus der SurvivalChallenge ausscheidet
    """

    trigger = DateTrigger(run_date=aufgabe.deadline)        # Job wird ausgeführt nach Deadline der Aufgabe
    job_id = f"aufgabe_{aufgabe.aufgabe_id}"

    scheduler.add_job(
        func=handle_abgelaufene_aufgabe,
        trigger=trigger,
        args=[aufgabe.aufgabe_id],
        id=job_id,
        replace_existing=True
    )

def run_daily_survival_task():
    from app.services.task_service import generate_survival_tasks_for_all_challenges
    generate_survival_tasks_for_all_challenges()