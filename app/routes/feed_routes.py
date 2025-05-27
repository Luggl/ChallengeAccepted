from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from services.user_service import *

# Blueprint für Feed-Funktionen
feed_bp = Blueprint("feed", __name__)

@feed_bp.route("/api/feed", methods=["GET"])
@jwt_required()
def get_feed():
    current_user_id = get_jwt_identity()

    success, result = get_feed_logic(current_user_id)

    if not success:
        return jsonify({"error": result}), 400

    return jsonify({"feed": result}), 200

@feed_bp.route("/api/posts/<int:id>", methods=["PUT"])
@jwt_required()
def edit_post(id):
    current_user_id = get_jwt_identity()
    data = request.get_json()
    post_id = id

    success, result = update_post_logic(data, post_id, current_user_id)

    if not success:
        return jsonify({"error": result}), 403

    return jsonify({"message": result}), 200