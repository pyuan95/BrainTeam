import sqlite3
import urllib.request
from urllib.parse import quote
import json
from bs4 import BeautifulSoup
from datetime import datetime
import re
from random import sample, shuffle
import json
conn = sqlite3.connect('alltopics.db')
db = conn.cursor()
db.execute("""DROP TABLE tossups""")
db.execute("""CREATE TABLE IF NOT EXISTS tossups(id INTEGER PRIMARY KEY, topic TEXT, tossup TEXT, answer TEXT, topicid INTEGER, 
            name TEXT, url TEXT, cd TEXT, ud TEXT)""")
#db.execute("""ALTER TABLE tossups ADD COLUMN cd TEXT""")
#db.execute("""ALTER TABLE tossups ADD COLUMN ud TEXT""")
db.execute("""CREATE TABLE IF NOT EXISTS topicslist(id INTEGER PRIMARY KEY, topic TEXT, daysold INTEGER, correct INTEGER, wrong INTEGER, numbertopics INTEGER)""")
try:
    a = open("settings.json",'r')
    a.close()
except:
    a = open("settings.json","w")
    b = dict()
    b['date'] = (datetime.now()).strftime('%Y-%m-%d')
    json.dump(b,a)
    a.close()

def parseHTML(column):
    a = column.replace("<strong>","[b]")
    b = a.replace("</strong>","[/b]")
    c = b.replace("<b>","[b]")
    d = c.replace("</b>","[/b]")
    e = BeautifulSoup(d)
    return e.get_text()

def downloadtopics(topics):
    db.execute("""SELECT topic FROM topicslist""")
    listoftopics = db.fetchall()
    for topic in topics:
        if topic in listoftopics:
            continue
        inp = quote(topic)
        url = "https://www.quizdb.org/api/search?search%5Bquery%5D=" + inp + "&search%5Blimit%5D=false&download=json"
        print(url)
        data = json.load(urllib.request.urlopen(url))
        x = 0
        final = []
        while x < len(data["data"]["tossups"]):
            text = data["data"]["tossups"][x]["formatted_text"]
            answer = data["data"]["tossups"][x]["formatted_answer"]
            z = [text, answer, data["data"]["tossups"][x]["category"]], topic
            final.append(z)
            x = x + 1
        for tossup in final:
            db.execute("""INSERT INTO tossups(topic, tossup, answer, topicid, name, url, cd, ud)
                        VALUES (?,?,?,?,?,?,?,?)""",
                       (tossup[1], parseHTML(tossup[0][0]), parseHTML(tossup[0][1]),
                        tossup[0][2]["id"], tossup[0][2]["name"],
                        tossup[0][2]["url"], tossup[0][2]["created_at"],
                        tossup[0][2]["updated_at"]))
        db.execute("""INSERT INTO topicslist(topic, daysold, correct, wrong, numbertopics) VALUES (?,?,?,?,?)""",
                   (topic,0, 0, 0, data["data"]["num_tossups_found"]))
        conn.commit()

def cleartopics():
    db.execute("DROP TABLE tossups")
    db.execute("DROP TABLE topicslist")


def removetopic(topic):
    try:
        db.execute("DELETE FROM tossups WHERE topic = ?",(topic))
        db.execute("DELETE FROM topicslist WHERE topic =?",(topic))
    except:
        print("failed")


def updatedate():
    with open("settings.json") as a:
        data = json.load(a)
    a.close()
    olddate = datetime.strptime(data["date"], '%Y-%m-%d')
    newdate = datetime.now()
    days = int((newdate - olddate).days)
    newdatestring = newdate.strftime('%Y-%m-%d')
    data['date'] = newdatestring
    with open("settings.json",'w') as a:
        json.dump(data,a)
    db.execute("""SELECT topic, daysold FROM topicslist""")
    topics = db.fetchall()
    for x in topics:
        try:
            db.execute("UPDATE topicslist SET daysold = " + str(days + x[1]) + " WHERE topic = " + str(x[0]))
        except:
            print("failed!")
def correct(topic):
    db.execute("UPDATE topicslist SET correct = correct + 1 WHERE topic = " + topic)

def wrong(topic):
    db.execute("UPDATE topicslist SET wrong = wrong + 1 WHERE topic = " + topic)


downloadtopics(["Biden","obama"])
db.execute("SELECT topic,tossup,answer,topicid,name,url,cd,ud FROM tossups")
alltopics = db.fetchall()
db.execute("SELECT topic, daysold, correct, wrong, numbertopics FROM topicslist")
topicslist = db.fetchall()
print(alltopics[2])
print(topicslist[1])




