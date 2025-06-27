# from flask import jsonify
# from flask.sansio.blueprints import Blueprint
# from flask_jwt_extended import jwt_required, get_jwt, get_jwt_identity
#
# from routes.challenge_routes import challenge_bp
# from services.achievements_service import get_achievements_logic
#
# achievements_bp = Blueprint('achievement', __name__)
#
# @challenge_bp.route('/api/achievements', methods=['GET'])
# @jwt_required
# def get_achievements():
#     result = get_achievements_logic(get_jwt_identity())
#
#     if not result["success"]:
#         return jsonify({"message": result}), 400
#     return jsonify({"message": result}), 200
#
