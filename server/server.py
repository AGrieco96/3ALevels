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
def update_game():
    try:
        lobby_id = request.json['lobby_id']
        player_id = request.json['player_id']
        time_score = request.json['time']

        doc_game = games_collection.document(lobby_id)
        doc_g = doc_game.get()
        level_number = str(doc_g.to_dict()['level'])
        field_string_time = player_id + '.time.' + level_number

        doc_game.update({
            field_string_time: time_score
        })

        doc_lobby = lobby_collection.document(lobby_id)
        doc_l = doc_lobby.get()
        if player_id != str(doc_l.to_dict()['player_1']):
            other_player = str(doc_l.to_dict()['player_1'])
        else:
            other_player = str(doc_l.to_dict()['player_2'])

        time_other = int(doc_g.to_dict()[other_player]['time'][level_number])

        if time_other != 0:
            field_string_counter_player = player_id + '.counter'
            field_string_counter_other_player = other_player + '.counter'
            counter_player = int(doc_g.to_dict()[player_id]['counter']) + 1
            counter_other_player = int(doc_g.to_dict()[other_player]['counter']) + 1
            new_level = int(level_number) + 1
            if time_other <= int(time_score):
                doc_game.update({
                    field_string_counter_other_player: str(counter_other_player)
                })
            else:
                doc_game.update({
                    field_string_counter_player: str(counter_player)
                })
            doc_game.update({
                u'level': str(new_level),
            })
        return jsonify({"success": True}), 200
    except Exception as e:
        return f"An Error Occurred: {e}"


@app.route('/new-game', methods=['POST'])
def create_game():
    try:
        lobby_id = request.json['lobby_id']

        doc_lobby = lobby_collection.document(lobby_id)
        doc = doc_lobby.get()
        username_player1 = str(doc.to_dict()['player_1'])
        username_player2 = str(doc.to_dict()['player_2'])

        new_game = {
            u'level': 1,
            username_player1: {
                u'counter': 0,
                u'time': {
                    u'1': 0,
                    u'2': 0,
                    u'3': 0,
                    u'4': 0,
                    u'5': 0
                }
            },
            username_player2: {
                u'counter': 0,
                u'time': {
                    u'1': 0,
                    u'2': 0,
                    u'3': 0,
                    u'4': 0,
                    u'5': 0
                }
            }
        }
        games_collection.document(lobby_id).set(new_game)
        return jsonify({"success": True}), 200
    except Exception as e:
        return f"An Error Occurred: {e}"


@app.route('/ai', methods=['PUT'])
def image_recognition():
    try:
        base64_image_string = request.json['image']
        object_str = request.json['object']
        lobby_id = request.json['lobby_id']
        player_id = request.json['player_id']
        time_score = request.json['time']

        doc_game = games_collection.document(lobby_id)
        doc_g = doc_game.get()
        level_number = str(doc_g.to_dict()['level'])
        field_string_time = player_id + '.time.' + level_number

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
            doc_game.update({
                field_string_time: [time_score, True]
            })
            response = "Good job! You have take a photo of a " + object_str
        else:
            doc_game.update({
                field_string_time: [time_score, False]
            })
            response = "Oh no! You have take a photo of a " + predictions[0]

        doc_lobby = lobby_collection.document(lobby_id)
        doc_l = doc_lobby.get()
        if player_id != str(doc_l.to_dict()['player_1']):
            other_player = str(doc_l.to_dict()['player_1'])
        else:
            other_player = str(doc_l.to_dict()['player_2'])

        if isinstance(doc_g.to_dict()[other_player]['time'][level_number], list):
            time_other = int(doc_g.to_dict()[other_player]['time'][level_number][0])

            doc_game = games_collection.document(lobby_id)
            doc_g = doc_game.get()

            field_string_counter_player = player_id + '.counter'
            field_string_counter_other_player = other_player + '.counter'
            counter_player = int(doc_g.to_dict()[player_id]['counter']) + 1
            counter_other_player = int(doc_g.to_dict()[other_player]['counter']) + 1
            new_level = int(level_number) + 1
            check_other_player = doc_g.to_dict()[other_player]['time'][level_number][1]
            check_player = doc_g.to_dict()[player_id]['time'][level_number][1]

            if time_other <= int(time_score):
                if check_other_player:
                    doc_game.update({
                        field_string_counter_other_player: str(counter_other_player)
                    })
                elif check_player:
                    doc_game.update({
                        field_string_counter_player: str(counter_player)
                    })
            elif check_player:
                doc_game.update({
                    field_string_counter_player: str(counter_player)
                })
            elif check_other_player:
                doc_game.update({
                    field_string_counter_other_player: str(counter_other_player)
                })
            doc_game.update({
                u'level': str(new_level),
            })

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


@app.route('/delete', methods=['DELETE'])
def delete():
    """
        delete() : Delete a document from Firestore collection
    """
    try:
        # Check for ID in URL query
        for i in range(2, 20):
            games_collection.document(str(i)).delete()
            lobby_collection.document(str(i)).delete()
        return jsonify({"success": True}), 200
    except Exception as e:
        return f"An Error Occurred: {e}"


if __name__ == '__main__':
    app.run(threaded=True, host='0.0.0.0', port=5000)
