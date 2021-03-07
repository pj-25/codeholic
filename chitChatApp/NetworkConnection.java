package chitChat.chitChatApp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NetworkConnection {

    private String name;
    private ServerChannel socketHandler;
    private MessageConsumer msgConsumer;
    private MessageDecoder msgDecoder;

    public static Map<String, FriendChannel> friends = new HashMap<String, FriendChannel>();

    public NetworkConnection(String name, MessageConsumer msgConsumer){
        this.name = name;
        this.msgConsumer = msgConsumer;
    }

    public NetworkConnection(String name, MessageConsumer msgConsumer, String serverIP, int serverPort) throws IOException{
        this(name, msgConsumer);
        connect(serverIP, serverPort);
    }

    public NetworkConnection(String name, ServerChannel serverChannel) throws IOException{
        this.name = name;
        connect(serverChannel);
    }

    public void connect(String serverIP, int serverPort) throws IOException{
        msgDecoder = new MessageDecoder(msgConsumer);
        connect(new ServerChannel(name, serverIP, serverPort, msgDecoder));
    }

    public void connect() throws IOException{
        msgDecoder = new MessageDecoder(msgConsumer);
        connect(new ServerChannel(name, msgDecoder));
    }

    public void connect(ServerChannel serverChannel) throws IOException{
        socketHandler = serverChannel;
        msgDecoder = (MessageDecoder) serverChannel.getDataConsumer();
        msgConsumer = (MessageConsumer) msgDecoder.getMsgConsumer();
        serverChannel.run();
        msgDecoder.setServerChannel(serverChannel);
    }

    public void connectTo(String friendID) throws IOException {
        FriendChannel friendChannel = new FriendChannel(friendID, msgConsumer);
        String connectionRequest = "1:" + friendID + ":" + friendChannel.getPort();
        socketHandler.send(connectionRequest);
        System.out.println("Connection request sent to " + friendID);
        friends.put(friendID, friendChannel);
    }

    public void sendTo(String name, String msg) throws IOException{
        FriendChannel friendChannel = friends.get(name);
        if(friendChannel!=null){
            try{
                friendChannel.writeMessage(msg);
            }
            catch (IOException e){
                System.out.println("Enable to send msg to "+name);
            }
        }
        else
            socketHandler.send("0:" + name + ":" + msg);
    }

    static public void disconnectWith(String friendID){
        friends.remove(friendID);
    }

    void close() throws IOException{
        if(socketHandler!=null)
            socketHandler.close();
        for(FriendChannel friendChannel: friends.values()){
            System.out.println( friendChannel.getFriendName() + " is disconnecting.....");
            friendChannel.close();
        }
        friends = null;
    }

    //getters and setters
    public ServerChannel getSocketHandler() {
        return socketHandler;
    }

    public void setSocketHandler(ServerChannel socketHandler) {
        this.socketHandler = socketHandler;
    }

    public MessageConsumer getMsgConsumer() {
        return msgConsumer;
    }

    public void setMsgConsumer(MessageConsumer msgConsumer) {
        this.msgConsumer = msgConsumer;
    }

    public MessageDecoder getMsgDecoder() {
        return msgDecoder;
    }

    public void setMsgDecoder(MessageDecoder msgDecoder) {
        this.msgDecoder = msgDecoder;
    }

    public static Map<String, FriendChannel> getFriends() {
        return friends;
    }

    public static void setFriends(Map<String, FriendChannel> friends) {
        NetworkConnection.friends = friends;
    }
}
