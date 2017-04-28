# -*- coding: utf-8 -*-
"""
Created on Mon Apr 24 17:12:05 2017

@author: Administrator
"""

import BaseHTTPServer
import json
import random
import pdb
from Crypto.Cipher import AES
from Crypto import Random
import base64
import hashlib

class AESCipher:

    def __init__(self, key):
        self.bs = 16
        self.key = hashlib.sha256(key.encode()).digest()

    def encrypt(self, message):
        message = self._pad(message)
        iv = Random.new().read(AES.block_size)
        cipher = AES.new(self.key, AES.MODE_CBC, iv)
        return base64.b64encode(iv + cipher.encrypt(message)).decode('utf-8')

    def decrypt(self, enc):
        enc = base64.b64decode(enc)
        iv = enc[:AES.block_size]
        cipher = AES.new(self.key, AES.MODE_CBC, iv)
        return self._unpad(cipher.decrypt(enc[AES.block_size:])).decode('utf-8')

    def _pad(self, s):
        return s + (self.bs - len(s) % self.bs) * chr(self.bs - len(s) % self.bs)

    @staticmethod
    def _unpad(s):
        return s[:-ord(s[len(s)-1:])]
        
class message: #the json file exchanged between server and client follows this form: it has three attributes
#(1)state: it means the state of client. state = 1: join the game; state = 2: now client is able to vote; state = 4: get
#final result, such as winning or not.
#(2)type: type = 0: query sent by client. List of players' names and updated state will be returned; type = 1: client join the game
#type = 2: client votes
#(3)msg: if type = 1 or type = 0: msg is the player's name; if type = 2: msg is the player thought to be demon

    def __init__(self, state, tp, msg):
        self.state = state
        self.tp = tp
        self.msg = msg
    def msgtoJSON(self):
        temp = {'state': self.state, 'type': self.tp, 'msg': self.msg}
        return json.dumps(temp)

aesWrapper = AESCipher('1234567890123456')

class HttpRequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    global aesWrapper
    rd = 0 #current round
    num = 0
    players = [] #global list to save players names
    state = {} #dict to perserve each player's state
    msgQueue = {} # message queue
    demon = []
    voteList = {} #vote record
    numRequired = 3 #how many players are required for a game
    demonNumber = 1
    killed = ""
    threshold = 2 # when game ends
    
    def _set_header(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
    '''    
    def do_GET(self):
        self._set_header()
        msg = {'name':'Tom', 'msg':'hello-world'}
        msg = json.dumps(msg)
        self.wfile.write(msg)
    '''
    
    def do_POST(self):
        self._set_header()
        data = self.rfile.read(int(self.headers.getheader('Content-Length')))
        data = aesWrapper.decrypt(data)
        data = json.loads(data)#assume the input is json file
        tp = data['type']
         
        if tp == 0:# query
            player_name = str(data['msg'])
            if player_name in HttpRequestHandler.msgQueue.keys():#if player has latest information, get it
                val = HttpRequestHandler.msgQueue[player_name]
                HttpRequestHandler.msgQueue.pop(player_name)
            else:
                val = 0
            #val == 0: no change; val == 1: you are chosen to be demon; val == 2: someone is killed; val == 4: success; val == 5: fail
            if val == 2:
                info = message(HttpRequestHandler.state[player_name], val, HttpRequestHandler.killed).msgtoJSON()
            if val == 4:
                info = message(4, val, "win").msgtoJSON()
            if val == 5:
                info = message(5, val, "lose").msgtoJSON()
            if val == 0:
                info = message(HttpRequestHandler.state[player_name], val, HttpRequestHandler.players).msgtoJSON()
            info = aesWrapper.encrypt(info)
            self.wfile.write(info)
        if tp == 1:#join game
            player_name = str(data['msg'])
            HttpRequestHandler.players.append(player_name)
            HttpRequestHandler.state[player_name] = 1 # state: join game and alive
            HttpRequestHandler.num += 1
            info = message(1, 0, 'received').msgtoJSON()
            info = aesWrapper.encrypt(info)
            self.wfile.write(info)
            
            if HttpRequestHandler.num == HttpRequestHandler.numRequired: #if all of payers are ready, choose demon
                for i in range(HttpRequestHandler.demonNumber):
                    randN = random.randint(0, len(HttpRequestHandler.players) - 1)
                    while randN in HttpRequestHandler.demon:
                        randN = random.randint(0, len(HttpRequestHandler.players) - 1)
                    HttpRequestHandler.demon.append(HttpRequestHandler.players[randN])
                    #self.msgQueue[self.players[randN]] = 1
                
                #update state of clients
                for player in HttpRequestHandler.players:
                    if player in HttpRequestHandler.demon:
                        HttpRequestHandler.state[player] = 3
                    else:
                        HttpRequestHandler.state[player] = 2
                    HttpRequestHandler.voteList[player] = 0

        if tp == 2:# vote    
            vote_name = data['msg']
            HttpRequestHandler.voteList[vote_name] += 1
            HttpRequestHandler.num -= 1
            info = message(1, 0, 'received').msgtoJSON()
            info = aesWrapper.encrypt(info)
            self.wfile.write(info)
            if HttpRequestHandler.num == 0:
                killed = ""
                maxVal = 0
                for name, vote in HttpRequestHandler.voteList.iteritems():
                    if vote > maxVal:
                        maxVal = vote
                        killed = name
                if killed in HttpRequestHandler.demon:
                    for i in range(len(HttpRequestHandler.players)):
                        if HttpRequestHandler.players[i] in HttpRequestHandler.demon:
                            HttpRequestHandler.msgQueue[HttpRequestHandler.players[i]] = 5
                        else:
                            HttpRequestHandler.msgQueue[HttpRequestHandler.players[i]] = 4
                else:
                    HttpRequestHandler.rd += 1
                    rest = len(HttpRequestHandler.players) - HttpRequestHandler.rd
                    HttpRequestHandler.num = rest
                    if rest == HttpRequestHandler.threshold:
                        for i in range(len(HttpRequestHandler.players)):
                            if HttpRequestHandler.players[i] in HttpRequestHandler.demon:
                                HttpRequestHandler.msgQueue[HttpRequestHandler.players[i]] = 4
                            else:
                                HttpRequestHandler.msgQueue[HttpRequestHandler.players[i]] = 5
                    else:
                        for i in range(len(HttpRequestHandler.players)):
                                HttpRequestHandler.msgQueue[HttpRequestHandler.players[i]] = 2
                                HttpRequestHandler.state[HttpRequestHandler.players[i]] += 1
                        HttpRequestHandler.killed = name
                        for i in range(len(HttpRequestHandler.players)):
                            HttpRequestHandler.voteList[HttpRequestHandler.players[i]] = 0
                     
                        
                
if __name__ == '__main__':
    httpServerAddress = ('192.168.56.1', 8000)
    server = BaseHTTPServer.HTTPServer(httpServerAddress, HttpRequestHandler)
    server.serve_forever()