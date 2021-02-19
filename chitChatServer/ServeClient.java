package chitChatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class ServeClient extends Thread{

    private static Map<String, ServeClient> clients = new HashMap<>();

    private String clientID;
    private Socket clientSocket;
    private DataInputStream inStream;
    private DataOutputStream outStream;

    public ServeClient(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        inStream = new DataInputStream(clientSocket.getInputStream());
        outStream = new DataOutputStream(clientSocket.getOutputStream());
        clientID = inStream.readUTF();
        clients.put(clientID, this);
        //System.out.println(clients);
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void sendTo(String receiverID, String msg) throws IOException{
        if(clients.containsKey(receiverID)){
            ServeClient receiverClient = clients.get(receiverID);
            receiverClient.send(msg);
        }
        else
            send("?Offline");
    }

    public void send(int intMsg) throws IOException{
        outStream.writeInt(intMsg);
    }

    public void send(String msg) throws IOException{
        outStream.writeUTF(msg);
    }

    public String getAddress(){
        return clientSocket.getInetAddress().getHostAddress();
    }

    public String getLocalAddress(){
        return clientSocket.getLocalAddress().getHostAddress();
    }

    @Override
    public void run() {
        try{
            ServeClient receiver = null;
            while(!clientSocket.isClosed()){
               String msg = inStream.readUTF();
                System.out.println(msg);
               String []msgCode = msg.split(":");

               receiver = clients.get(msgCode[1]);
               switch (msgCode[0]){
                   case "0":
                       if(receiver != null){
                           receiver.send("0:" + clientID + ":" + msgCode[2]);
                           System.out.println(msgCode[1] + ":" + msgCode[2]);
                       }
                       else
                           send("-1:" + msgCode[1]);
                       break;

                   case "1":
                   case "2":
                       if(receiver != null){
                           String address = getAddress();
                           if(address.equals(receiver.getAddress())){
                               address = getLocalAddress();
                           }
                           receiver.send(msgCode[0] + ":" + clientID + ":" + address + ":" + msgCode[2]);
                       }
                       else
                           send("-2:" + msgCode[1]);
                       break;

                   default:
                       System.out.println("Invalid Message: " + msg);
               }

           }
        }
        catch (EOFException e){
            System.out.println( clientID + "'s connection Closed!");
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                clients.remove(clientID);
                clientSocket.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
