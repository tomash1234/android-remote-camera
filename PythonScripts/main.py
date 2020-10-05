import socket
import sys

SERVER_ADDRESS = "192.168.1.113"
PORT = 6660

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((SERVER_ADDRESS, PORT))
message = bytes([50])
sock.sendall(message)

sock.close()
