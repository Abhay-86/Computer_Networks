import java.beans.Customizer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


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
        int gap = Integer.parseInt(args[0]);
        int num_pckt = Integer.parseInt(args[1]);
        int pckt_size = Integer.parseInt(args[2]);
        UDPClient sender = new UDPClient("localhost", 20021, gap);
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
        int t_P_S[] = new int[(int)(e_t-startTime+1000)/1000];
        int delayPerSec[] = new int[(int)(e_t-startTime+1000)/1000];
        int packetPerSec[] = new int[(int)(e_t-startTime+1000)/1000];
        Random rand = new Random();
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
            if(RTT>t_left || rand.nextInt(1000)%10==0){
                i++;
                n_l++;
                continue;
            }
            int cur = (int)((curTime - startTime)/1000);
            delayPerSec[cur] += Math.round((float)(RTT)/2.0);
            packetPerSec[cur]++;
            t_P_S[cur] += packet.getData().length;
            System.out.println("Packet received with RTT: "+RTT);
            average_Round_Trip_Time += (float)(RTT);
            i++;
        }
        System.out.println("<-------------------------------------------->\n"
                            +"Number of Packets Lost = "+n_l
                            +"\nAverage Round Trip Time = "+ average_Round_Trip_Time/(float)(num_pckt-n_l)+" ms");
        System.out.println("loss = "+ (float)(n_l*100)/num_pckt + "%");
        DefaultCategoryDataset throughputdataset = new DefaultCategoryDataset( );
        DefaultCategoryDataset delaydataset = new DefaultCategoryDataset( );
        /*try{
            clientSocket = new DatagramSocket();
        }
        catch(SocketException e){}
        try{
            //Send a UDP request to the server:
            System.out.print("Enter Message: ");
            String requestMessage = inputReader.readLine();
            sendRequest(requestMessage);

            //Receive a reply from the server:
            DatagramPacket reply = receiveReply();
            String receivedMessage = new String(reply.getData(), 0, reply.getLength()); // Convert the byte array data into String
            System.out.printf("Message from server: \"%s\"", receivedMessage);
        }
        catch(UnknownHostException e){}
        catch(IOException e){}
    }*/
        for(int j=0;j<t_P_S.length;j++){
            throughputdataset.addValue((Number)(t_P_S[j]), "throughput", j+1);
            delaydataset.addValue((Number)(packetPerSec[j]==0?0:delayPerSec[j]/packetPerSec[j]), "delay", j+1);
        }
        LineChart_AWT chart1 = new LineChart_AWT("Part 1.2", "Throughput","Throughput(bytes/sec)", throughputdataset);
        LineChart_AWT chart2 = new LineChart_AWT("Part 1.2", "Delay","Delay(millisec)" ,delaydataset);
        chart1.pack();
        chart2.pack();
        /*DatagramPacket packet 
          = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);*/
        RefineryUtilities.positionFrameOnScreen( chart1 ,0.2,0.5);
        RefineryUtilities.positionFrameOnScreen( chart2 ,0.8,0.5);
        chart1.setVisible( true );
        chart2.setVisible( true );
    }
}

/*public String sendEcho(String msg) {
        buf = msg.getBytes();
        
        String received = new String(
          packet.getData(), 0, packet.getLength());
        return received;
    }*/
public class LineChart_AWT extends ApplicationFrame {

    public LineChart_AWT( String applicationTitle , String chartTitle, String dataLabel ,DefaultCategoryDataset dataset ) {
       super(applicationTitle);
       JFreeChart lineChart = ChartFactory.createLineChart(
          chartTitle,
          "Time(sec)",dataLabel,
          dataset,
          PlotOrientation.VERTICAL,
          true,true,false);
          
       ChartPanel chartPanel = new ChartPanel( lineChart );
       chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
       setContentPane( chartPanel );
    }
 }
