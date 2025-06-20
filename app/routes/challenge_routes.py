from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from services.challenge_service import (
    create_challenge_standard_logic,
    create_challenge_survival_logic,
    delete_challenge_logic
)
from utils.auth_utils import get_uuid_formated_id

# Blueprint für die Challenge erstellen
challenge_bp = Blueprint('challenge', __name__)


# Split von Challenge Standard und Survival benötigt, da ChallengeSportart unterschiedlich definiert ist (Schwierigkeitsgrad und nicht Intensitätsangaben)
@challenge_bp.route('/api/challengestandard', methods=['POST'])
@jwt_required()       #Sicherstellen, dass User eingeloggt ist!
def create_challenge_standard():
    data = request.get_json()
    current_user_id = get_jwt_identity()

    group_id_str = request.args.get('group_id')
    if not group_id_str:
        return jsonify({"error": "Gruppen-ID (group_id) erforderlich"}), 400

    group_id = get_uuid_formated_id(group_id_str)
    if not group_id:
        return jsonify({"error": "Ungültige Gruppen-ID"}), 400

    result = create_challenge_standard_logic(current_user_id, data, group_id)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 400

    return jsonify({
        "message": "Challenge erfolgreich erstellt",
        "challenge": result["data"]
    }), 201

@challenge_bp.route('/api/challengesurvival', methods=['POST'])
@jwt_required()
def create_challenge_survival():
    data = request.get_json()
    current_user_id = get_jwt_identity()

    # Gruppen-ID aus gid holen
    group_id_str = request.args.get('group_id')
    if not group_id_str:
        return jsonify({"error": "Gruppen-ID (group_id) erforderlich"}), 400

    group_id = get_uuid_formated_id(group_id_str)
    if not group_id:
        return jsonify({"error": "Ungültige Gruppen-ID"}), 400

    result = create_challenge_survival_logic(current_user_id, data, group_id)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 400

    return jsonify({
        "message": "Survival-Challenge erfolgreich erstellt",
        "challenge": result["data"]
    }), 201

@challenge_bp.route("/api/challenge", methods=["DELETE"])
@jwt_required()
def delete_challenge():
    challenge_id = get_uuid_formated_id(request.args.get("challenge_id"))
    if not challenge_id:
        return jsonify({"error": "Ungültige Challenge-ID"}), 400

    user_id = get_uuid_formated_id(get_jwt_identity())
    if not user_id:
        return jsonify({"error": "Ungültige Benutzer-ID"}), 401

    result = delete_challenge_logic(challenge_id, user_id)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 400

    return jsonify({"message": "Challenge gelöscht"}), 200