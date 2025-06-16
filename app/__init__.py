from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_jwt_extended import JWTManager
from datetime import timedelta

db = SQLAlchemy()
jwt = JWTManager()  # JWTManager global verfügbar machen

def create_app():
    app = Flask(__name__)

    # DB-Konfiguration
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///app.db'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

    # JWT-Konfiguration
    app.config["JWT_SECRET_KEY"] = "supergeheimes-passwort"  # Ändere das für Produktion!
    app.config["JWT_TOKEN_LOCATION"] = ["headers"]
    app.config["JWT_HEADER_NAME"] = "Authorization"
    app.config["JWT_HEADER_TYPE"] = "Bearer"
    app.config["JWT_ACCESS_TOKEN_EXPIRES"] = timedelta(hours=4)

    db.init_app(app)
    jwt.init_app(app)  # JWT mit App verbinden

    # Blueprint registrieren
    from app.routes.user_routes import user_bp
    from app.routes.group_routes import group_bp
    # from app.routes.challenge_routes import challenge_bp
    # from app.routes.feed_routes import feed_bp
    # from app.routes.task_routes import task_bp
    # from app.routes.membership_routes import membership_bp
    app.register_blueprint(user_bp)
    app.register_blueprint(group_bp)
    # app.register_blueprint(challenge_bp)
    # app.register_blueprint(feed_bp)
    # app.register_blueprint(task_bp)
    # app.register_blueprint(membership_bp)

    return app
