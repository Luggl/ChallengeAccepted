from apscheduler.triggers.date import DateTrigger
from repositories.task_repository import handle_abgelaufene_aufgabe


def schedule_deadline_job(scheduler, aufgabe):
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
    from services.task_service import generate_survival_tasks_for_all_challenges
    generate_survival_tasks_for_all_challenges()