import uuid

from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

from services.task_service import generate_standard_tasks_for_challenge_logic, get_task_by_date, generate_survival_tasks_for_all_challenges
from services.task_service import *
from repositories.task_repository import find_task_by_id
from utils.auth_utils import get_uuid_formated_id

# Blueprint für alle Aufgaben-Routen
task_bp = Blueprint("task", __name__)



@task_bp.route("/api/task", methods=["GET"])
@jwt_required()
def get_tasks():

    result = get_task_logic(get_jwt_identity())

    if not result["success"]:
        return jsonify({"message": result}), 400
    return jsonify({"message": result}), 200


@task_bp.route("/api/task", methods=["POST"])
@jwt_required()
def complete_task():
    current_user_id_str = get_uuid_formated_id(get_jwt_identity())
    erfuellung_id_str = get_uuid_formated_id(request.json.get("erfuellung_id"))
    video_file = request.files.get("verification")
    description = request.get_json().get("description")

    result = complete_task_logic(erfuellung_id_str, current_user_id_str, description, video_file)

    if not result["success"]:
        return jsonify({"error": result}), 400

    return jsonify({"message": result}), 201

@task_bp.route('/api/vote', methods=["POST"])
@jwt_required()
def vote():
    current_user_id = get_jwt_identity()
    task_id = request.json.get("task_id")

    vote = request.json.get('vote')

    if vote is None:
        return jsonify({"error": "Vote cannot be empty"}), 400

    #Hier prüfen, ob Vote noch aussteht
    result = vote_logic(current_user_id, task_id, vote)

    if not result["success"]:
        return jsonify({"error": result}), 400

    return jsonify({"message": result}), 200


@task_bp.route('/api/survivaltasks', methods=["GET"])
def create_survivaltasks():
    result = generate_survival_tasks_for_all_challenges()

    if not result:
        return jsonify({"message": result}), 400
    return jsonify({"message": result}), 201