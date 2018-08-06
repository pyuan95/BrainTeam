import string
x = open(r"C:\Users\Patrick\Desktop\brane team\searchDatabase.htm", encoding="ISO-8859-1").read().splitlines()
y = open(r'C:\Users\Patrick\Desktop\brane team\3.txt', 'w', encoding="ISO-8859-1")
z = open(r'C:\Users\Patrick\Desktop\brane team\genericwords.txt')
lst = list()
genericwords = []
for line in z:
     genericwords.append(line[:len(line)-1])
print(genericwords)
def checkcommon(str1, str2):
    a = str1.split(" ")
    b = str2.split(" ")
    lstt = list()
    #print("of" in genericwords)
    for element in a:
        if element in b:
            if element not in lstt:
                if element not in genericwords:
                    lstt.append(element)
    if len(lstt) <3 or str1 != str2 :
        return False
    else:
        return " ".join(lstt)

for line in x:
    if "ANSWER:</strong></em>" in line:
        lst.append(line)
bruh = dict()
for item in lst:
    item = item.strip()
    idx = item.rfind('</em>')
    item = item[idx+5:]
    item = item[:-4]
    item = item.strip()
    #if "[" in item:
        #idx = item.find('[')
        #item = item[:idx-1]
        #item = item.strip()
    item = item.translate(item.maketrans('', '', string.punctuation))
    item = item.lower()
    mm = False
    k = None
    for key in bruh:
        if checkcommon(key,item) is False:
            pass
        else:
            mm = checkcommon(key,item)
            k = key
    if mm is False:
        if item not in bruh:
            bruh[item] = 1
        else:
            bruh[item] = bruh[item] + 1
    else:
        x = bruh[k]
        del bruh[k]
        bruh[mm] = x+1
    print(len(bruh))
    #hh = item.split(" ")
    #item = hh[len(hh)-1]
def findvalue(dict, value):
    final = []
    for key in dict:
        if dict[key] == value:
            final.append(key)
    return final
values = list(bruh.values())
bruh1 = dict()
for value in values:
    if value not in bruh1:
        bruh1[value] = 1
    else:
        bruh1[value] = bruh1[value] + 1
bruh1list = list(bruh1.keys())
bruh1list.sort(reverse= True)
for value in bruh1list:
    print(value, bruh1[value])
values = list(set(values))
values.sort(reverse = True)
print(values)
for value in values:
    x = findvalue(bruh, value)
    for key in x:
        y.write(key + ": " + str(bruh[key]) + "\n")



#y = open(r'C:\Users\Patrick\Desktop\brane team\y.txt', 'w', encoding="ISO-8859-1")
