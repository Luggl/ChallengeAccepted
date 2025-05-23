from flask import Blueprint

user_routes = Blueprint('user_routes', __name__)

@user_routes.route('/hello')
def hello_user():
    return "Hello from user_routes!"
