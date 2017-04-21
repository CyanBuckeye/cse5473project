# -*- coding: utf-8 -*-
"""
Created on Mon Apr 17 20:45:48 2017

@author: Administrator
"""

import BaseHTTPServer
import json

class HttpRequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    def _set_header(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        
    def do_GET(self):
        self._set_header()
        msg = {'name':'Tom', 'msg':'hello-world'}
        msg = json.dumps(msg)
        self.wfile.write(msg)
        
if __name__ == '__main__':
    httpServerAddress = ('', 8000)
    server = BaseHTTPServer.HTTPServer(httpServerAddress, HttpRequestHandler)
    server.serve_forever()