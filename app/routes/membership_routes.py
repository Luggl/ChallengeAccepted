from flask import Blueprint, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

# Blueprint für Mitglieder-Verwaltung
membership_bp = Blueprint("membership", __name__)

@membership_bp.route("/api/groups/<int:gid>/memberships/<uuid:uuid>/promote", methods=["PUT"])
@jwt_required()
def promote_user(gid, uuid):
    current_user_id = get_jwt_identity()

    group_id = gid,
    target_user_uuid = uuid,
    requester_id = current_user_id

    success, result = promote_user_logic(gid, uuid, current_user_id)

    if not success:
        return jsonify({"error": result}), 403

    return jsonify({"message": result}), 200

@membership_bp.route("/api/groups/<int:gid>/memberships/<uuid:uuid>/degrade", methods=["PUT"])
@jwt_required()
def degrade_user(gid, uuid):
    current_user_id = get_jwt_identity()
    group_id = gid,
    target_user_uuid = uuid,
    requester_id = current_user_id

    success, result = degrade_user_logic(gid, uuid, current_user_id)

    if not success:
        return jsonify({"error": result}), 403

    return jsonify({"message": result}), 200


@membership_bp.route("/api/groups/<int:gid>/memberships/<uuid:uuid>", methods=["DELETE"])
@jwt_required()
def remove_user_from_group(gid, uuid):
    current_user_id = get_jwt_identity()

    group_id = gid,
    target_user_uuid = uuid,
    requester_id = current_user_id

    success, result = remove_user_logic(gid, uuid, current_user_id)

    if not success:
        return jsonify({"error": result}), 403

    return '', 204
