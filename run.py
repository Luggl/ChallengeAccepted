from app import create_app  # Holt die Flask-App aus app/__init__.py
from flask import request, jsonify
import logging

app = create_app()          # Erstellt eine App-Instanz

#Logging konfigurieren
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')

@app.before_request
def log_request_info():
    logging.info(f"Route aufgerufen: {request.method} {request.path}")
    logging.info(f"Request args: {request.args}")
    logging.info(f"Request JSON: {request.get_json(silent=True)}")

@app.after_request
def log_request_info(response):
    try:
        data = response.get_json()
        logging.info(f"Response: success={data.get('success')}, error={data.get('error')}")
    except Exception:
        logging.warning("Response konnte nicht als JSON gelesen werden.")
    return response

if __name__ == "__main__":  # Nur ausführen, wenn Datei direkt gestartet wird
    app.run(debug=True, host='0.0.0.0', port=5000)     # Startet Flask-Server lokal im Debug-Modus