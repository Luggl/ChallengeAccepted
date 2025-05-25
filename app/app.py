from flask import Flask

app = Flask(__name__)

@app.route('/api/test', methods=['GET'])
def test():
    return 'test was called'

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=3000, debug=True)