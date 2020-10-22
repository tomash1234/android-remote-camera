import socket
import sys
import matplotlib.pyplot as plt
import matplotlib.image as mpimg
import PIL.Image as Image
import io
import numpy as np

COMMAND_GET = 50

SERVER_ADDRESS = "192.168.1.113"
PORT = 6660
MSGLEN = 4
ROT_LEN = 6

def myreceive(sock):
    #rotation
    rec = sock.recv(ROT_LEN)
    rot_x = rec[0]*256 + rec[1] - 180
    rot_y = rec[2]*256 + rec[3] - 180
    rot_z = rec[4]*256 + rec[5] - 180
    print('Camera rotation x:', str(rot_x) + '° y:', str(rot_y) + '° z:', str(rot_z) + '°')
    
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

    new_image = Image.fromarray(array)
    imgplot = plt.imshow(new_image)
    plt.show()
    new_image.save("out.jpg")



sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((SERVER_ADDRESS, PORT))
sock.settimeout(10)
message = bytes([COMMAND_GET])
sock.sendall(message)
myreceive(sock)

sock.close()

