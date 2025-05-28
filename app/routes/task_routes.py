from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from services.user_service import *

# Blueprint für alle Aufgaben-Routen
task_bp = Blueprint("task", __name__)

@task_bp.route("/api/tasks/<int:id>", methods=["GET"])
@jwt_required()
def get_task(id):
    current_user_id = get_jwt_identity()

    success, result = get_task_logic(id, current_user_id)

    if not success:
        return jsonify({"error": result}), 404

    return jsonify({"task": result}), 200

@task_bp.route("/api/tasks/<int:id>/complete", methods=["POST"])
@jwt_required()
def complete_task(taskid):
    current_user_id = get_jwt_identity()

    success, result = complete_task_logic(taskid, current_user_id)

    if not success:
        return jsonify({"error": result}), 400

    return jsonify({"message": result}), 201

@task_bp.route('/api/vote/<int:id>', methods=["POST"])
@jwt_required()
def vote(id):
    current_user_id = get_jwt_identity()

    vote = request.json.get('vote')

    if vote is None:
        return jsonify({"error": result}), 400

    #Hier prüfen, ob Vote noch aussteht
    success, result = vote_logic(current_user_id, id, vote)

    if not success:
        return jsonify({"error": result}), 400

    return jsonify({"message": result}), 200


