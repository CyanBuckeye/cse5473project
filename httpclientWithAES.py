import json
import httplib
import pdb
from Crypto.Cipher import AES
from Crypto import Random
import base64
import hashlib

class AESCipher:
    def __init__(self, raw_key):
        self.bs = 16
        self.raw_key = raw_key
        self.key = hashlib.sha256(raw_key.encode()).digest()
    
        
    def _pad(self, s):#PKCS5 Padding
        return s + (self.bs - len(s) % self.bs) * chr(self.bs - len(s) % self.bs)
    
    def _unpad(self, s):#remove padding
        return s[:-ord(s[-1])]
                 
    def encrypt(self, plaintxt):
        msg = self._pad(plaintxt)
        iv = Random.new().read(AES.block_size)
        cipher = AES.new(self.key, AES.MODE_CBC, iv)
        return base64.b64encode(iv + cipher.encrypt(msg))
        
    def decrypt(self, ciphertxt):
        ciphertxt = base64.b64decode(ciphertxt)
        iv = ciphertxt[:AES.block_size]
        cipher = AES.new(self.key, AES.MODE_CBC, iv)
        return self._unpad(cipher.decrypt(ciphertxt[AES.block_size:])).decode('utf-8')

aesWrapper = AESCipher('1234567890123456')

def join(name):
    conn = httplib.HTTPConnection('192.168.56.1', 8000)
    data = {"type": 1, "msg":name}
    data = json.dumps(data)
    #conn.request("GET", "/")
    headers = {"Content-type": "application/json"}
    ciphertxt = aesWrapper.encrypt(data)
    conn.request("POST", "", ciphertxt, headers)
    r1 = conn.getresponse()

    data = r1.read()
    data = aesWrapper.decrypt(data)    
    pk = json.loads(data.decode())
    print(pk['state'], str(pk['msg']))
    conn.close()

def query(name):
    conn = httplib.HTTPConnection('192.168.56.1', 8000)
    data = {"type": 0, "msg":name}
    data = json.dumps(data)
    headers = {"Content-type": "application/json"}
    ciphertxt = aesWrapper.encrypt(data)
    conn.request("POST", "", ciphertxt, headers)
    r1 = conn.getresponse()
    print(r1.status, r1.reason)

    data = r1.read()
    data = aesWrapper.decrypt(data)  
    pk = json.loads(data.decode())
    print(pk['state'], str(pk['msg']))
    conn.close()

def vote(name):
    conn = httplib.HTTPConnection('192.168.56.1', 8000)
    data = {"type": 2, "msg":name}
    data = json.dumps(data)
    headers = {"Content-type": "application/json"}
    ciphertxt = aesWrapper.encrypt(data)
    conn.request("POST", "", ciphertxt, headers)
    r1 = conn.getresponse()
    print(r1.status, r1.reason)

    data = r1.read()
    data = aesWrapper.decrypt(data)  
    pk = json.loads(data.decode())
    print(pk['state'], str(pk['msg']))
    conn.close()

join('evan')