from flask import jsonify, Blueprint
from utils.scheduler import scheduler

scheduler_bp = Blueprint('scheduler', __name__)

@scheduler_bp.route('/debug/jobs', methods=['GET'])
def debug_jobs():
    for job in scheduler.get_jobs():
        print("⚙️ Job-ID:", job.id, flush=True)
        print("   Nächste Ausführung:", job.next_run_time, flush=True)
    return jsonify({"status": "Jobs wurden in die Konsole geschrieben"})