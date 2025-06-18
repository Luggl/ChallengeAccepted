from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from services.membership_service import *

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
