from flask import Flask
from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()  # außerhalb der Funktion, damit global verfügbar

def create_app():
    app = Flask(__name__)

    # Konfiguration (z. B. SQLite oder PostgreSQL)
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///app.db'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

    db.init_app(app)

    # Import und Registrierung der Routen
    from app.routes.user_routes import user_bp
    app.register_blueprint(user_bp)

    return app


