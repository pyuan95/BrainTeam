import string
import sqlite3
import collections
import re
from multiprocessing import Pool
import codecs

categories = ['Current Events', 'Fine Arts', 'Geography', 'History', 'Literature', 'Mythology', 'Philosophy', 'Religion', 'Science', 'Social Science', 'Trash']
# categories = ['Current Events']
z = open('genericwords.txt')
genericwords = []
for line in z:
     genericwords.append(line[:len(line)-1])

MIN_JACCARD_SIM = .4

def get_jaccard_sim(str1, str2):
    a = set(str1.split()) 
    b = set(str2.split())
    c = a.intersection(b)
    if len(a) + len(b) - len(c) == 0:
         return 0
    return float(len(c)) / (len(a) + len(b) - len(c))

def get_max_jaccard_sim(str1, str2):
     sims = []
     sims.append(get_jaccard_sim(str1, str2))
     str1 = str1.split(" ")
     str2 = str2.split(" ")
     if len(str1) == len(str2):
          return get_jaccard_sim(" ".join(str1), " ".join(str2))
     if len(str2) > len(str1):
          diff = len(str2) - len(str1)
          for i in range(1,diff + 1):
               sims.append(get_jaccard_sim(" ".join(str2[diff:]), " ".join(str1)))
               sims.append(get_jaccard_sim(" ".join(str2[:diff * -1]), " ".join(str1)))
     else:
          diff = len(str1) - len(str2)
          for i in range(1, diff + 1):
               sims.append(get_jaccard_sim(" ".join(str1[diff:]), " ".join(str2)))
               sims.append(get_jaccard_sim(" ".join(str1[:diff * -1]), " ".join(str2)))
     return max(sims)

def answer_in(answer, d, min_sim):
     for key in d:
          if get_max_jaccard_sim(answer, key) > min_sim:
               return key
     return False

def count_and_write_frequencies(category):
     y = open('freq_lists/' + category + '.txt', 'w', encoding="UTF-8")
     x = sqlite3.connect('allTossups.db')
     db = x.cursor()
     db.execute("SELECT answer FROM allTossups WHERE Category='" + category + "'")
     answer_lines = [a[0] for a in db.fetchall()]

     encoding_exceptions = 0
     for idx, line in enumerate(answer_lines):
          line = line.lower()
          line.strip()
          # line = re.sub("[\(\[].*?[\)\]]", "", line)
          line = line.split(" ")
          line = [word for word in line if word not in genericwords]
          line = " ".join(line)
          line = line.replace('"', "")
          line = line.replace('.', "")
          line = line.replace(",", "")
          line = line.replace("[", "")
          line = line.replace("(", "")
          line = line.replace("]", "")
          line = line.replace(")", "")
          line = line.replace("-", " ")
          # print(line)
          line.strip()
          answer_lines[idx] = line
          
     frequencies = dict()
     for index, line in enumerate(answer_lines):
          if index % 1000 == 0:
               print(category + ": ", index, " / ", len(answer_lines))
          is_in = answer_in(line, frequencies, MIN_JACCARD_SIM)
          # print(is_in)
          if is_in:
               frequencies[is_in] += 1
          else:
               frequencies[line] = 1

     lst = sorted(frequencies.items(), key=lambda x: x[1], reverse=True)

     for line in lst:
          line = list(line)
          line[0] = line[0][:line[0].find("or") - 1] if "or" in line[0] else line[0]
          line[0] = line[0][:line[0].find("accept") - 1] if "accept" in line[0] else line[0]
          line[0] = line[0].strip()
          y.write(line[0] + ": " + str(line[1]) + "\n")
     y.close()

if __name__ == '__main__':
     p = Pool(11)
     p.map(count_and_write_frequencies, categories)

#y = open(r'C:\Users\Patrick\Desktop\brane team\y.txt', 'w', encoding="ISO-8859-1")
