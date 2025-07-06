def response(success, data=None, error=None):
    """Standard-Rückgabe-Objekt aus Backend an die Schnittstellen"""
    return {"success": success, "data": data, "error": error}
