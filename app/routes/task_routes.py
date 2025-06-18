from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from services.task_service import *

# Blueprint für alle Aufgaben-Routen
task_bp = Blueprint("task", __name__)

@task_bp.route("/api/task", methods=["GET"])
@jwt_required()
def get_task():
    current_user_id = get_jwt_identity()
    task_id = request.args.get("task_id")

    result = get_task_logic(task_id, current_user_id)

    if not result["success"]:
        return jsonify({"error": result}), 404

    return jsonify({"task": result}), 200

@task_bp.route("/api/task", methods=["POST"])
@jwt_required()
def complete_task():
    current_user_id = get_jwt_identity()
    task_id = request.json.get("task_id")

    result = complete_task_logic(task_id, current_user_id)

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


