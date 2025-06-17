def send_password_reset_mail(email, token):
    """
    Simuliert den Versand einer E-Mail mit einem Reset-Link für das Passwort.
    Gibt den Link einfach in der Konsole aus.
    """
    reset_link = f"http://localhost:3000/reset-password?token={token}"
    print(f"[DEV-MAIL] Passwort-Zurücksetzen-Link für {email}:")
    print(reset_link)