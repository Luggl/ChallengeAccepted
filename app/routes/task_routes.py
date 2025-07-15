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
    user_id = get_jwt_identity()
    erfuellung_id = request.args.get("erfuellung_id")
    description = request.form.get("description")
    video_file = request.files.get("verification")
    app.logger.info(f"API /api/task called - user_id from token: {user_id}, erfuellung_id: {erfuellung_id}")

    if not user_id:
        app.logger.warning("Missing or invalid JWT token: user_id is None")
        return {"error": "Unauthorized"}, 401

    if not erfuellung_id:
        app.logger.warning(f"user_id {user_id} sent request without erfuellung_id")
        return {"error": "Missing erfuellung_id"}, 400

    if not video_file:
        app.logger.warning(f"user_id {user_id} sent request without verification video file")
        return {"error": "Missing verification file"}, 400

    try:
        result = complete_task_logic(erfuellung_id, user_id, description, video_file)
        app.logger.info(f"Task completed successfully for user_id {user_id}, erfuellung_id {erfuellung_id}")
        return {"success": True}
    except Exception as e:
        app.logger.error(f"Error processing task for user_id {user_id}: {e}", exc_info=True)
        return {"error": "Internal Server Error"}, 500

    #
    # result = complete_task_logic(erfuellung_id, user_id, description, video_file)
    #
    # if not result["success"]:
    #     return jsonify({"error": result["error"]}), 400
    #
    # return jsonify({"message": result}), 201

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