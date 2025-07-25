import os
import subprocess
import uuid
from pathlib import Path, PurePosixPath
from werkzeug.utils import secure_filename
from app.utils.response import response

if os.name == "nt": #Windows
    BASE_DIR = Path(__file__).resolve().parent
    UPLOAD_ROOT = BASE_DIR / "media" / "aufgabenerfuellung"
else: #Linux
    BASE_DIR = Path("/")
    UPLOAD_ROOT = BASE_DIR / "media" / "aufgabenerfuellung"

SERVER_URL = "http://138.199.220.111"

def safe_video_logic(task_id, video_file):
    filename = secure_filename(f"{uuid.uuid4()}.mp4")
    task_id_str = str(uuid.UUID(bytes=task_id))

    upload_path = UPLOAD_ROOT / task_id_str
    upload_path.mkdir(parents=True, exist_ok=True)      #Upload_Path erzeugen, falls noch nicht existent

    full_path = upload_path / filename
    absolute_path = str(full_path.resolve())
    try:
        # Temporäre Datei abspeichern
        temp_path = full_path.with_suffix(".temp.mp4")
        video_file.save(str(temp_path))

        # Mit ffmpeg streambar machen (moov atom an Anfang setzen)
        # -c copy = keine Neucodierung, nur Metadaten umbauen
        streamable_path = full_path
        subprocess.run([
            "ffmpeg", "-i", str(temp_path),
            "-movflags", "faststart",
            "-c", "copy",
            str(streamable_path)
        ], check=True)

        # Temporäre Datei löschen
        temp_path.unlink(missing_ok=True)


        #Rückgabe einer URL, nicht eines Dateipfads
        relative_url = "/" + str(PurePosixPath(full_path.relative_to(BASE_DIR)))
        absolute_url = SERVER_URL + relative_url
        return response(True, data={"path": absolute_path, "url": absolute_url})
    except subprocess.CalledProcessError as ffmpeg_error:
        return response(False, error=f"FFmpeg-Fehler: {ffmpeg_error}")
    except Exception as e:
        return response(False, error="Fehler beim Speichern des Videos!")


def generate_video_thumbnail(video_path):
    video_path = Path(video_path)           # Path-String wird als Path-Objekt behandelt
    video_folder = video_path.parent
    thumbnail_filename = f"{uuid.uuid4()}.jpg"
    thumbnail_path = video_folder / thumbnail_filename

    # Nimm Frame bei 00:00:01 - Genutzt wird ffmpeg
    command = [
        "ffmpeg",
        "-i", str(video_path),
        "-ss", "00:00:01.000",
        "-vframes", "1",
        str(thumbnail_path),
    ]

    try:
        subprocess.run(command, check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

        try:
            relative_url = "/" + str(PurePosixPath(thumbnail_path.relative_to(BASE_DIR)))
        except ValueError:
            # Fallback, falls Pfad nicht relativ zu BASE_DIR ist
            relative_url = "/" + thumbnail_path.as_posix()

        absolute_url = SERVER_URL + relative_url

        return response(True, data={"path": str(thumbnail_path), "url": absolute_url})

    except subprocess.CalledProcessError:
        return response(False, error="Thumbnail konnte nicht erstellt werden")
