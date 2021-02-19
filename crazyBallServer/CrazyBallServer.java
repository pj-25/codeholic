package crazyBallServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;


public class CrazyBallServer {
    public static final int PORT = 8383;
    private DatagramSocket socket;
    private boolean isRunning = true;

    public static Map<String, GameSession> gameSessions = new HashMap<String, GameSession>();


    public static void main(String []s) throws IOException{
        CrazyBallServer server = new CrazyBallServer();
        server.run();
    }

    public CrazyBallServer() throws IOException {
        socket = new DatagramSocket(PORT);
    }

    public CrazyBallServer(int port) throws IOException{
        socket = new DatagramSocket(port);
    }

    public void run(){
        try{
            while(isRunning){
                byte []buffer = new byte[512];
                DatagramPacket playerPacket = new DatagramPacket(buffer, buffer.length);

                socket.receive(playerPacket);
                String msg = new String(playerPacket.getData());

                GameSession game = null;

                String []msgCode = msg.trim().split(":");

                /*
                for(String m:msgCode){
                    System.out.println(m + ":" + m.length());
                }*/
                switch (msgCode[0]) {
                    case "0":
                    case "1":
                        game = gameSessions.get(msgCode[1]);
                        if(game != null && game.isRunning())
                            game.send(Integer.parseInt(msgCode[0]), msgCode[2]);
                        break;

                    case "2":
                        GameSession newGame = new GameSession(socket, playerPacket, msgCode[1]);
                        String gameCode = msgCode[1] + "_" + newGame.hashCode();

                            gameSessions.put(gameCode, newGame);
                        System.out.println(msgCode[1] + " launched game session: " + gameCode);

                        newGame.send(0, gameCode);
                        break;

                    case "3":
                        if (gameSessions.containsKey(msgCode[1])) {
                            game = gameSessions.get(msgCode[1]);
                            game.joinOpponent(playerPacket, msgCode[2]);
                            game.start();
                        } else {
                            playerPacket.setData("?InvalidGameCode".getBytes());
                            socket.send(playerPacket);
                        }
                        break;

                    default:
                        playerPacket.setData("?InvalidRequest".getBytes());
                        socket.send(playerPacket);
                        break;
                }
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
