package chitChatApp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;


public class ChitChatClient extends Application {

    private String name;
    private ServerChannel socketHandler;
    private Stage primaryStage;

    static VBox displayPane;
    static ScrollPane scrollWindow = null;

    public static Map<String, FriendChannel> friends = new HashMap<String, FriendChannel>();

    public ChitChatClient() {}

    public void connectTo(String friendID) throws IOException{
        FriendChannel friendChannel = new FriendChannel(friendID);
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

    static public void disconnect(String friendID){
        friends.remove(friendID);
    }

    public void connect() throws IOException{
        socketHandler = new ServerChannel(name);
        socketHandler.run();
    }

    public void openChatBox() throws IOException{
        ScrollPane scrollPane = new ScrollPane();

        displayPane = new VBox();
        displayPane.setSpacing(10);

        scrollPane.setContent(displayPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(580);
        scrollWindow = scrollPane;

        TextField receiverName = new TextField();
        receiverName.setPromptText("To");
        receiverName.setMaxWidth(80);

        TextField msgBox = new TextField();
        msgBox.setPromptText("Enter your message...");
        msgBox.setMinWidth(270);

        Button sendBtn = new Button("Send");
        sendBtn.setOnAction(e->{
            if(!receiverName.getText().isEmpty() && !msgBox.getText().isEmpty()) {
                try {
                    sendTo(receiverName.getText(), msgBox.getText());
                    displayMessage("To-> " + receiverName.getText(), msgBox.getText(), displayPane, Pos.CENTER_RIGHT);
                }
                catch (IOException ioe){
                    displayMessage("Internet", "Server disconnected :(", displayPane, Pos.CENTER);
                }
            }
        });

        HBox msgPane = new HBox(receiverName, msgBox, sendBtn);
        msgPane.setAlignment(Pos.BOTTOM_CENTER);

        TextField friendName = new TextField();
        friendName.setPromptText("Enter your friend name...");

        Button connectBtn = new Button("Connect");
        connectBtn.setOnAction(e->{
            if(!friendName.getText().isEmpty()){
                try{
                    connectTo(friendName.getText());
                }
                catch (IOException x){
                    popupMsg("Unable to connect to " + friendName);
                }
            }
        });

        HBox connectPane = new HBox(friendName, connectBtn);
        connectPane.setSpacing(10);
        connectPane.setAlignment(Pos.CENTER);
        connectPane.setPadding(new Insets(10));

        VBox mainPane = new VBox(scrollPane, msgPane, connectPane);

        Scene scene = new Scene(mainPane);
        scene.getStylesheets().add("chitChatApp/style.css");

        primaryStage.setOnCloseRequest(e->{
            try {
                socketHandler.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        primaryStage.setMaxWidth(400);
        primaryStage.setMaximized(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ChitChat:" + name);
        primaryStage.setOnCloseRequest(e->{
            close();
        });
        //primaryStage.setResizable(false);
    }

    @Override
    public void start(Stage primaryStage) throws IOException{
        Label loginLbl = new Label("LOGIN");
        loginLbl.setFont(Font.font("Monospace", FontWeight.EXTRA_BOLD, 40));
        Label userIdLbl = new Label("User ID");

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter your name");
        userIdField.setMaxWidth(150);

        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e->{
            try {
                if(!userIdField.getText().isEmpty()) {
                    name = userIdField.getText();
                    connect();
                    System.out.println("Connected Successfully!");
                    openChatBox();
                }
                else{
                    popupMsg("Please enter valid userID!");
                }
            }
            catch (IOException x){
                popupMsg("Server Not Found! :(");
            }
        });

        VBox vpane = new VBox(loginLbl, userIdLbl, userIdField, loginBtn);
        vpane.setSpacing(10);
        vpane.setAlignment(Pos.CENTER);
        vpane.getStyleClass().add("login-background");
        Scene scene = new Scene(vpane, 600, 400);
        scene.getStylesheets().add("chitChatApp/style.css");

        primaryStage.setTitle("ChitChat");
        primaryStage.setScene(scene);
        this.primaryStage = primaryStage;
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    static void displayMessage(String from, String msg, Pos pos){
        displayMessage(from, msg, displayPane, pos);
    }

    static void displayMessage(String from, String msg, Pane displayPane, Pos pos){
        Label nameLbl = new Label(from + ":");
        nameLbl.setTextFill(Color.GREEN);
        Label msgLbl = new Label(msg);
        msgLbl.setTextFill(Color.WHITE);


        VBox msgPane = new VBox(nameLbl, msgLbl);
        msgLbl.setMinWidth(100);
        msgLbl.setAlignment(pos);
        msgPane.setPadding(new Insets(20));
        msgPane.setStyle("-fx-background-color: rgba(0,0,0, 0.2);");
        msgPane.setAlignment(pos);

        Platform.runLater(() -> {                               //**********
            displayPane.getChildren().add(msgPane);
            scrollWindow.setVvalue(1.0);
        });
    }

    void close(){
        try{
            if(socketHandler!=null)
                socketHandler.close();
            for(FriendChannel friendChannel: friends.values()){
                System.out.println( friendChannel.getFriendName() + " is disconnecting.....");
                friendChannel.close();
            }
            friends = null;
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    void popupMsg(String msg){
        Popup popup = new Popup();
        Label label = new Label(msg);
        label.setFont(Font.font(25));
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-radius: 50px; -fx-background-color: rgba(0,0,0, 0.3);");
        label.setTextFill(Color.WHITE);
        label.setPadding(new Insets(20));
        popup.getContent().add(label);
        //popup.setX((primaryStage.getWidth()/2) - 120);
        //popup.setY((primaryStage.getHeight()/3));
        popup.setAutoHide(true);
        popup.show(primaryStage);
    }


    public static void main(String []s) throws IOException{
        launch(s);
    }
}
