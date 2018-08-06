x = open(r"C:\Users\Patrick\Desktop\brane team\quinterest\HS level\3\science.txt",encoding="ISO-8859-1")
a = 0
lst = list()
import math
for line in x:
    a = a+1
    lst.append(line)
b = math.floor(a*0.055)
a = a*0.12
a = math.floor(a)
print(lst[a])
print(a)
print(lst[b])
print(b)