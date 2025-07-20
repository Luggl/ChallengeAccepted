import atexit
from datetime import timedelta
from flask import Flask, request, Response
from flask_sqlalchemy import SQLAlchemy
from flask_jwt_extended import JWTManager
from app.utils.scheduler_instance import scheduler
from app.services.schedule import run_daily_survival_task
import logging

db = SQLAlchemy()
jwt = JWTManager()  # JWTManager global verfügbar machen

#Für den Logout werden die erzeugten Tokens zur Auth. in eine Blacklist gespeichert!
blacklisted_tokens = set()

@jwt.token_in_blocklist_loader
def check_if_token_revokes(jwt_header, jwt_payload):
    jti = jwt_payload['jti'] # JTI = JWT ID (einzigartige Kennung)
    return jti in blacklisted_tokens

def create_app():
    app = Flask(__name__)

    # 200MB Videoupload möglich!
    app.config['MAX_CONTENT_LENGTH'] = 200 * 1024 * 1024

    # DB-Konfiguration
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///app.db'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

    # JWT-Konfiguration
    app.config["JWT_SECRET_KEY"] = "supergeheimes-passwort"  # Ändere das für Produktion!
    app.config["JWT_TOKEN_LOCATION"] = ["headers"]
    app.config["JWT_HEADER_NAME"] = "Authorization"
    app.config["JWT_HEADER_TYPE"] = "Bearer"
    app.config["JWT_ACCESS_TOKEN_EXPIRES"] = timedelta(hours=12)        # 12 Stunden Gültigkeit des Login-Tokens
    app.config["JWT_BLACKLIST_ENABLED"] = True
    app.config["JWT_BLACKLIST_TOKEN_CHECKS"] = ["access", "refresh"]

    db.init_app(app)
    jwt.init_app(app)  # JWT mit App verbinden

    # Blueprint registrieren
    from app.routes.user_routes import user_bp
    from app.routes.group_routes import group_bp
    from app.routes.challenge_routes import challenge_bp
    from app.routes.feed_routes import feed_bp
    from app.routes.task_routes import task_bp
    from app.routes.membership_routes import membership_bp
    from app.routes.scheduler_route import scheduler_bp
    app.register_blueprint(user_bp)
    app.register_blueprint(group_bp)
    app.register_blueprint(challenge_bp)
    app.register_blueprint(feed_bp)
    app.register_blueprint(task_bp)
    app.register_blueprint(membership_bp)
    app.register_blueprint(scheduler_bp)

    scheduler.add_job(func=run_daily_survival_task, trigger="cron", hour=7,
                      minute=0)  # Jeden Tag um 07:00 werden die Survival Tasks erzeugt
    scheduler.start()
    atexit.register(lambda: scheduler.shutdown())  # gägngige Praxis: Bei App-Ende wird der Scheduler deaktiviert

    # Logging konfigurieren
    logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')

    @app.before_request
    def log_request_info():
        logging.info(f"Route aufgerufen: {request.method} {request.path}")
        logging.info(f"Request args: {request.args}")
        logging.info(f"Request JSON: {request.get_json(silent=True)}")

    @app.after_request
    def log_response_info(response: Response):
        if response.content_type == "application/json":
            try:
                response_data = response.get_data(as_text=True)
                app.logger.info(f"Raw JSON response: {response_data}")
            except Exception as e:
                app.logger.warning(f"JSON-Auslesen fehlgeschlagen: {e}")
        else:
            app.logger.info(f"Non-JSON response: {response.status_code} {response.content_type}")
        return response

    return app
