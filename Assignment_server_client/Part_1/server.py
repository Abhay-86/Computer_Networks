from time import sleep
import  os 
from decimal import Decimal
from socket import AF_INET , timeout ,  SOCK_DGRAM ,  socket
from random import choice , randint
from datetime import datetime as dt
from string import ascii_lowercase ,  digits




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
    host = socket.gethostname()
    port = 5000

    server_socket = socket.socket()
    server_socket.bind((host, port))
    server_socket.listen(2)
    conn, address = server_socket.accept()
    print("Connection from: " + str(address))
    while True:
        data = conn.recv(1024).decode()
        if not data:
            break
        print("from connected user: " + str(data))
        data = input(' -> ')
        conn.send(data.encode())

    conn.close() 


def accept_wrapper(sock):
    conn, addr = sock.accept()  # Should be ready to read
    print(f"Accepted connection from {addr}")
    conn.setblocking(False)
    data = types.SimpleNamespace(addr=addr, inb=b"", outb=b"")
    events = selectors.EVENT_READ | selectors.EVENT_WRITE
    sel.register(conn, events, data=data)
    

IP_Address = "127.0.0.1"  
Size_of_Buffer = 4096
Port  = 20001 

 
IP_Port_server = ( IP_Address, Port)
Severs_UDP_Socket = socket(family = AF_INET, type = SOCK_DGRAM) 
Severs_UDP_Socket.bind(IP_Port_server) 

print("Server started......")
 
while True:
    data, fromAddress = Severs_UDP_Socket.recvfrom(Size_of_Buffer) 
     
    Data_from_Client = data.decode()
    print("Client data", Data_from_Client)    
    if Data_from_Client == 'exit':  
         Severs_UDP_Socket.sendto("Server Exiting the Connection".encode(), fromAddress) 
         print("Terminated")
         break
    
    elif Data_from_Client[0] == 'C':
        print("Clinet IP address :: ", fromAddress)
        '''
        name, addr1 = UDPServerSocket.recvfrom(bufferSize) 
 
   	# receiving pwd from client
   	pwd, addr1 = UDPServerSocket.recvfrom(bufferSize) 
 
   	name = name.decode() 
   	pwd = pwd.decode()
   	msg =''
   	if name not in di:
       	msg ='name does not exists'
       	flag = 0
   	for i in di:
      	   if i == name:
             if di[i]== pwd:
               msg ="pwd match"
               flag = 1
             else:
               msg ="pwd wrong"
       bytesToSend = str.encode(msg)
       UDPServerSocket.sendto(bytesToSend, addr1) 
        '''
        Size_of_Buffer = int(Data_from_Client.split(':')[1]) 
        Severs_UDP_Socket.sendto(("Current buffer size is "+str(Size_of_Buffer)).encode(), fromAddress) 
        continue
    elif(Data_from_Client[0] == 'F'): 
       Data_from_Client = Data_from_Client.strip('F :')
       filename = Data_from_Client
       path_to_script = os.path.dirname(os.path.abspath(__file__))
       my_filename = os.path.join(path_to_script, filename)   
       present = os.path.exists(my_filename) 
       Words = [] 
       '''
       while True:
    	print >>sys.stderr, '\nwaiting to receive message'
    	data, address = sock.recvfrom(4096)
    
    	print >>sys.stderr, 'received %s bytes from %s' % (len(data), address)
    	print >>sys.stderr, data
    
    		if data:
        	   sent = sock.sendto(data, address)
                  print >>sys.stderr, 'sent %s bytes back to %s' % (sent, address)
       '''
       if(present): 

         with open(my_filename, "r") as handle:
           Lines = handle.readlines() 
           index  = 0 
           Severs_UDP_Socket.sendto(Lines[0].encode(), fromAddress) 
           for  i in range(1 , len(Lines)) : 
                for j in Lines[i].split(' ') : 
                     Words.append(j.strip('\n'))
           
         index = 0 
         while True :   
          
              word = Words[index]
              data, fromAddress = Severs_UDP_Socket.recvfrom(Size_of_Buffer) 
              data = data.decode()
              '''
              try:

    		# Send data
    		print >>sys.stderr, 'sending "%s"' % message
    		sent = sock.sendto(message, server_address)

    		# Receive response
    		print >>sys.stderr, 'waiting to receive'
    		data, server = sock.recvfrom(4096)
    		print >>sys.stderr, 'received "%s"' % data

	      finally:
                print >>sys.stderr, 'closing socket'
                sock.close()

              ''' 
              print("Client :: " , data)
              if(data == ( "Word_#"+str(index+1))) : 
                    Severs_UDP_Socket.sendto(Words[index].encode(), fromAddress)
                    index+=1  
              if(word == "EOF") : 
                      Severs_UDP_Socket.sendto('File Transfer Finished ;-)'.encode(), fromAddress)
                      exit(0)
                      break       
       else :  
         Severs_UDP_Socket.sendto('404: File-not-Found'.encode(), fromAddress)
         
