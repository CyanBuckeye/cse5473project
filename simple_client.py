import socket
import json
server_address, server_port = 'localhost', 9999
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((server_address, server_port))
data = sock.recv(1024)
sock.close()

data = json.loads(data)
print(data['name'])
