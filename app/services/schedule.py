from apscheduler.triggers.date import DateTrigger
from app import scheduler
from repositories.task_repository import handle_abgelaufene_aufgabe


def schedule_deadline_job(aufgabe):
    trigger = DateTrigger(run_date=aufgabe.deadline)
    job_id = f"aufgabe_{aufgabe.aufgabe_id}"
    scheduler.add_job(
        func=handle_abgelaufene_aufgabe,
        trigger=trigger,
        args=[aufgabe.aufgabe_id],
        id=job_id,
        replace_existing=True
    )