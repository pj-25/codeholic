package chitChat.chitChatApp;

import javafx.geometry.Pos;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class ServerChannel {
    private String name;
    private Socket socket = null;
    private DataInputStream inStream = null;
    private DataOutputStream outStream = null;
    private DataConsumer dataConsumer;

    public static final int SERVER_PORT = 9898;
    public static final String SERVER_IP = "18.191.252.245"; //18.191.252.245

    public ServerChannel(String name, DataConsumer dataConsumer) throws IOException{
        this(name, SERVER_IP, SERVER_PORT, dataConsumer);
    }

    public ServerChannel(String name, String serverIP, int port, DataConsumer dataConsumer) throws IOException{
        this.name = name;
        this.dataConsumer = dataConsumer;
        connect(serverIP, port);
    }

    public DataConsumer getDataConsumer() {
        return dataConsumer;
    }

    public void setDataConsumer(DataConsumer dataConsumer) {
        this.dataConsumer = dataConsumer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DataInputStream getInStream() {
        return inStream;
    }

    public void setInStream(DataInputStream inStream) {
        this.inStream = inStream;
    }

    public DataOutputStream getOutStream() {
        return outStream;
    }

    public void setOutStream(DataOutputStream outStream) {
        this.outStream = outStream;
    }

    public boolean isRunning() {
        return !socket.isClosed();
    }

    void connect(String serverIP, int port) throws IOException{
        if(socket != null){
            close();
        }
        socket = new Socket(InetAddress.getByName(serverIP), port);
        inStream = new DataInputStream(socket.getInputStream());
        outStream = new DataOutputStream(socket.getOutputStream());
        send(name);
    }

    public void run() {
        new Thread(()->{
            while(isRunning()){
                try {
                    System.out.println("Waiting for msg......");
                    String msg = inStream.readUTF();
                    System.out.println("Received message: " +msg);
                    dataConsumer.consume(msg);
                }
                catch (IOException e){
                    try{
                        close();
                    }
                    catch (IOException x){
                        x.printStackTrace();
                    }
                    System.out.println("Connection Closed!!");
                }
            }
        }).start();
    }

    public void send(String msg) throws IOException{
        outStream.writeUTF(msg);
        System.out.println("Sent msg >>" + msg);
    }

    public void close() throws IOException{
        inStream.close();
        outStream.close();
        socket.close();
    }
}
