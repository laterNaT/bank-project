#!/bin/sh
export FLASK_APP=./index.py
pipenv run flask --debug run --port=8100
