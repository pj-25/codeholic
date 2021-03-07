package chitChat.chitChatApp;

import java.io.IOException;
import java.net.*;

public class FriendChannel {

    public String friendName;
    private DatagramPacket writerPacket;
    private DatagramPacket readerPacket;
    private DatagramSocket readerSocket;
    private final DatagramSocket writerSocket;
    private boolean isConnected = false;
    private DataConsumer dataConsumer;

    public FriendChannel(String friendName, DataConsumer dataConsumer) throws IOException{
        this.friendName = friendName;
        readerSocket = new DatagramSocket();
        writerSocket = new DatagramSocket();
        readerPacket = new DatagramPacket(new byte[256], 256);
        writerPacket = new DatagramPacket(new byte[256], 256);
        this.dataConsumer = dataConsumer;
    }

    public FriendChannel(String friendName, String connectionAddress, int port, DataConsumer dataConsumer) throws IOException, ConnectException {
        this(friendName, dataConsumer);
        connect(connectionAddress, port);
    }

    public DatagramSocket getSocket(){
        return readerSocket;
    }

    public int getPort(){
        return readerSocket.getLocalPort();
    }

    public String getAddress(){
        return readerSocket.getLocalAddress().getHostAddress();
    }

    public void setSocket(DatagramSocket socket){
        this.readerSocket = socket;
    }

    public void holePunch(){
        new Thread(()->{
            while(!isConnected){
                try{
                    byte []holePunchMsg = friendName.getBytes();
                    writerPacket.setData(holePunchMsg);
                    writerPacket.setLength(holePunchMsg.length);
                    readerSocket.send(writerPacket);
                    System.out.println("Hole punched using " + readerSocket.getLocalSocketAddress() + " to " + writerPacket.getSocketAddress());
                    Thread.sleep(2000);
                }
                catch (IOException e){
                    System.out.println("Unable to punch hole!!");
                }
                catch (InterruptedException x){
                    System.out.println("hole punching exited...");
                    break;
                }
            }
        }).start();
    }

    public void connect(String connectionAddress, int port) throws IOException, ConnectException{
        SocketAddress socketAddress = new InetSocketAddress(connectionAddress, port);
        System.out.println( "connected to:" + socketAddress);

        writerPacket.setAddress(Inet4Address.getByName(connectionAddress));
        readerPacket.setAddress(Inet4Address.getByName(connectionAddress));

        writerPacket.setPort(port);
        readerPacket.setPort(port);

        writerSocket.connect(Inet4Address.getByName(connectionAddress), port);

        System.out.println("{" + writerSocket.getInetAddress() + "}");
        isConnected = true;

        listen();
        holePunch();
    }

    public void listen() throws ConnectException {
        if(isConnected){
            new Thread(()->{
                while(!readerSocket.isClosed()){
                    try {
                        readerPacket = new DatagramPacket(new byte[256], 256);
                        System.out.println("Waiting to receive msg from " + friendName + ".......");
                        readerSocket.receive(readerPacket);
                        String []msgCode = new String(readerPacket.getData()).trim().split(":");
                        switch (msgCode[0]){
                            case "0":
                                close();
                                dataConsumer.consume("Internet", friendName + " disconnected :(", "CENTER");
                                break;

                            case "1":
                                dataConsumer.consume(friendName, msgCode[1], "CENTER_LEFT");
                                break;

                            default:
                                System.out.println("Error: Invalid message!");
                        }
                    }
                    catch (IOException e){
                        System.out.println(friendName + " disconnected!");
                    }
                }
            }).start();
        }
        else
            throw new ConnectException();
    }


    public void write(String msg) throws IOException{
        byte []msgBytes = msg.getBytes();
        writerPacket.setData(msgBytes);
        writerPacket.setLength(msgBytes.length);
        System.out.println(msg + " -> sent to " + writerPacket.getSocketAddress());
        writerSocket.send(writerPacket);
    }

    public void writeMessage(String msg) throws IOException{
        write("1:"+ msg);
    }

    public void sendCloseRequest() throws IOException{
        write("0:" + "?Connection closed!");
    }

    public void close() throws IOException{
        sendCloseRequest();
        readerSocket.close();
        writerSocket.close();
        NetworkConnection.disconnectWith(friendName);
        isConnected = false;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    static class ConnectException extends Exception{
        public ConnectException(){
            super("Connection not established!");
        }
    }
}
