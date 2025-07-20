from flask import Blueprint, request, jsonify
import logging
from flask import current_app as app
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.services.task_service import get_task_logic, complete_task_logic, vote_logic, generate_survival_tasks_for_all_challenges

# Blueprint für alle Aufgaben-Routen
task_bp = Blueprint("task", __name__)



@task_bp.route("/api/task", methods=["GET"])
@jwt_required()
def get_tasks():

    result = get_task_logic(get_jwt_identity())

    if not result["success"]:
        return jsonify({"error": result["error"]}), 400
    return jsonify({"message": result["data"]}), 200


@task_bp.route("/api/task", methods=["POST"])
@jwt_required()
def complete_task():
    try:
        user_id = get_jwt_identity()

        #Zugriff auf Query Parameter
        erfuellung_id = request.args.get("erfuellung_id")

        #Zugriff auf Text aus Multipart
        description = request.form.get("description")

        #Zugriff auf Datei
        video_file = request.files.get("verification")

        #Logging
        logging.info(f"description: {description}")
        logging.info(f"video_file: {video_file.filename if video_file.filename else None}")

        result = complete_task_logic(erfuellung_id, user_id, description, video_file)

        if not result["success"]:
            return jsonify({"error": result["error"]}), 400

        return jsonify({"message": result}), 201

    except Exception as e:
        logging.exception("Fehler beim Hochladen des Beitrags:")
        return jsonify({
            "success": False,
            "data": None,
            "error": str(e)
        }), 500


@task_bp.route('/api/vote', methods=["POST"])
@jwt_required()
def vote():
    current_user_id = get_jwt_identity()
    beitrag_id = request.args.get("beitrag_id")

    vote = request.json.get('vote')

    if vote is None:
        return jsonify({"error": "Vote cannot be empty"}), 400

    #Hier prüfen, ob Vote noch aussteht
    result = vote_logic(current_user_id, beitrag_id, vote)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 400

    return jsonify({"message": result["data"]}), 200


@task_bp.route('/api/survivaltasks', methods=["GET"])
def create_survivaltasks_manual():
    """Diese Route ist nur zu Testzwecken vorhanden - Im Livebetrieb übernimmt der Scheduler den Job,
    jeden Morgen um 07:00Uhr alle Tasks zu erzeugen."""

    result = generate_survival_tasks_for_all_challenges()

    if not result:
        return jsonify({"message": result["error"]}), 400
    return jsonify({"message": result["data"]}), 201