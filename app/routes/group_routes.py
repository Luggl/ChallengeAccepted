from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from services.group_service import *

# Blueprint für alle Gruppenfunktionen
group_bp = Blueprint('group', __name__)

@group_bp.route('/api/group', methods=['POST'])
@jwt_required()
def create_group():
    #Sicherstellen wer der User ist
    current_user_id = str(uuid.UUID(get_jwt_identity())) #JWT_Token von String in UUID Format geswitcht und dann wieder in String
    # Daten in JSON Format auslesen
    data = request.get_json()

    name = data.get('name')
    beschreibung = data.get('beschreibung')
    gruppenbild = data.get('gruppenbild')

    if name is None:
        return jsonify({"error": "Gruppenname ist erforderlich!"}), 400

    # Service Logik Methode
    result = create_group_logic(name, beschreibung, gruppenbild, created_by = current_user_id)

    if not result['success']:
        return jsonify({"error": result}), 400

    return jsonify({"message": "Gruppe erstellt", "gruppe": result["data"]}), 201

@group_bp.route('/api/invitationlink', methods=['GET'])
@jwt_required()
def invitation_link():
    current_user_id = uuid.UUID(get_jwt_identity()).bytes

    # Gruppe-ID aus den Query-Parametern holen
    gruppe_id_str = request.args.get('gruppe_id')

    if not gruppe_id_str:
        return jsonify({"error": "gruppe_id ist erforderlich!"}), 400

    gruppe_id = uuid.UUID(gruppe_id_str).bytes

    result = invitation_link_logic(gruppe_id, current_user_id)

    if not result['success']:
        return jsonify({"error": result["data"]}), 400

    return jsonify({"message": "Einladungslink erstellt", "link": result}), 200

@group_bp.route('/api/group', methods=['PUT'])
@jwt_required()
def join_group_via_link():
    user_id = get_jwt_identity()
    invitation_link = request.args.get('invitationLink')

    result = join_group_via_link_logic(user_id, invitation_link)

    if not result["success"]:
        return jsonify({"error": result}), 400

    return jsonify({"message": result}), 200

@group_bp.route('/api/group', methods=['DELETE'])
@jwt_required()
def delete_group():
    current_user_id = get_jwt_identity()        # aktuell eingeloggter User prüfen
    group_id = request.args.get('group_id')

    # Logik in Services erhält sowohl die Group-ID, als auch die User_Id um zu prüfen, ob Löschaufruf erlaubt
    result = delete_group_logic(group_id, current_user_id)

    if not result["success"]:
        return jsonify({"error": result}), 403 # Keine Berechtigung oder Fehler

    return jsonify({"message": result}), 204


@group_bp.route('/api/groupfeed/<int:gid>', methods=['GET'])
@jwt_required()
def get_group_feed(gid):
    current_user_id = get_jwt_identity()

    success, result = get_group_feed_logic(gid, current_user_id)

    if not success:
        return jsonify({"error": result}), 403

    return jsonify({"message": result}), 200

@group_bp.route('/api/groups')
@jwt_required()
def get_group_overview():
    current_user_id = get_jwt_identity()

    # Achtung, hier muss im result auch eine Information mitgeliefert werden, ob der User eine Aufgabe zu erledigen hat oder nicht
    # Genauso ob die Aufgabe Standard oder Survival Challenge bezogen ist
    success, result = get_group_overview_logic(current_user_id)

    if not success:
        return jsonify({"error": result}), 403

    return jsonify({"message": result}), 200
