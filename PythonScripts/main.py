import socket
import sys
import matplotlib.pyplot as plt
import matplotlib.image as mpimg
import PIL.Image as Image
import io
import numpy as np

COMMAND_GET = 50

SERVER_ADDRESS = "192.168.14.173"
PORT = 6660
MSGLEN = 4

def myreceive(sock):
    rec = sock.recv(MSGLEN)
    width = rec[0]*256 + rec[1]
    height = rec[2]*256 + rec[3]
    print('Picture dimensions ', width, height)
    bytes_wait = width * height * 3
    rec_data = 0
    data = list()
    while rec_data < bytes_wait:
        rec = sock.recv(2048)
        data += rec
        rec_data+=len(rec)
    print('Picture data ', len(data))
    createImage(width, height, data)
    
def createImage(width, height, data):
    array = np.array(data, dtype=np.uint8)
    array = np.reshape(array, (height, width, 3))
    print(array.shape)

    new_image = Image.fromarray(array)
    imgplot = plt.imshow(new_image)
    plt.show()



sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((SERVER_ADDRESS, PORT))
sock.settimeout(10)
message = bytes([COMMAND_GET])
sock.sendall(message)
myreceive(sock)

sock.close()

