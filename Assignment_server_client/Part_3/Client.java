import java.beans.Customizer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class UDPClient{
    protected DatagramSocket udpSocket;
    protected InetAddress serverAddress;
    protected int port;
    protected static final int t_left = 100;
    // private Scanner scanner;


    private UDPClient(String going_addr, int port, int gap) throws IOException {
        this.serverAddress = InetAddress.getByName(going_addr);
        this.port = port;
        udpSocket = new DatagramSocket();
        udpSocket.setSoTimeout(gap+t_left);
        // scanner = new Scanner(System.in);
    }
/*   {private void service() throws IOException {
        while (true) {
            DatagramPacket request = new DatagramPacket(new byte[1], 1);
          socket.receive(request);
 
            String quote = getRandomQuote();
            byte[] buffer = quote.getBytes();
 
            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();
 
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(response);
        }
    }
 
    private void loadQuotesFromFile(String quoteFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(quoteFile));
        String aQuote;
 
        while ((aQuote = reader.readLine()) != null) {
            listQuotes.add(aQuote);
        }
 
        reader.close();
    }} */

    public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
        String going_addr = args[0];    
        int gap = Integer.parseInt(args[1]);
        int num_pckt = Integer.parseInt(args[2]);
        int pckt_size = Integer.parseInt(args[3]);
        UDPClient sender = new UDPClient(going_addr, 20021, gap);
        /*    private void loadQuotesFromFile(String quoteFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(quoteFile));
        String aQuote;
 
        while ((aQuote = reader.readLine()) != null) {
            listQuotes.add(aQuote);
        }
 
        reader.close();
    }*/
        System.out.println("-- Running UDP Client at " + InetAddress.getLocalHost() + " --");
        sender.start(sender,gap, num_pckt, pckt_size);
    }


    private void start(UDPClient client,int gap, int num_pckt, int pckt_size) throws IOException, InterruptedException { 
        Thread receiverThread = new Thread(new receivePackets(client, gap, num_pckt, t_left));
        /*InetAddress clientAddress = request.getAddress();
int clientPort = request.getPort();
 
String data = "Message from server";
buffer = data.getBytes();
 
DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
socket.send(response);*/
        Thread senderThread = new Thread(new sendPackets(client, gap, num_pckt, pckt_size));
        receiverThread.start();
        senderThread.start();
    }
    

    protected long getTimestamp(byte buf[]){
        long timestamp =0;
        int i=0;
        while(i<8){
            timestamp = timestamp<<8;
            timestamp |= buf[i] & 0xFF;
            i++;
        }
        return timestamp;
    }

    protected byte[] pushTimeinDgram(long timestamp, int pckt_size){
        byte buf[] = new byte[pckt_size];
        int i=7;
        long mask = (long)(1<<8) -1;
        while(i>=0){
            buf[i--] = (byte)(timestamp & mask);
            timestamp = timestamp>>8;
        }
        return buf;
    }

}
/*
    public static DatagramPacket getRequest() throws IOException{
        byte[] buffer = new byte[512];
        DatagramPacket request = new DatagramPacket(buffer, 512);
        serverSocket.receive(request);
        return request;
    }

    public static void sendReply(DatagramPacket request, String message) throws UnknownHostException, IOException{
        byte[] byteEncodedMessage = message.getBytes(); // Encode our quote into byte array
        InetAddress targetAddress = request.getAddress(); // Get the address of whoever sent a request datagram packet to our server

        DatagramPacket reply = new DatagramPacket(byteEncodedMessage, byteEncodedMessage.length, targetAddress, 18);
        serverSocket.send(reply);
    }*/


class sendPackets implements Runnable{

    private int gap, num_pckt, pckt_size;
    UDPClient client;


    public sendPackets(UDPClient client, int gap, int num_pckt, int pckt_size ){
        this.client = client;
        this.gap = gap;
        this.num_pckt = num_pckt;
        this.pckt_size = pckt_size;
    }

    public void run(){
    /* if (received.equals("end")) {
                running = false;
                continue;
            }*/
            for(int i=0;i<num_pckt;i++){
            long timestamp = System.currentTimeMillis();
            
            byte buf[] = client.pushTimeinDgram(timestamp, pckt_size);
            DatagramPacket p = new DatagramPacket(buf, buf.length, client.serverAddress, client.port);
            System.out.println("sent at: " + timestamp);
            try {
                client.udpSocket.send(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            try {
                Thread.sleep(gap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class receivePackets implements Runnable{

    private int gap, num_pckt;
    private UDPClient client;
    private int t_left;

    public receivePackets(UDPClient client, int gap, int num_pckt, int t_left ){
        this.client = client;
        this.gap = gap;
        this.num_pckt = num_pckt;
        this.t_left = t_left;
    }

    public void run(){
        long startTime = System.currentTimeMillis();
        int i=0;
        int n_l = 0;
        long ex = 1000;
        long e_t = startTime+ gap*num_pckt + t_left + ex;
        float average_Round_Trip_Time = 0;
        /*public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Syntax: QuoteServer <file> <port>");
            return;
        }
 
        String quoteFile = args[0];
        int port = Integer.parseInt(args[1]);
 
        try {
            QuoteServer server = new QuoteServer(port);
            server.loadQuotesFromFile(quoteFile);
            server.service();
        } catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }*/
        while(num_pckt>i && System.currentTimeMillis()<e_t){
            byte buf[] = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                client.udpSocket.receive(packet);
            } catch (IOException e) {
                System.out.println("Receive Timeout");
            }
            long curTime = System.currentTimeMillis();
            //packet = new DatagramPacket(buf, buf.length, address, port);
            long sentTime = client.getTimestamp(packet.getData());
            int RTT = (int)(curTime-sentTime);
            // String received = new String(packet.getData(), 0, packet.getLength());
            if(RTT>t_left){
                n_l++;
            }
            else{
            	System.out.println("Packet received with RTT: "+RTT+" ms");
                average_Round_Trip_Time+=(float)RTT;
            }
            System.out.println("Packet received with RTT: "+RTT);
            average_Round_Trip_Time += (float)(RTT);
            i++;
        }
        System.out.println("<-------------------------------------------->\n"
                            +"Number of Packets Lost = "+n_l
                            +"\nAverage Round Trip Time = "+ average_Round_Trip_Time/(float)(num_pckt-n_l)+" ms");
        System.out.println("loss = "+ (float)(n_l*100)/num_pckt + "%");
    }
 }
