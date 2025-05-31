from flask import Flask, json

from app.routes.user_routes import user_bp #Blueprint aus user_routes importieren



def test_register_user():
    app = Flask(__name__)
    app.config["TESTING"] = True    #Testmodus in der Flask-App
    app.register_blueprint(user_bp)

    client = app.test_client()
    userdata = {        #Userdaten simulieren
    "username": "tobi",
    "email": "tobi@mail.de",
    "password": "123456"
    }

    response = client.post('/api/user', data=json.dumps(userdata), content_type="application/json")
    print(response.json)