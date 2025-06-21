# um Videoformat per JSON über Flask zurückzugeben
def serialize_beitrag(beitrag):
    return{
        "beitrag_id": beitrag.beitrag_id.hex(),
        "beschreibung": beitrag.beschreibung,
        "erstellt_am": beitrag.erstellDatum.isoformat(),
        "user_id": beitrag.user_id.hex(),
        "gruppe_id": beitrag.gruppe_id.hex(),
        "video_url": f"/media/{beitrag.video_path}" if beitrag.video_path else None
    }