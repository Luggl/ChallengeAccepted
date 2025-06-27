from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from services.task_service import get_task_logic, complete_task_logic, vote_logic, generate_survival_tasks_for_all_challenges

# Blueprint für alle Aufgaben-Routen
task_bp = Blueprint("task", __name__)



@task_bp.route("/api/task", methods=["GET"])
@jwt_required()
def get_tasks():

    result = get_task_logic(get_jwt_identity())

    if not result["success"]:
        return jsonify({"error": result["error"]}), 400
    return jsonify({"message": result}), 200


@task_bp.route("/api/task", methods=["POST"])
@jwt_required()
def complete_task():
    user_id = get_jwt_identity()
    erfuellung_id = request.args.get("erfuellung_id")
    description = request.form.get("description")
    video_file = request.files.get("verification")

    result = complete_task_logic(erfuellung_id, user_id, description, video_file)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 400

    return jsonify({"message": result}), 201

@task_bp.route('/api/vote', methods=["POST"])
@jwt_required()
def vote():
    current_user_id = get_jwt_identity()
    beitrag_id = request.json.get("beitrag_id")

    vote = request.json.get('vote')

    if vote is None:
        return jsonify({"error": "Vote cannot be empty"}), 400

    #Hier prüfen, ob Vote noch aussteht
    result = vote_logic(current_user_id, beitrag_id, vote)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 400

    return jsonify({"message": result}), 200


@task_bp.route('/api/survivaltasks', methods=["GET"])
def create_survivaltasks():
    result = generate_survival_tasks_for_all_challenges()

    if not result:
        return jsonify({"message": result}), 400
    return jsonify({"message": result}), 201