from flask import Flask

app = Flask(__name__)

@app.route('/api/test', methods=['GET'])
def test():
    return 'test was called'