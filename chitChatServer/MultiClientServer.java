package chitChatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class MultiClientServer {
    private final static int PORT_NUMBER = 9898;

    private ServerSocket serverSocket = null;
    private boolean isRunning = true;

    public MultiClientServer() throws IOException {
        this(PORT_NUMBER);
    }

    public MultiClientServer(int port) throws IOException{
        serverSocket = new ServerSocket(port);
    }

    public static void main(String []s) throws IOException{
        MultiClientServer multiClientServer = new MultiClientServer();
        multiClientServer.run();
    }

    public void run(){
        try{
            serverSocket.setSoTimeout(0);

            while(isRunning){
                Socket clientSocket = serverSocket.accept();
                ServeClient serveClientThread = new ServeClient(clientSocket);
                serveClientThread.start();
                System.out.println(clientSocket + "{" + clientSocket.getLocalSocketAddress() +"}");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
