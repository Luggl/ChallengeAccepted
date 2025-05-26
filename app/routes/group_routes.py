from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

# Blueprint für alle Gruppenfunktionen
group_bp = Blueprint('group', __name__)

@group_bp.route('/api/groups', methods=['POST'])
def create_group():
    # Daten in JSON Format auslesen
    data = request.get_json()

    name = data.get('name')
    beschreibung = data.get('beschreibung')
    gruppenbild = data.get('gruppenbild')

    if name is None:
        return jsonify({"error": "Gruppenname ist erforderlich!"}), 400

    # Service Logik Methode
    success, result = create_group_logic(name, beschreibung, gruppenbild)

    if not success:
        return jsonify({"error": result}), 400

    return jsonify({"message": "Gruppe erstellt", "gruppe": result}), 201

@group_bp.route('/api/invitationlink', methods=['GET'])
def invitation_link():
    success, result = invitation_link_logic()

    if not success:
        return jsonify({"error": result}), 400

    return jsonify({"message": "Einladungslink erstellt", "link": result}), 200

@group_bp.route('/api/groups', methods=['PUT'])
def join_group_via_link():
    user_id = request.args.get('user')
    invitation_link = request.args.get('invitationLink')

    success, result = join_group_via_link_logic(user_id, invitation_link)

    if not success:
        return jsonify({"error": result}), 400

    return jsonify({"message": result}), 200

@group_bp.route('/api/groups/<int:id>', methods=['DELETE'])
@jwt_required()
def delete_group(id):
    current_user_id = get_jwt_identity()        # aktuell eingeloggter User prüfen

    # Logik in Services erhält sowohl die Group-ID, als auch die User_Id um zu prüfen, ob Löschaufruf erlaubt
    success, result = delete_group_logic(id, current_user_id)

    if not success:
        return jsonify({"error": result}), 403 # Keine Berechtigung oder Fehler

    return jsonify({"message": result}), 204
