# we should get all the necessary libraries required for building the socket
from socket import AF_INET , timeout ,  SOCK_DGRAM ,  socket
# Now we will first import all necessary libraries
import os
from datetime import datetime as dt
from time import sleep
from decimal import Decimal
from string import ascii_lowercase ,  digits
from random import choice , randint


def accept_wrapper(sock):
    conn, addr = sock.accept()  # Should be ready to read
    print(f"Accepted connection from {addr}")
    conn.setblocking(False)
    data = types.SimpleNamespace(addr=addr, inb=b"", outb=b"")
    events = selectors.EVENT_READ | selectors.EVENT_WRITE
    sel.register(conn, events, data=data)
    
def service_connection(key, mask):
    sock = key.fileobj
    data = key.data
    if mask & selectors.EVENT_READ:
        recv_data = sock.recv(1024)  # Should be ready to read
        if recv_data:
            data.outb += recv_data
        else:
            print(f"Closing connection to {data.addr}")
            sel.unregister(sock)
            sock.close()
    if mask & selectors.EVENT_WRITE:
        if data.outb:
            print(f"Echoing {data.outb!r} to {data.addr}")
            sent = sock.send(data.outb)  # Should be ready to write
            data.outb = data.outb[sent:]

def start_connections(host, port, num_conns):
    server_addr = (host, port)
    for i in range(0, num_conns):
        connid = i + 1
        print(f"Starting connection {connid} to {server_addr}")
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.setblocking(False)
        sock.connect_ex(server_addr)
        events = selectors.EVENT_READ | selectors.EVENT_WRITE
        data = types.SimpleNamespace(
            connid=connid,
            msg_total=sum(len(m) for m in messages),
            recv_total=0,
            messages=messages.copy(),
            outb=b"",
        )
        sel.register(sock, events, data=data)

def server_program():
    # get the hostname
    host = socket.gethostname()
    port = 5000  # initiate port no above 1024

    server_socket = socket.socket()  # get instance
    # look closely. The bind() function takes tuple as argument
    server_socket.bind((host, port))  # bind host address and port together

    # configure how many client the server can listen simultaneously
    server_socket.listen(2)
    conn, address = server_socket.accept()  # accept new connection
    print("Connection from: " + str(address))
    while True:
        # receive data stream. it won't accept data packet greater than 1024 bytes
        data = conn.recv(1024).decode()
        if not data:
            # if data is not received break
            break
        print("from connected user: " + str(data))
        data = input(' -> ')
        conn.send(data.encode())  # send data to the client

    conn.close()  # close the connection


Port  = 20001
IP_Port_Server = ("127.0.0.1", Port)

FileName = input("File name enter karo :: ") 
size_of_Buffer =  int(input("Buffer Size enter karo :: "))
Client_UDP_socket = socket(family = AF_INET, type = SOCK_DGRAM)



Client_UDP_socket.settimeout(1) 
print("Socket created successfully ^v^")
msg_calibration = ("C :" + str(size_of_Buffer)).encode()
reached_pkts = 0 
RTT_AVG = 0 
print("----------------------Buffer Size ---------------------")
Client_UDP_socket.sendto(msg_calibration, IP_Port_Server)
Msg_Ack_Recieved = Client_UDP_socket.recvfrom(size_of_Buffer)
print("Server  : " , Msg_Ack_Recieved)
"""
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    conn, addr = s.accept()
    with conn:
        print(f"Connected by {addr}")
        while True:
            data = conn.recv(1024)
            if not data:
                break
            conn.sendall(data)
""" 
msg_filename = ("F :" + FileName).encode()   
Client_UDP_socket.sendto(msg_filename, IP_Port_Server)  
Reply_From_Server =  Client_UDP_socket.recvfrom(size_of_Buffer)[0].decode() 
if(Reply_From_Server ==  '404: File-not-Found'):  
    print("File hi nhi mill gai" , Reply_From_Server )
    Client_UDP_socket.sendto('exit'.encode(), IP_Port_Server) 
else : 
    print("Secret message.....mat open karna")
    filename =  ''.join([choice(ascii_lowercase + digits) for _ in range(randint(3, 5))]) 
    path_to_script = os.path.dirname(os.path.abspath(__file__))
    my_filename = os.path.join(path_to_script, filename) 
    '''
    while message.lower().strip() != 'bye':
        client_socket.send(message.encode())  # send message
        data = client_socket.recv(1024).decode()  # receive response

        print('Received from server: ' + data)  # show in terminal

        message = input(" -> ")  # again take input

    client_socket.close()  # close the connection
    '''
    word_number  = 1
    Client_UDP_socket.sendto(('Word_#'+str(word_number)).encode(), IP_Port_Server) 
    Reply_From_Server =  Client_UDP_socket.recvfrom(size_of_Buffer)[0].decode() 
    with open(my_filename, "w") as handle:
        while(Reply_From_Server != "EOF"):    
           print(Reply_From_Server, file=handle) 
           word_number+=1 
           Client_UDP_socket.sendto(('Word_#'+str(word_number)).encode(), IP_Port_Server) 
           Reply_From_Server =  Client_UDP_socket.recvfrom(size_of_Buffer)[0].decode() 
        
     

Client_UDP_socket.sendto('exit'.encode(), IP_Port_Server)
exit_msg = Client_UDP_socket.recvfrom(size_of_Buffer)
print("Finished")

 
