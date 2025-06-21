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

def serialize_gruppe(gruppe):
    return{
        "gruppe_id": gruppe.gruppe_id.hex(),
        "gruppenname": gruppe.gruppenname,
        "beschreibung": gruppe.beschreibung,
        "gruppenbild": gruppe.gruppenbild,
        "erstellt_am": gruppe.erstellungsDatum.isoformat(),
    }

def serialize_aufgabenerfuellung(aufgabenerfuellung):
    return{
        "aufgabe_id": aufgabenerfuellung.aufgabe_id.hex(),
        "user_id": aufgabenerfuellung.user_id.hex(),
        "gruppe_id": aufgabenerfuellung.gruppe_id.hex(),
        "status": aufgabenerfuellung.status.name,
        "beschreibung": aufgabenerfuellung.aufgabe.beschreibung,
        "zielwert": aufgabenerfuellung.aufgabe.zielwert,
        "unit": aufgabenerfuellung.aufgabe.unit.name,
        "dauer": aufgabenerfuellung.aufgabe.dauer,
        "deadline": aufgabenerfuellung.aufgabe.deadline,
        "datum": aufgabenerfuellung.aufgabe.datum,

    }