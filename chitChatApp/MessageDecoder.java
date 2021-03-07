package chitChat.chitChatApp;

import java.io.IOException;

public class MessageDecoder implements DataConsumer{
    private DataConsumer msgConsumer;
    private ServerChannel serverChannel;

    public MessageDecoder(DataConsumer msgConsumer){
        this.msgConsumer = msgConsumer;
    }

    public MessageDecoder(DataConsumer msgConsumer, ServerChannel serverChannel){
        this.msgConsumer = msgConsumer;
        this.serverChannel = serverChannel;
    }

    public DataConsumer getMsgConsumer() {
        return msgConsumer;
    }

    public void setMsgConsumer(DataConsumer msgConsumer) {
        this.msgConsumer = msgConsumer;
    }

    public ServerChannel getServerChannel() {
        return serverChannel;
    }

    public void setServerChannel(ServerChannel serverChannel) {
        this.serverChannel = serverChannel;
    }

    @Override
    public void consume(String ...dataStrings) throws IOException{
        FriendChannel friendChannel;
        for(String msg:dataStrings){
            String []msgCode = msg.split(":");
            switch (msgCode[0]){
                case "-1":  //offline friend <- msg send
                    msgConsumer.consume("Internet", msgCode[1]+" is offline :(", "CENTER");
                    break;

                case "-2":  //offline friend <- connect
                    msgConsumer.consume("Internet", msgCode[1] + " is offline :(", "CENTER");
                    NetworkConnection.disconnectWith(msgCode[1]);
                    break;

                case "0":   //message from friend
                    msgConsumer.consume(msgCode[1], msgCode[2], "CENTER_LEFT");
                    break;

                case "1":   //receiving connection request
                    try {
                        friendChannel = new FriendChannel(msgCode[1], msgCode[2], Integer.parseInt(msgCode[3]), msgConsumer);

                        String joinRequest = "2:" + msgCode[1] + ":" + friendChannel.getPort();
                        serverChannel.send(joinRequest);

                        NetworkConnection.friends.put(msgCode[1], friendChannel);
                        System.out.println(msgCode[1] + " connected successfully!");
                    }
                    catch (FriendChannel.ConnectException | IOException c){
                        c.printStackTrace();
                    }
                    break;

                case "2":     //receiving response of sent connection request
                    friendChannel = NetworkConnection.friends.get(msgCode[1]);
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
                    

                default:
                    System.out.println("Invalid Message!");
            }    
        }
    }
}
