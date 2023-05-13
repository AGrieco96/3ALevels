from flask import Flask, request, jsonify
from firebase_admin import credentials, firestore, initialize_app
from imageai.Classification import ImageClassification
import os
import base64
import datetime

# Initialize Flask App
app = Flask(__name__)

# Initialize Firestore DB
cred = credentials.Certificate('./key.json')
default_app = initialize_app(cred)
db = firestore.client()
games_collection = db.collection('games')
lobby_collection = db.collection('lobbies')


@app.route('/update-game', methods=['PUT'])
def updateGame():
    try:
        lobby_id = request.json['lobby_id']
        player_id = request.json['player_id']
        level = request.json['level']
        time_score = request.json['time']
        field_string_time = player_id + '.time.' + level

        doc_game = games_collection.document(lobby_id)
        doc_game.update({
            field_string_time: time_score
        })

        doc_lobby = lobby_collection.document(lobby_id)
        doc_l = doc_lobby.get()
        if player_id != str(doc_l.to_dict()['player_1']):
            other_player = str(doc_l.to_dict()['player_1'])
        else:
            other_player = str(doc_l.to_dict()['player_2'])

        doc_g = doc_game.get()
        time_other = doc_g.to_dict()[other_player]['time'][level]

        if time_other != 0:
            field_string_counter_player = player_id + '.counter'
            field_string_counter_other_player = other_player + '.counter'
            counter_player = doc_g.to_dict()[player_id]['counter'] + 1
            counter_other_player = doc_g.to_dict()[other_player]['counter'] + 1
            if time_other <= time_score:
                doc_game.update({
                    field_string_counter_other_player: counter_other_player
                })
            else:
                doc_game.update({
                    field_string_counter_player: counter_player
                })
        return jsonify({"success": True}), 200
    except Exception as e:
        return f"An Error Occurred: {e}"


@app.route('/new-game', methods=['POST'])
def createGame():
    try:
        lobby_id = request.json['lobby_id']

        doc_lobby = lobby_collection.document(lobby_id)
        doc = doc_lobby.get()
        username_player1 = str(doc.to_dict()['player_1'])
        username_player2 = str(doc.to_dict()['player_2'])

        new_game = {
            username_player1: {
                u'counter': 0,
                u'lobby_id': lobby_id,
                u'time': {
                    u'level1': 0,
                    u'level2': 0,
                    u'level3': 0,
                    u'level4': 0,
                    u'level5': 0
                },
            },
            username_player2: {
                u'counter': 0,
                u'lobby_id': lobby_id,
                u'time': {
                    u'level1': 0,
                    u'level2': 0,
                    u'level3': 0,
                    u'level4': 0,
                    u'level5': 0
                }
            }
        }
        games_collection.document(lobby_id).set(new_game)
        return jsonify({"success": True}), 200
    except Exception as e:
        return f"An Error Occurred: {e}"


@app.route('/ai', methods=['POST'])
def image_recognition():
    try:
        base64_image_string = request.json['image']
        object_str = request.json['object']
        execution_path = os.getcwd()
        prediction = ImageClassification()
        prediction.setModelTypeAsMobileNetV2()
        prediction.setModelPath(os.path.join(execution_path, "mobilenet_v2-b0353104.pth"))
        prediction.loadModel()

        timestamp = str(datetime.datetime.now())
        content = base64.b64decode(base64_image_string)
        with open('image.jpg' + timestamp, 'wb') as f:
            f.write(content)

        predictions, probabilities = prediction.classifyImage(
            os.path.join(execution_path, "image.jpg" + timestamp), result_count=10)
        string_list = []
        for s in predictions:
            if ' ' in s:
                for w in s.split():
                    string_list.append(w)
            else:
                string_list.append(s)

        if object_str in string_list:
            response = "Good job! You have take a photo of a " + object_str
        else:
            response = "Oh no! You have take a photo of a " + predictions[0]
        return jsonify(response), 200
    except Exception as e:
        return f"An Error Occurred: {e}"


@app.route('/list', methods=['GET'])
def read():
    """
        read() : Fetches documents from Firestore collection as JSON
        todo : Return document that matches query ID
        all_todos : Return all documents
    """
    try:
        # Check if ID was passed to URL query
        todo_id = request.args.get('id')
        if todo_id:
            todo = games_collection.document(todo_id).get()
            return jsonify(todo.to_dict()), 200
        else:
            all_todos = [doc.to_dict() for doc in games_collection.stream()]
            return jsonify(all_todos), 200
    except Exception as e:
        return f"An Error Occurred: {e}"


@app.route('/delete', methods=['GET', 'DELETE'])
def delete():
    """
        delete() : Delete a document from Firestore collection
    """
    try:
        # Check for ID in URL query
        todo_id = request.args.get('id')
        games_collection.document(todo_id).delete()
        return jsonify({"success": True}), 200
    except Exception as e:
        return f"An Error Occurred: {e}"


if __name__ == '__main__':
    app.run(threaded=True, host='0.0.0.0', port=5000)
