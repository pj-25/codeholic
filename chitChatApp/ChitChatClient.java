package chitChat.chitChatApp;

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
    private Stage primaryStage;
    private NetworkConnection networkConnection;

    public ChitChatClient() {}

    public void openChatBox() throws IOException{
        ScrollPane scrollPane = new ScrollPane();

        VBox displayPane = new VBox();
        displayPane.setSpacing(10);

        scrollPane.setContent(displayPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(580);

        //setup network connection
        MessageConsumer msgConsumer = new MessageConsumer(scrollPane, displayPane);
        networkConnection = new NetworkConnection(name, msgConsumer);
        networkConnection.connect();

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
                    networkConnection.sendTo(receiverName.getText(), msgBox.getText());
                    msgConsumer.displayMessage("To-> " + receiverName.getText(), msgBox.getText(), Pos.CENTER_RIGHT);
                }
                catch (IOException ioe){
                    msgConsumer.displayMessage("Internet", "Server disconnected :(", Pos.CENTER);
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
                    networkConnection.connectTo(friendName.getText());
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
        scene.getStylesheets().add("chitChat/chitChatApp/style.css");

        primaryStage.setOnCloseRequest(e->{
            try {
                networkConnection.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        primaryStage.setMaxWidth(400);
        primaryStage.setMaximized(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ChitChat:" + name);
        //primaryStage.setResizable(false);
    }

    @Override
    public void start(Stage primaryStage){
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
        scene.getStylesheets().add("chitChat/chitChatApp/style.css");

        primaryStage.setTitle("ChitChat");
        primaryStage.setScene(scene);
        this.primaryStage = primaryStage;
        primaryStage.setMaximized(true);

        primaryStage.show();
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


    public static void main(String []s) {
        launch(s);
    }
}
