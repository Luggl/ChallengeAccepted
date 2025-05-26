from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

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
    success, result = register_user_logic(username, email, password)

    if not success:
        return jsonify({"error": result}), 400

    return jsonify({"message": "User erfolgreich registriert", "user": result}), 201

@user_bp.route('/api/login', methods=['POST'])
def login_user():
    data = request.get_json()
    email = data.get('email')
    password = data.get('password')

    if email is None or password is None:
        return jsonify({"error": "Email und Password sind erforderlich"}), 400

    # Hier Methode einbinden aus Services
    success, result = login_user_logic(email, password)
    if not success:
        return jsonify({"error": result}), 401 # Nicht authorisiert!

    return jsonify({"message": "Login erfolgreich", "user": result}), 200

@user_bp.route('/api/password-reset', methods=['POST'])
def forgot_password():
    data = request.get_json()
    email = data.get('email')

    if email is None:
        return jsonify({"error": "Email ist erforderlich"}), 400

    # Wieder Einbinden der Logik aus services (Hier wird allerdings nur ein Link an die E-Mail des Users gesendet, falls vorhanden!)
    success, message = forgot_password_logic(email)

    if not success:
        return jsonify({"error": message}), 404

    return jsonify({"message": message})

@user_bp.route('/api/reset-password/confirm', methods=['POST'])
def reset_password():


@user_bp.route('/api/users/<int:id>', methods=['DELETE'])
@jwt_required() # Sicherstellen, dass User eingeloggt ist
def delete_user(id):
    # Prüfen, wer der aktuell eingeloggte User ist
    current_user_id = get_jwt_identity()

    # Sicherstellen, dass kein anderer User gelöscht wird außer sich selbst.
    if current_user_id != id:
        return jsonify({"error": "Du darfst nur deinen eigenen Account löschen!"}), 404

    # Logik in Services:
    success, message = delete_user_logic(id)

    if not success:
        return jsonify({"error": message}), 404

    return jsonify({"message": message})

