import json
from datetime import datetime
from kafka import KafkaProducer
from flask import Flask, jsonify, request

app = Flask(__name__)
producer = KafkaProducer(bootstrap_servers='localhost:9092')
topicName = "restapi-topic"

BANKS = [
    {"key": "1", "name": "SWEDBANK"},
    {"key": "2", "name": "IKANOBANKEN"},
    {"key": "3", "name": "JPMORGAN"},
    {"key": "4", "name": "NORDEA"},
    {"key": "5", "name": "CITIBANK"},
    {"key": "6", "name": "HANDELSBANKEN"},
    {"key": "7", "name": "SBAB"},
    {"key": "8", "name": "HSBC"},
    {"key": "9", "name": "NORDNET"},
]


def writeKafka(topicName, message):
    currentDate = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    producer.send(topicName, (currentDate + " " + message).encode())


@app.route("/bank/list", methods=["GET"])
def list_all_banks():
    writeKafka(topicName, "returning all banks")
    return jsonify(BANKS)


@app.route("/bank/find/<string:name>", methods=["GET"])
def list_by_name(name):
    for bank in BANKS:
        if bank["name"] == name:
            writeKafka(topicName, "returning bank by name: " + name)
            return jsonify(bank)
    writeKafka(topicName, "failed to find bank by name: " + name)
    return jsonify({"error": "data not found"})


@app.route("/bank/find/<int:key>", methods=["GET"])
def list_by_key(key):
    for bank in BANKS:
        if bank["key"] == key:
            writeKafka(topicName, "returning bank by key: " + key)
            return jsonify(bank)
    writeKafka(topicName, "failed to find bank by key: " + key)
    return jsonify({"error": "data not found"})
