import re


def is_password_strong(password):
    if len(password) < 10:
        return False, "Das Passwort muss mindestens 10 Zeichen lang sein."
    if not re.search(r"[A-Z]", password):
        return False, "Das Passwort muss mindestens einen Großbuchstaben enthalten."
    if not re.search(r"[a-z]", password):
        return False, "Das Passwort muss mindestens einen Kleinbuchstaben enthalten."
    if not re.search(r"[0-9]", password):
        return False, "Das Passwort muss mindestens eine Zahl enthalten."
    if not re.search(r"[!@#$%^&*(),.?\":{}|<>]", password):
        return False, "Das Passwort muss mindestens ein Sonderzeichen enthalten."

    return True