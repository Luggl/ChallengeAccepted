import uuid

from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

from services.aufgaben_service import generate_standard_tasks_for_challenge_logic, get_task_by_date, generate_survival_task
from services.task_service import *
from repositories.task_repository import find_task_by_id

# Blueprint für alle Aufgaben-Routen
task_bp = Blueprint("task", __name__)


@task_bp.route('/api/challenge/<challenge_id>/generate-tasks', methods=['POST'])
@jwt_required()
def route_generate_standard_tasks(challenge_id):
    try:
        challenge_id = uuid.UUID(challenge_id).bytes
    except ValueError:
        return {"success": False, "error": "Ungültige Challenge-ID"}, 400

    result = generate_standard_tasks_for_challenge_logic(challenge_id)
    return result, 200 if result["success"] else 400

@task_bp.route("/api/challengesurvival/<challenge_id>/task/generate", methods=["POST"])
@jwt_required()
def route_generate_survival_task(challenge_id):
    try:
        challenge_uuid = uuid.UUID(challenge_id)
    except ValueError:
        return {"success": False, "error": "Ungültige Challenge-ID"}, 400

    result = generate_survival_task(challenge_uuid.bytes)

    if not result["success"]:
        return result, 400

    aufgabe_id = uuid.UUID(result["task_id"]).bytes
    aufgabe = find_task_by_id(aufgabe_id)

    task_data = {
        "id": str(uuid.UUID(bytes=aufgabe.aufgabe_id)),
        "beschreibung": aufgabe.beschreibung,
        "zielwert": aufgabe.zielwert,
        "unit": aufgabe.unit.value,
        "typ": aufgabe.typ,
        "datum": aufgabe.datum.isoformat(),
        "startzeit": aufgabe.startzeit.isoformat(),
        "deadline": aufgabe.deadline.isoformat(),
        "sportart_id": str(uuid.UUID(bytes=aufgabe.sportart_id)),
        "challenge_id": str(uuid.UUID(bytes=aufgabe.challenge_id))
    }

    return {
        "success": True,
        "message": result["message"],
        "task": task_data
    }, 200

@task_bp.route('/api/challenge/<challenge_id>/task', methods=['GET'])
@jwt_required()
def route_get_task_by_date(challenge_id):
    datum = request.args.get("datum")
    if not datum:
        return {"success": False, "error": "Datum fehlt als Query-Parameter"}, 400

    try:
        challenge_uuid = uuid.UUID(challenge_id)
    except ValueError:
        return {"success": False, "error": "Ungültige Challenge-ID"}, 400

    from app import db
    result = get_task_by_date(challenge_uuid, datum)
    return result, 200 if result["success"] else 404


@task_bp.route("/api/task", methods=["GET"])
@jwt_required()
def get_task():

    result = create_tasks_daily()

    if not result["success"]:
        return jsonify({"message": result}), 400
    return jsonify({"message": result}), 200


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


@task_bp.route('/api/survivaltasks', methods=["POST"])
def create_survivaltasks():
    result = generate_survival_tasks_for_all_challenges()

    if not result:
        return jsonify({"message": result}), 400
    return jsonify({"message": result}), 201