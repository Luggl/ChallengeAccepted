from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from services.membership_service import remove_user_logic, get_membership_overview_logic


# Blueprint für Mitglieder-Verwaltung
membership_bp = Blueprint("membership", __name__)

@membership_bp.route("/api/group", methods=["DELETE"])
@jwt_required()
def remove_user_from_group():
    current_user_id = get_jwt_identity()
    group_id = request.args.get("group_id")
    kick_user_id = request.args.get("user_id")

    result = remove_user_logic(group_id, kick_user_id, current_user_id)

    if not result["success"]:
        return jsonify({"error": result}), 403

    return jsonify({"message": result}), 204


@membership_bp.route('/api/groups', methods=['GET'])
@jwt_required()
def get_group_overview():
    current_user_id = get_jwt_identity()

    # Achtung, hier muss im result auch eine Information mitgeliefert werden, ob der User eine Aufgabe zu erledigen hat oder nicht
    # Genauso ob die Aufgabe Standard oder Survival Challenge bezogen ist
    result = get_membership_overview_logic(current_user_id)

    if not result["success"]:
        return jsonify({"error": result["error"]}), 403
    return jsonify({"message": result["data"]}), 200