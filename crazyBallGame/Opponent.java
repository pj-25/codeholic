package crazyBallGame;

import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Opponent extends Player{
    private DatagramSocket socket;
    private DatagramPacket datagramPacket;

    private int buffer;
    private int start;
    private int end;
    private final int WINDOW_SIZE = 3;
    private final int BUFFER_SIZE = 10;
    private boolean isReady = false;

    public Opponent(String playerName,int playerCode, double x, double y, double width, double height){
        super(playerName, playerCode, x, y, width, height);
        start = 0;
        end = -1;
    }

    public Opponent(int playerCode, double x, double y, double width, double height){
        this("opponent", playerCode, x, y, width, height);
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public DatagramPacket getDatagramPacket() {
        return datagramPacket;
    }

    public void setDatagramPacket(DatagramPacket datagramPacket) {
        this.datagramPacket = datagramPacket;
    }

    public void connect(DatagramSocket socket, DatagramPacket datagramPacket){
        this.socket = socket;
        this.datagramPacket = datagramPacket;
    }

    public void connect(String serverIP, int serverPort) throws IOException{
        socket = new DatagramSocket();
        byte[] buffer = new byte[512];
        datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(serverIP), serverPort);
    }

    public void sendJoinRequest(String gameCode, String playerName) throws IOException{
        send("3:" + gameCode + ":" + playerName );
    }

    public void receiveName() throws IOException{
        receiveName(0);
    }

    public void receiveName(int timeout) throws IOException{
        try {
            socket.setSoTimeout(timeout);
            datagramPacket.setData(new byte[512]);
            socket.receive(datagramPacket);
            String recvMsg = new String(datagramPacket.getData()).trim();
            if(recvMsg.startsWith("?"))
                throw new IOException(recvMsg);
            else{
                setName(recvMsg);
                listenMoves();
            }
        }
        catch (SocketTimeoutException e){
            throw new IOException("Server Down");
        }
        finally {
            socket.setSoTimeout(0);
        }
    }

    public void listenMoves(){
        new Thread(()->{
            try{
                while(!socket.isClosed()){
                    datagramPacket.setData(new byte[512]);
                    socket.receive(datagramPacket);
                    String move = new String(datagramPacket.getData()).trim();

                    if(move.equals("0")){
                        Platform.runLater(super::moveUp);
                    }
                    else if(move.equals("1")){
                        Platform.runLater(super::moveDown);
                    }
                    else {
                        System.out.println("Invalid Move");
                    }
                }
            }
            catch (IOException e){
                System.out.println("Player disconnected!");
            }
        }).start();
    }

    public void disconnect(){
        socket.close();
    }

    public void send(@NotNull String msg) throws IOException{
        byte []msgBytes = msg.getBytes();
        datagramPacket.setData(msgBytes);
        datagramPacket.setLength(msgBytes.length);
        socket.send(datagramPacket);
    }

    @Override
    public void moveUp(){
        super.moveUp();
        sendMove("0");
    }

    @Override
    public void moveDown(){
        super.moveDown();
        sendMove("1");
    }

    public void sendMove(String move) {
       new Thread(()->{
           try {

               send(getPlayerCode() + ":" + move );
           }
           catch (IOException e){
               e.printStackTrace();
           }
       }).start();
    }

}
