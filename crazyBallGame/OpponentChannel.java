package crazyBallGame;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.Socket;

public class OpponentChannel extends Player{
    private Socket socket;
    private DataInputStream inStream;
    private DataOutputStream outStream;

    public OpponentChannel(String playerName, int playerCode, double x, double y, double width, double height){
        super(playerName, playerCode, x, y, width, height);
    }

    public OpponentChannel(int playerCode, double x, double y, double width, double height){
        super("opponent", playerCode, x,y,width,height);
    }


}
