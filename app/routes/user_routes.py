from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity, create_access_token, get_jwt
from app.services.user_service import *
from app import blacklisted_tokens


# Blueprint ist eine "Mini-App" innerhalb Flask, um Routen besser zu strukturieren.
user_bp = Blueprint('user', __name__)

@user_bp.route('/api/user', methods=['POST'])
def register_user():
    # Lesen der Daten aus dem Body der Schnittstelle
    data = request.get_json()

    # Einzelne Werte zuweisen
    username = data.get('username')
    email = data.get('email')
    password = data.get('password')

    # Prüfung, ob alle Daten vorhanden:
    if username is None or email is None or password is None:
        return jsonify({"error": "Username, Password und Email sind erforderlich"}), 400

    # Hier die Methode einbinden, die prüft ob Werte i. O.!
    result = register_user_logic(username, email, password)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 400

    return jsonify({
        "message": "User erfolgreich registriert",
        "user": result["data"],
    }), 201

@user_bp.route('/api/login', methods=['POST'])
def login_user():
    data = request.get_json()
    email = data.get('email')
    password = data.get('password')

    if email is None or password is None:
        return jsonify({"error": "Login und Password sind erforderlich"}), 400

    # Hier Methode einbinden aus Services - login kann Username oder E-Mail sein!
    result = login_user_logic(email, password)
    if not result["success"]:
        return jsonify({"error": result}), 401 # Nicht authorisiert!

    # Token erzeugen
    access_token = create_access_token(identity=result["data"]["id"])

    return jsonify({"message": "Login erfolgreich",
                    "user": result["data"],
                    "access_token": access_token}), 200

@user_bp.route('/api/password-reset', methods=['POST'])
def forgot_password():
    data = request.get_json()
    email = data.get('email')

    if not email:
        return jsonify({"error": "Email ist erforderlich"}), 400

    result = forgot_password_logic(email)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 404

    return jsonify({"message": result["data"]}), 200


@user_bp.route('/api/password-reset/confirm', methods=['POST'])
def reset_password():
    data = request.get_json()
    token = data.get('token')
    new_pw = data.get('newPassword')

    if not token or not new_pw:
        return jsonify({"error": "Token und neues Passwort sind erforderlich"}), 400

    result = reset_password_logic(token, new_pw)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 400

    return jsonify({"message": result["data"]}), 200

@user_bp.route('/api/user/<uuid:id>', methods=['DELETE'])
@jwt_required() # Sicherstellen, dass User eingeloggt ist
def delete_user(id):
    # Prüfen, wer der aktuell eingeloggte User ist
    current_user_id = get_jwt_identity()

    # Sicherstellen, dass kein anderer User gelöscht wird außer sich selbst.
    uuid_obj = uuid.UUID(current_user_id)
    if uuid_obj != id:
        return jsonify({"error": "Du darfst nur deinen eigenen Account löschen!"}), 404

    # Logik in Services:
    message = delete_user_logic(id)

    if not message["success"]:
        return jsonify({"error": message}), 404

    return jsonify({"message": message["data"]})

@user_bp.route('/api/user', methods=['GET'])
@jwt_required()
def get_user():
    current_user_id = get_jwt_identity()

    result = get_user_logic(current_user_id)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 404

    return jsonify({"user": result["data"]}), 200

@user_bp.route('/api/user/me', methods=['PATCH'])
@jwt_required()
def update_user():
    current_user_id = get_jwt_identity()
    update_data = request.get_json()

    if not update_data:
        return jsonify({"error": "Keine Daten übergeben."}), 400

    result = update_user_logic(current_user_id, update_data)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 404

    return jsonify({"message": result["data"]}), 200


@user_bp.route('/api/user/password', methods=['PATCH'])
@jwt_required()
def update_password():
    current_user_id = get_jwt_identity()

    old_pw = request.json.get('oldPassword')
    new_pw = request.json.get('newPassword')

    if not old_pw or not new_pw:
        return jsonify({"error": "Altes und neues Passwort sind erforderlich"}), 400

    result = update_password_logic(current_user_id, old_pw, new_pw)

    if not result["success"]:
        return jsonify({"error": result}), 404

    return jsonify({"message": result}), 200

@user_bp.route('/api/logout', methods=['POST'])
@jwt_required()
def logout():
    jti = get_jwt()["jti"]
    blacklisted_tokens.add(jti)
    return jsonify({"message": "Logged out"}), 200
