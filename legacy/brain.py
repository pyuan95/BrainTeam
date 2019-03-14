import requests
from urllib.parse import quote
import json
from datetime import datetime
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

def cleartopics():
    from os import remove
    with open("database/topics.json") as c:
        data = json.load(c)
    for key in list(data):
        if key == "date":
            pass
        else:
            data.pop(key, None)
            remove("database/" + key + ".json")
    c.close()
    a = open("database/topics.json", 'w')
    json.dump(data, a)
    a.close()

def removetopic(topic):
    from os import remove
    with open("database/topics.json") as c:
        data = json.load(c)
    data.pop(topic, None)
    a = open("database/topics.json", 'w')
    json.dump(data, a)
    remove("database/" + topic + ".json")
    a.close()
    c.close()


#topics.json format: (topic: [daysage, openage, right, wrong])
def jsondump(topic):
    inp = quote(topic)
    url = "https://www.quizdb.org/api/search?search%5Bquery%5D=" + inp + "&search%5Blimit%5D=false&download=json"
    print(url)
    a = requests.get(url).json()
    b = open("database/" + topic + ".json","w")
    json.dump(a, b)
    b.close()
    b = open("database/" + topic + ".json")
    data = json.load(b)
    weight = data["data"]["num_tossups_found"]
    b.close()
    if weight == 0:
        from os import remove
        remove("database/" + topic + ".json")
        print("topic has no tossups")
        raise Exception("No Tossups Are here!")
    with open("database/topics.json") as c:
        data = json.load(c)
    c.close()
    data[topic] = [0, 0, 0, 0, int(weight)]
<<<<<<< HEAD
    c = open("database/topics.json", "w")
    json.dump(data, c)
    c.close()

=======
    c = open("database/topics.json","w")
    json.dump(data,c)
>>>>>>> parent of 2c457ae... Reformat stuff and stuff

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
    c.close()

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
    c.close()

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
    c.close()


def parsetopic(topic):
    with open("database/" + topic + ".json") as c:
        data = json.load(c)
    x = 0
    final = {}
    while x < len(data["data"]["tossups"]):
        text = data["data"]["tossups"][x]["text"]
        answer = data["data"]["tossups"][x]["answer"]
        final[x] = [text,answer], topic
        x = x + 1
    c.close()
    return final
def readtopic(lst): #input a list [question, answer]
    question = lst[0]
    #answer = lst[1]
    questionsentences = split_into_sentences(question)
    return questionsentences


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

def tieronequeue(number = 3): #makes a queue of all topics from last 7 days and correct rate of less than 33 pct. List: [[tossups],[topics]]
    with open("database/topics.json") as c:
        data = json.load(c)
    keys = []
    for key in data:
        if key == 'date':
            pass
        else:
            if data[key][2] + data[key][3] != 0:
                if data[key][0] <= 7 or data[key][2]/(data[key][2] + data[key][3]) <= 0.33:
                    keys.append(key)
            else:
                keys.append(key)
    dictlist = []
    for key in keys:
        dictlist.append(selectquestions(parsetopic(key), questionnumber=number))
    return dicttolist(combinequestions(dictlist))

<<<<<<< HEAD

def tiertwoqueue(numberquestions=30):  # makes queue of n questions (def = 30), topics in last 30 days or hit rate less than 50% have 2x chance, may appear twice.
=======
def tiertwoqueue(numberquestions = 30): # makes queue of n questions (def = 30), topics in last 30 days or hit rate less than 50% have 2x chance, may appear twice.
>>>>>>> parent of 2c457ae... Reformat stuff and stuff
    with open("database/topics.json") as c:
        data = json.load(c)
    keys = []
    for key in data:
        if key == 'date':
            pass
        else:
            if data[key][2] + data[key][3] != 0:
                if data[key][0] <= 21 or data[key][2]/(data[key][2] + data[key][3]) <= 0.5:
                    keys.append(key)
            else:
                keys.append(key)
    for key in data:
        if key == "date":
            pass
        else:
            keys.append(key)
    try:
        a = sample(keys, number)
    except:
        a = keys
    dictlist = []
    for topic in a:
        dictlist.append(selectquestions(parsetopic(topic), questionnumber=1))
    return dicttolist(combinequestions(dictlist))

def selectrandomquestion(number = 1):
    with open("database/topics.json") as c:
        data = json.load(c)
    keys = []
    for key in data:
        if key == "date":
            pass
        else:
            keys.append(key)
    try:
        a = sample(keys, number)
    except:
        a = keys
    dictlist = []
    for topic in a:
        dictlist.append(selectquestions(parsetopic(topic), questionnumber=1))
    return dicttolist(combinequestions(dictlist))


def selectweightedquestion(number = 1):
    with open("database/topics.json") as c:
        data = json.load(c)
    keys = []
    for key in data:
        if key == "date":
            pass
        else:
            x = 0
            y = data[key][4]
            while x < y:
                keys.append(key)
                x = x + 1
    try:
        a = sample(keys, number)
    except:
        a = keys
    dictlist = []
    if len(keys) == 0:
        return dictlist
    for topic in a:
        dictlist.append(selectquestions(parsetopic(topic), questionnumber=1))
    x = dicttolist(combinequestions(dictlist))
    return x


def numtopics():
    with open("database/topics.json") as c:
        data = json.load(c)

    return len(data) - 1

def moreinfo():
    with open("database/" + "Clinton" + ".json") as c:
        data = json.load(c)
    print(data['data']['tossups'][0]['tournament'])


def tossupinfo(topic): # SHOULD BE DONE FROM MAIN.py AFTER THE UPDATE
    with open("database/" + topic + ".json") as c:
        data = json.load(c)
    answer1 = data["data"]["tossups"][0]["subcategory"]
    finallist = []
    for key in answer1:
        finallist.append(key + " = " + str(answer1[key]))
    return finallist

def selectalltopicsquestions(number = 10):
    data = parsetopic("everythingbrainteam")
    return sample(list(data.values()), number)


####### EXPERIMENTAL ##############

def downloadtopics(topics):
    with open("database/alltopics.json") as a:
        alltopics = json.load(a)
    with open("database/topics.json") as b:
        topicslist = json.load(b)
    a.close()
    b.close()
    for topic in topics:
        inp = quote(topic)
        url = "https://www.quizdb.org/api/search?search%5Bquery%5D=" + inp + "&search%5Blimit%5D=false&download=json"
        print(url)
        data = json.load(urllib.request.urlopen(url))
        x = 0
        final = []
        while x < len(data["data"]["tossups"]):
            text = data["data"]["tossups"][x]["text"]
            answer = data["data"]["tossups"][x]["answer"]
            z = [text, answer, data["data"]["tossups"][x]["subcategory"]], topic
            final.append(z)
            x = x + 1
        alltopics[topic] = final
        topicslist[topic] = [0,0,0,0,int(data["data"]["num_tossups_found"])]
    with open("database/alltopics.json",'w') as a:
        json.dump(alltopics,a)
    with open("database/topics.json",'w') as b:
        json.dump(topicslist,b)

def tieronequeue1(number=3):  # makes a queue of all topics from last 7 days and correct rate of less than 33 pct. List: [[tossups],[topics]]
    with open("database/topics.json") as c:
        data = json.load(c)
    c.close()
    keys = []
    for key in data:
        if key == 'date':
            pass
        else:
            if data[key][2] + data[key][3] != 0:
                if data[key][0] <= 7 or data[key][2] / (data[key][2] + data[key][3]) <= 0.33:
                    keys.append(key)
            else:
                keys.append(key)
    with open("database/alltopics.json") as c:
        data = json.load(c)
    c.close()
    queue = []
    for key in keys:
        try:
            tossups = sample(data[key], number)
            for tossup in tossups:
                queue.append(tossup)
        except ValueError:
            for tossup in data[key]:
                queue.append(tossup)
    print(queue)
    shuffle(queue)
    return queue

def tiertwoqueue1(number=3):  # makes a queue of all topics from last 7 days and correct rate of less than 33 pct. List: [[tossups],[topics]]
    with open("database/topics.json") as c:
        data = json.load(c)
    c.close()
    keys = []
    for key in data:
        if key == 'date':
            pass
        else:
            if data[key][2] + data[key][3] != 0:
                if data[key][0] <= 21 or data[key][2] / (data[key][2] + data[key][3]) <= 0.5:
                    keys.append(key)
            else:
                keys.append(key)
    with open("database/alltopics.json") as c:
        data = json.load(c)
    c.close()
    queue = []
    for key in keys:
        try:
            tossups = sample(data[key], number)
            for tossup in tossups:
                queue.append(tossup)
        except ValueError:
            for tossup in data[key]:
                queue.append(tossup)
    print(queue)
    shuffle(queue)
    return queue

def selectrandomquestion1(number = 1):
    with open("database/alltopics.json") as c:
        data = json.load(c)
    try: a = sample(data.values(), number)
    except: a = data.values()
    queue3 = []
    for topic in a:
        queue3.append((sample(data[topic]),1))
    return queue3

def selectweightedquestion1(number = 20):
    with open("database/topics.json") as c:
        data = json.load(c)
    keys = []
    for key in data:
        if key == "date":
            pass
        else:
            x = 0
            y = data[key][4]
            while x < y:
                keys.append(key)
                x = x + 1
    try:
        a = sample(keys, number)
    except:
        a = keys

    with open("database/alltopics.json") as c:
        data = json.load(c)
    queue4 = []
    for topic in a:
        queue4.append((sample(data[topic]), 1))
    return queue4

<<<<<<< HEAD
downloadtopics(["Iran","Hoppity"])
#print(selectrandomquestion1())

=======
downloadtopics(["trump","clinton","java"])
#print(selectrandomquestion1())
>>>>>>> parent of 0f1b272... jj
