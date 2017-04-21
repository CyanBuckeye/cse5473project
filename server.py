# -*- coding: utf-8 -*-
"""
Created on Mon Apr 17 11:41:44 2017

@author: Administrator
"""

import socketserver
import json
import copy 

user_conns = {}

class serverHandler(socketserver.BaseRequestHandler):
    def handle(self):
        sock = self.request
        data = sock.recv(1024)
        jdata = json.loads(data)
        
        if (jdata['action'] == 'join'):
            username = jdata['username']
            user_conns[username] = self.request
            for c in user_conns:
                print(c + ": " + str(user_conns[c]))
            print("")
        else:
            print(jdata)
        

if __name__ == "__main__":
    HOST, PORT = "localhost", 9999
    server = socketserver.TCPServer((HOST, PORT), serverHandler, True)
    server.serve_forever()
    
