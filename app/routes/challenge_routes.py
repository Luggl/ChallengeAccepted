from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from services.challenge_service import *

# Blueprint für die Challenge erstellen
challenge_bp = Blueprint('challenge', __name__)


# Split von Challenge Standard und Survival benötigt, da ChallengeSportart unterschiedlich definiert ist (Schwierigkeitsgrad und nicht Intensitätsangaben)
@challenge_bp.route('/api/challengestandard')
@jwt_required()       #Sicherstellen, dass User eingeloggt ist!
def create_challenge_standard():
    data = request.get_json()
    current_user_id = get_jwt_identity()
    gid = request.args.get('gid', type=int)


    success, result = create_challenge_standard_logic(current_user_id, data, gid)

    if not success:
        return jsonify({"error": result}), 400

    return jsonify({"message": "Challenge erstellt", "challenge": result}), 201

@challenge_bp.route('/api/challengesurvival', methods=['POST'])
# @jwt_required()
def create_challenge_survival():
    data = request.get_json()
    # current_user_id = get_jwt_identity()
    gid = request.args.get('gid', type=int)

    ## Nur zum Testen!
    current_user_id = 123

    success, result = create_challenge_survival_logic(current_user_id, data, gid)

    if not success:
        return jsonify({"error": result}), 400

    return jsonify({"message": "Challenge erstellt", "challenge": result}), 201

@challenge_bp.route("/api/challenges/<int:id>", methods=["DELETE"])
@jwt_required()  # Nur eingeloggte Nutzer dürfen Challenges löschen
def delete_challenge(id):
    current_user_id = get_jwt_identity()

    success, result = delete_challenge_logic(id, current_user_id)

    if not success:
        return jsonify({"error": result}), 403

    return '', 204  # Erfolgreich gelöscht, keine Rückgabe nötig