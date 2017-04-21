import socket
import json
server_address, server_port = 'localhost', 9999
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((server_address, server_port))
send_msg = {'name':'tom', 'msg':'hello'}
dump_msg = json.dumps(send_msg)
sock.send(str.encode(dump_msg))
sock.close()
