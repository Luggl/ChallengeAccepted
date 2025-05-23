from app import create_app  # Holt die Flask-App aus app/__init__.py

app = create_app()          # Erstellt eine App-Instanz

if __name__ == "__main__":  # Nur ausführen, wenn Datei direkt gestartet wird
    app.run(debug=True)     # Startet Flask-Server lokal im Debug-Modus