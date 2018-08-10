import pandas as pd
import requests
from urllib.parse import quote
import json
from datetime import datetime
from time import sleep
import re
from random import sample, shuffle
from math import floor
def split_into_sentences(text):
    caps = "([A-Z])"
    prefixes = "(Mr|St|Mrs|Ms|Dr)[.]"
    suffixes = "(Inc|Ltd|Jr|Sr|Co)"
    starters = "(Mr|Mrs|Ms|Dr|He\s|She\s|It\s|They\s|Their\s|Our\s|We\s|But\s|However\s|That\s|This\s|Wherever)"
    acronyms = "([A-Z][.][A-Z][.](?:[A-Z][.])?)"
    websites = "[.](com|net|org|io|gov)"
    text = " " + text + "  "
    text = text.replace("\n"," ")
    text = re.sub(prefixes,"\\1<prd>",text)
    text = re.sub(websites,"<prd>\\1",text)
    if "Ph.D" in text: text = text.replace("Ph.D.","Ph<prd>D<prd>")
    text = re.sub("\s" + caps + "[.] "," \\1<prd> ",text)
    text = re.sub(acronyms+" "+starters,"\\1<stop> \\2",text)
    text = re.sub(caps + "[.]" + caps + "[.]" + caps + "[.]","\\1<prd>\\2<prd>\\3<prd>",text)
    text = re.sub(caps + "[.]" + caps + "[.]","\\1<prd>\\2<prd>",text)
    text = re.sub(" "+suffixes+"[.] "+starters," \\1<stop> \\2",text)
    text = re.sub(" "+suffixes+"[.]"," \\1<prd>",text)
    text = re.sub(" " + caps + "[.]"," \\1<prd>",text)
    if "”" in text: text = text.replace(".”","”.")
    if "\"" in text: text = text.replace(".\"","\".")
    if "!" in text: text = text.replace("!\"","\"!")
    if "?" in text: text = text.replace("?\"","\"?")
    text = text.replace(".",".<stop>")
    text = text.replace("?","?<stop>")
    text = text.replace("!","!<stop>")
    text = text.replace("<prd>",".")
    sentences = text.split("<stop>")
    sentences = sentences[:-1]
    sentences = [s.strip() for s in sentences]
    return sentences

def cleanhtml(raw_html): #kinda useless tbh
  cleanr = re.compile('<.*?>')
  cleantext = re.sub(cleanr, '', raw_html)
  return cleantext

#topics.json format: (topic: [daysage, openage, right, wrong])
def jsondump(topic):
    inp = quote(topic)
    url = "https://www.quizdb.org/api/search?search%5Bquery%5D=" + inp + "&search%5Blimit%5D=false&download=json"
    print("https://www.quizdb.org/api/search?search%5Bquery%5D=Donald%20Trump&search%5Blimit%5D=false&download=json")
    print(url)
    a = requests.get(url).json()
    b = open("database/" + topic + ".json","w")
    json.dump(a, b)
    b.close()
    with open("database/topics.json") as c:
        data = json.load(c)
    c.close()
    data[topic] = [0, 0, 0, 0]
    c = open("database/topics.json","w")
    json.dump(data,c)
def updatedate(): #run on startup
    with open("database/topics.json") as c:
        data = json.load(c)
    olddate = datetime.strptime(data["date"], '%Y-%m-%d')
    newdate = datetime.now()
    days = int((newdate-olddate).days)
    for key in data:
        if key == "date":
            data[key] = newdate.strftime('%Y-%m-%d')
        else:
            data[key][0] = data[key][0] + days
            data[key][1] = data[key][1] + 1
    c.close()
    c = open("database/topics.json", "w")
    json.dump(data, c)

def correct(topic):
    with open("database/topics.json") as c:
        data = json.load(c)
    if topic not in data:
        return "topic not here"
    for key in data:
        if key == topic:
            data[key][2] = data[key][2] + 1
    c.close()
    c = open("database/topics.json", "w")
    json.dump(data, c)

def wrong(topic):
    with open("database/topics.json") as c:
        data = json.load(c)
    if topic not in data:
        return "topic not here"
    for key in data:
        if key == topic:
            data[key][3] = data[key][3] + 1
    c.close()
    c = open("database/topics.json", "w")
    json.dump(data, c)


def parsetopic(topic):
    with open("database/" + topic + ".json") as c:
        data = json.load(c)
    x = 0
    final = {}
    while x < len(data["data"]["tossups"]):
        text = data["data"]["tossups"][x]["text"]
        answer = data["data"]["tossups"][x]["answer"]
        final[x] = [text,answer]
        x = x + 1
    return final

def readtopic(lst, time = 0.25): #input a list [question, answer]
    question = lst[0]
    answer = lst[1]
    questiontext = question.split()
    questionsentences = split_into_sentences(question)
    x = 0
    while x < len(questiontext):
        print(questiontext[x] + " ", end="", flush=True) # prob replace later
        x = x + 1
        sleep(time)


def selectquestions(dictionary, questionnumber = 3):
    questionrange = list(range(len(dictionary)))
    if len(questionrange) < questionnumber:
        return dictionary
    numbers = sample(questionrange, questionnumber)
    final = dict()
    x = 0
    for num in numbers:
        final[x] = dictionary[num]
        x = x + 1
    return final

def combinequestions(dictionaries):
    questionlist = []
    for dictionary in dictionaries:
        for key in dictionary:
            questionlist.append(dictionary[key])
    final = dict()
    x = 0
    for question in questionlist:
        final[x] = question
        x = x + 1
    return final

def dicttolist(dictionary):  # also shuffles the dictionary into random order
    final = list()
    for key in dictionary:
        final.append(dictionary[key])
    shuffle(final)
    return final
