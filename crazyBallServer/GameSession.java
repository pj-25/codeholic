package crazyBallServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class GameSession{
    private DatagramPacket[] playerDatagramPackets;
    private String []playerNames;
    private boolean isRunning = false;
    private DatagramSocket serverSocket;

    public GameSession(DatagramSocket serverSocket, DatagramPacket player1, String player1Name) {
        this.serverSocket = serverSocket;
        playerNames = new String[2];
        playerDatagramPackets = new DatagramPacket[2];

        playerDatagramPackets[0] = player1;
        playerNames[0]  = player1Name;
    }

    public GameSession(DatagramSocket serverSocket, DatagramPacket player1, String player1Name, DatagramPacket player2, String player2Name){
        this(serverSocket, player1, player1Name);
        joinOpponent(player2, player2Name);
    }

    public DatagramPacket[] getPlayerDatagramPackets() {
        return playerDatagramPackets;
    }

    public void setPlayerDatagramPackets(DatagramPacket[] playerDatagramPackets) {
        this.playerDatagramPackets = playerDatagramPackets;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void joinOpponent(DatagramPacket player2, String player2Name){
        playerDatagramPackets[1] = player2;
        playerNames[1] = player2Name;
    }

    public void start(){
        isRunning = true;
        try{
            send(0, playerNames[1]);
            send(1, playerNames[0]);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    void send(int to, String msg) throws IOException{
        byte []msgBytes = msg.getBytes();
        playerDatagramPackets[to].setData(msgBytes);
        playerDatagramPackets[to].setLength(msgBytes.length);
        serverSocket.send(playerDatagramPackets[to]);
    }
}
