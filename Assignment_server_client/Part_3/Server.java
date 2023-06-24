import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;



public class UDPServer {
    private DatagramSocket udpSocket;
    private int port;
 
    public UDPServer(String serverName,int port) throws SocketException, IOException {
        this.port = port;
        this.udpSocket = new DatagramSocket(this.port, InetAddress.getByName(serverName));
    }


    private void listen() throws Exception {
        System.out.println("-- Running Server at " + InetAddress.getLocalHost() + "--");
        Random rand = new Random();
        while (true) {
            
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            
            /*DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 		    9876);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket =
            new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData()); */
            
            // blocks until a packet is received
            udpSocket.receive(packet);
            
            System.out.println(
                "Message from " + packet.getAddress().getHostAddress());
            Thread.sleep(rand.nextInt(200));
            udpSocket.send(packet);
        }
    }


    public static void main(String[] args) throws Exception {
        UDPServer client = new UDPServer(args[0],20021);
        client.listen();
    }
}
