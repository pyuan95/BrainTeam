import pandas as pd
import requests
from urllib.parse import quote
import json
from datetime import datetime
def jsondump(topic):
    d = open("database/topics.txt","r")
    for line in d:
        if topic == line:
            return None
    d.close()
    inp = quote(topic)
    url = "https://www.quizdb.org/api/search?search%5Bquery%5D" + inp + "&search%5Blimit%5D=false&download=json"
    a = requests.get(url).json()
    b = open("database/" + topic + ".json","w")
    c = open("database/topics.txt","a")
    c.write(topic + ",0 0 0 0" + "\n")
    json.dump(a,b)


