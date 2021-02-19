package crazyBallGame;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class SendHandler {
    private int[] buffer;
    private int start;
    private int end;
    private final int WINDOW_SIZE = 3;
    private final int BUFFER_SIZE = 10;
    private boolean isReady = false;

    private DatagramSocket sender;
    private DatagramPacket dataPacket;

    public SendHandler(DatagramSocket datagramSocket, DatagramPacket datagramPacket) {
        sender = datagramSocket;
        dataPacket = datagramPacket;
        buffer = new int[BUFFER_SIZE];
        start = 0;
        end = -1;
    }

    public void write(int data) {
        buffer[++end] = data;

    }

    public void send(int i) {

    }

    public void send() {

    }

}

