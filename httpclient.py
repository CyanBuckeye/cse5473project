import json
import http.client
import pdb

def join(name):
    conn = http.client.HTTPConnection('', 8000)
    data = {"type": 1, "msg":name}
    data = json.dumps(data)
    #conn.request("GET", "/")
    headers = {"Content-type": "application/json"}

    conn.request("POST", "", data, headers)
    r1 = conn.getresponse()
    print(r1.status, r1.reason)

    data = r1.read()
    pk = json.loads(data.decode())
    print(pk['state'], pk['msg'])
    conn.close()

def query(name):
    conn = http.client.HTTPConnection('', 8000)
    data = {"type": 0, "msg":name}
    data = json.dumps(data)
    headers = {"Content-type": "application/json"}
    conn.request("POST", "", data, headers)
    r1 = conn.getresponse()
    print(r1.status, r1.reason)

    data = r1.read()
    pk = json.loads(data.decode())
    print(pk['state'], pk['msg'])
    conn.close()

def vote(name):
    conn = http.client.HTTPConnection('', 8000)
    data = {"type": 2, "msg":name}
    data = json.dumps(data)
    headers = {"Content-type": "application/json"}
    conn.request("POST", "", data, headers)
    r1 = conn.getresponse()
    print(r1.status, r1.reason)

    data = r1.read()
    pk = json.loads(data.decode())
    print(pk['state'], pk['msg'])
    conn.close()
