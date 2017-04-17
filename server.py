# -*- coding: utf-8 -*-
"""
Created on Mon Apr 17 11:41:44 2017

@author: Administrator
"""

import SocketServer
import json

class serverHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        sock = self.request
        send_msg = {'name':'tom', 'msg':'hello'}
        dump_msg = json.dumps(send_msg)
        sock.send(str.encode(dump_msg))
        sock.close()
        

if __name__ == "__main__":
    HOST, PORT = "localhost", 9999
    server = SocketServer.TCPServer((HOST, PORT), serverHandler)
    server.serve_forever()
    
