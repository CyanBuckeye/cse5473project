import json
import http.client
conn = http.client.HTTPConnection('', 8000)
conn.request("GET", "/")
r1 = conn.getresponse()
print(r1.status, r1.reason)
data = r1.read()
pk = json.loads(data.decode())
print(pk['name'], pk['msg'])
conn.close()
