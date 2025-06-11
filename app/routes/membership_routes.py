from flask import Blueprint, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from services.membership_service import *

# Blueprint für Mitglieder-Verwaltung
membership_bp = Blueprint("membership", __name__)

@membership_bp.route("/api/groups/<int:gid>/memberships/<uuid:uuid>", methods=["DELETE"])
@jwt_required()
def remove_user_from_group(gid, uuid):
    current_user_id = get_jwt_identity()

    success, result = remove_user_logic(gid, uuid, current_user_id)

    if not success:
        return jsonify({"error": result}), 403

    return '', 204
