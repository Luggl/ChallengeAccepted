from apscheduler.triggers.date import DateTrigger
from app import scheduler
from repositories.task_repository import handle_abgelaufene_aufgabe
from services.task_service import generate_survival_tasks_for_all_challenges


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

def run_daily_survival_task():
    generate_survival_tasks_for_all_challenges()