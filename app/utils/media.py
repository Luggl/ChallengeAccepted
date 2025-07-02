import os
import subprocess
import uuid
from pathlib import Path

from werkzeug.utils import secure_filename

from utils.response import response

UPLOAD_ROOT = "media/aufgabenerfuellung"

def safe_video_logic(task_id, video_file):
    filename = secure_filename(f"{uuid.uuid4()}.mp4")
    task_id_str = str(uuid.UUID(bytes=task_id))
    upload_path = os.path.join(UPLOAD_ROOT, task_id_str)
    os.makedirs(upload_path, exist_ok=True)

    full_path = os.path.join(upload_path, filename)
    try:
        video_file.save(full_path)
        return response(True, data=full_path)
    except Exception as e:
        return response(False, error="Fehler beim Speichern des Videos!")


def generate_video_thumbnail(video_path):
    video_path = Path(video_path)
    video_folder = video_path.parent
    thumbnail_filename = f"{uuid.uuid4()}.jpg"
    thumbnail_path = video_folder / thumbnail_filename

    #Nimm Frame bei 00:00:01
    command = [
        "ffmpeg",
        "-i", str(video_path),
        "-ss", "00:00:01.000",
        "-vframes", "1",
        str(thumbnail_path)
    ]

    try:
        subprocess.run(command, check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        return str(thumbnail_path)
    except subprocess.CalledProcessError:
        return response(False, error= "Thumbnail konnte nicht erstellt werden")