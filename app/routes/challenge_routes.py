from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity


# Blueprint für die Challenge erstellen
challenge_bp = Blueprint('challenge', __name__)

@challenge_bp.route('/api/challenges')
@jwt_required       #Sicherstellen, dass User eingeloggt ist!
def create_challenge():
    data = request.get_json()
    current_user_id = get_jwt_identity()

    success, result = create_challenge_logic(current_user_id, data)

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