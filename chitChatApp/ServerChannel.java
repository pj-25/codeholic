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
            FriendChannel friendChannel;
            while(isRunning()){
                try {
                    System.out.println("Waiting for msg......");
                    String msg = inStream.readUTF();
                    System.out.println("Received message: " +msg);
                    String []msgCode = msg.split(":");
                        switch (msgCode[0]){
                            case "-1":
                                dataConsumer.consume("Internet", msgCode[1]+" is offline :(", "CENTER");
                                break;

                            case "-2":
                                dataConsumer.consume("Internet", msgCode[1] + " is offline :(", "CENTER");
                                ChitChatClient.friends.remove(msgCode[1]);
                                break;

                            case "0":   //message from friend
                                dataConsumer.consume(msgCode[1], msgCode[2], "CENTER_LEFT");
                                break;

                            case "1":   //receiving connection request
                                try {
                                    friendChannel = new FriendChannel(msgCode[1], msgCode[2], Integer.parseInt(msgCode[3]), dataConsumer);

                                    String joinRequest = "2:" + msgCode[1] + ":" + friendChannel.getPort();
                                    send(joinRequest);

                                    ChitChatClient.friends.put(msgCode[1], friendChannel);
                                    System.out.println(msgCode[1] + " connected successfully!");
                                }
                                catch (FriendChannel.ConnectException | IOException c){
                                    c.printStackTrace();
                                }
                                break;

                            case "2":     //receiving response of sent connection request
                                friendChannel = ChitChatClient.friends.get(msgCode[1]);
                                if(friendChannel != null) {
                                    try {
                                        friendChannel.connect(msgCode[2], Integer.parseInt(msgCode[3]));
                                        System.out.println(msgCode[1] + " accepted your connection request!");
                                    }
                                    catch (FriendChannel.ConnectException c){
                                        c.printStackTrace();
                                    }
                                }else{
                                    System.out.println("Invalid message: " + msg);
                                }
                                break;

                            case "3":


                            default:
                                System.out.println("Invalid Message!");
                        }
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
