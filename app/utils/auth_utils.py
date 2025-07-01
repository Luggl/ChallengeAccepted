import uuid


def get_uuid_formated_id(stringValue):
    """User- oder Group-ID lesen und als bytes zurückgeben. Bei Fehler: None."""
    try:
        return uuid.UUID(stringValue).bytes
    except ValueError:
        return None

def get_uuid_formated_string(value):
    try:
        return str(uuid.UUID(bytes=value))
    except ValueError:
        return None