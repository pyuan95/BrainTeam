from pandas.io.json import json_normalize
import pandas as pd
import json
with open('hello.json') as f:
    data = json.load(f)

a = pd.DataFrame.from_dict(data['data']['bonuses'])
print(a)