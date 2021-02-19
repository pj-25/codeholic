package crazyBallGame;

import javafx.animation.Animation;
import javafx.application.Application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class CrazyBallGame extends Application{

    public final static String SERVER_IP = "localhost"; //18.191.252.245
    public final static int SERVER_PORT = 8383;
    private String gameCode;
    private Stage primaryStage;
    private Pane displayPane;
    private Player []player;
    private BouncingBall ball;
    private Timeline ballAnim;
    private int gameMode ;
    private DatagramSocket socket;
    private DatagramPacket datagramPacket;

    private int turn = 0;

    public final static int PLAYGROUND_WIDTH = 800;
    public final static int PLAYGROUND_HEIGHT = 600;

    public static void main(String []s){
        launch(s);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        player = new Player[2];

        Label gameTitle = new Label("Crazy Ball");
        gameTitle.setFont(Font.font("Chilanka", FontWeight.EXTRA_BOLD, 40));


        Label nameLbl = new Label("Player Name");
        TextField playerName = new TextField();
        playerName.setPromptText("Enter your name...");
        playerName.setMaxWidth(200);

        ToggleGroup gameModes = new ToggleGroup();

        RadioButton localMatchrBtn = new RadioButton("Create Local Match");
        localMatchrBtn.setToggleGroup(gameModes);

        Label opponentNameLbl = new Label("Opponent Name:");
        TextField opponentName = new TextField();
        opponentName.setPromptText("Enter opponent Name...");
        opponentName.setMaxWidth(200);
        Button playLocalBtn = new Button("Play");
        playLocalBtn.setOnAction(e->{
            gameMode = -1;
            if(!playerName.getText().isEmpty() && !opponentName.getText().isEmpty())
                startLocalMatch(playerName.getText(), opponentName.getText());
            else
                popupMsg("Please enter valid player names!");
        });

        HBox localMatchPane = new HBox(opponentNameLbl, opponentName, playLocalBtn);
        localMatchPane.setAlignment(Pos.CENTER);
        localMatchPane.setSpacing(10);
        localMatchPane.disableProperty().bind(localMatchrBtn.selectedProperty().not());


        Button crtOnlineMatchBtn = new Button("Create Online Match");
        crtOnlineMatchBtn.setOnAction(e->{
            if(!playerName.getText().isEmpty()){
                gameMode = 0;
                createOnlineMatch(playerName.getText());
            }
            else
                popupMsg("Enter valid player Name!");
        });


        RadioButton joinMatchrBtn = new RadioButton("Join Online Match");
        joinMatchrBtn.setToggleGroup(gameModes);

        Label gameCodeLbl = new Label("Game Code:");
        TextField gameCodeField = new TextField();
        gameCodeField.setPromptText("Enter game code...");

        Button joinBtn = new Button("Join");
        joinBtn.setOnAction(e->{
            if(!playerName.getText().isEmpty() && !gameCodeField.getText().isEmpty()){
                gameMode = 1;
                gameCode = gameCodeField.getText();
                joinGame(playerName.getText());
            }
        });

        HBox joinOptionPane = new HBox(gameCodeLbl, gameCodeField, joinBtn);
        joinOptionPane.setAlignment(Pos.CENTER);
        joinOptionPane.setSpacing(10);
        joinOptionPane.disableProperty().bind(joinMatchrBtn.selectedProperty().not());


        VBox vpane = new VBox(gameTitle, nameLbl, playerName, localMatchrBtn, localMatchPane, joinMatchrBtn, joinOptionPane, crtOnlineMatchBtn);
        vpane.setAlignment(Pos.CENTER);
        vpane.setSpacing(10);
        vpane.getStyleClass().add("background-style");

        Scene scene = new Scene(vpane, 800, 600);
        scene.getStylesheets().add("crazyBallGame/style.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Crazy Ball");
        primaryStage.setOnCloseRequest(e->close());
        primaryStage.setOnCloseRequest(e->{
            close();
        });
        primaryStage.show();
    }

    public void joinGame(String playerName){
        setPlayer2(playerName);

        new Thread(()->{
            try{
                setOpponentAsPlayer1();
                ((Opponent) player[0]).connect(SERVER_IP, SERVER_PORT);
                ((Opponent) player[0]).sendJoinRequest(gameCode, playerName);
                ((Opponent) player[0]).receiveName();

                Platform.runLater(this::startGame);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }).start();

    }

    public void waitWindow(){
        Text statusLbl = new Text("Game Session created successfully :)");
        statusLbl.setFont(Font.font(40));
        Text gameCodeTxt = new Text("GameCode: " + gameCode);
        gameCodeTxt.setFont(Font.font(20));
        Text waitMsg = new Text("Waiting for Opponent to join...");
        waitMsg.setFont(Font.font(25));

        VBox vpane = new VBox(statusLbl, gameCodeTxt, waitMsg);
        vpane.setSpacing(10);
        vpane.setAlignment(Pos.BOTTOM_CENTER);
        vpane.getStyleClass().add("wait-window");

        Scene scene = new Scene(new StackPane(vpane), 800, 600);
        scene.getStylesheets().add("crazyBallGame/style.css");
        primaryStage.setScene(scene);
    }

    public void startLocalMatch(String player1, String player2){
        setPlayer1(player1);
        setPlayer2(player2);
        startGame();
    }

    public void createOnlineMatch(String playerName){
        setPlayer1(playerName);

        new Thread(()->{
            try {
                DatagramSocket socket = new DatagramSocket();
                byte []buffer = ("2:" + playerName).getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(SERVER_IP), SERVER_PORT);
                socket.send(datagramPacket);

                socket.setSoTimeout(5000);
                buffer = new byte[512];
                datagramPacket.setData(buffer);
                datagramPacket.setLength(buffer.length);
                socket.receive(datagramPacket);
                socket.setSoTimeout(0);

                String msg = new String(datagramPacket.getData()).trim();

                if (msg.startsWith("?")) {
                   popupMsg(msg.substring(1));
                } else {
                    gameCode = msg;

                    Platform.runLater(this::waitWindow);

                    setOpponentAsPlayer2(msg);
                    datagramPacket.setData(new byte[512]);
                    ((Opponent)player[1]).connect(socket, datagramPacket);
                    ((Opponent)player[1]).receiveName();

                    Platform.runLater(()->{
                        popupMsg("Opponent(" + player[1].getName() + ") joined!");
                        startGame();
                    });
                }
            }
            catch(SocketTimeoutException e){
                Platform.runLater(()->{
                    popupMsg("Server Not Found! :(");
                });
            }
            catch (IOException e){
                Platform.runLater(()->{
                        popupMsg("You are Offline! :(");
                });
            }
        }).start();
    }

    void popupMsg(String msg){
        Popup popup = new Popup();
        Label label = new Label(msg);
        label.setFont(Font.font(25));
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add("popup-style");
        label.setTextFill(Color.WHITE);
        label.setPadding(new Insets(20));
        popup.getContent().add(label);

        popup.setX(primaryStage.getX() + (primaryStage.getWidth() - 250)/2);
        popup.setY(primaryStage.getY() + 80);
        popup.setAutoFix(true);

        popup.setAutoHide(true);
        popup.show(primaryStage);
    }

    public void startGame(){
        displayPane = new Pane();

        Rectangle playground = new Rectangle(0,0,PLAYGROUND_WIDTH, PLAYGROUND_HEIGHT);
        playground.setFill(Color.rgb(43, 43, 43));


        displayPane.getChildren().add(playground);

        ball = new BouncingBall( 300, 300, 15);
        displayPane.getChildren().add(ball);

        if(gameMode!=-1){
            connect();
        }
        displayPane.getChildren().addAll(player);

        Label p1Name = new Label(player[0].getName());
        Label p1Score = new Label();
        p1Score.setFont(Font.font("Monospace", 30));
        p1Score.textProperty().bindBidirectional(player[0].scoreProperty());

        Label p2Name = new Label(player[1].getName());
        Label p2Score = new Label();
        p2Score.setFont(Font.font("Monospace", 30));
        p2Score.textProperty().bindBidirectional(player[1].scoreProperty());

        HBox scorePane = new HBox(p1Name, p1Score, new Text("--"), p2Score, p2Name);
        scorePane.setSpacing(10);
        scorePane.setAlignment(Pos.CENTER);

        VBox vpane = new VBox(displayPane, scorePane);
        vpane.setAlignment(Pos.CENTER);


        Scene scene = new Scene(vpane);
        scene.setOnKeyPressed(this::addMoveControls);


        Text counter = new Text("5");
        counter.setFont(Font.font("Monospace", FontWeight.EXTRA_BOLD,100));
        counter.setFill(Color.GREEN);
        counter.setX(PLAYGROUND_WIDTH/2.0);
        counter.setY(PLAYGROUND_HEIGHT/2.0 - 50);
        displayPane.getChildren().add(counter);

        KeyFrame countDownFrame = new KeyFrame(Duration.millis(800), e->{
            counter.setText(Integer.parseInt(counter.getText()) - 1 + "");
        });
        Timeline countDownAnim = new Timeline(countDownFrame);
        countDownAnim.setCycleCount(5);
        countDownAnim.play();
        countDownAnim.setOnFinished(e->{
            displayPane.getChildren().remove(displayPane.getChildren().size()-1);
            play();
        });

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
    }

    public void close(){
        if(gameMode != -1){
            if(socket!=null)
                socket.close();

            Opponent opponent = ((Opponent) player[(gameMode+1)%2]);
            if (opponent != null){
                opponent.disconnect();
            }
        }
        primaryStage.close();
    }

    public void connect(){
        try{
            socket = new DatagramSocket();
            byte []buffer = new byte[512];
            datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(SERVER_IP), SERVER_PORT);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMove(String move) {
        byte []msgBytes = ((gameMode+1)%2 + ":" + gameCode + ":" + move).getBytes();
        datagramPacket.setData(msgBytes);
        datagramPacket.setLength(msgBytes.length);

        try {
            socket.send(datagramPacket);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    void setPlayer1(String playerName){
        player[0] = new Player(playerName,0, 20, 200, 30, 100);
    }

    void setPlayer2(String playerName){
        player[1] = new Player(playerName, 1,PLAYGROUND_WIDTH - 50, 100, 30, 100);
    }

    void setOpponentAsPlayer1(String playerName){
        player[0] = new Opponent(playerName,0, 20, 200, 30, 100);
    }

    void setOpponentAsPlayer1(){
        player[0] = new Opponent(0, 20, 200, 30, 100);
    }

    void setOpponentAsPlayer2(){
        player[1] = new Opponent(1,PLAYGROUND_WIDTH - 50, 100, 30, 100);
    }

    void setOpponentAsPlayer2(String playerName){
        player[1] = new Opponent(playerName, 1,PLAYGROUND_WIDTH - 50, 100, 30, 100);
    }

    public void play(){
        ballAnim = null;
        KeyFrame ballMovement = new KeyFrame(Duration.millis(5), e->{
            if( ball.getCenterY() - ball.getRadius() <= 5 || ball.getCenterY() + ball.getRadius() >= PLAYGROUND_HEIGHT - 5 ){
                ball.bounce(0);
            }
            else if( (turn == 0 && ball.getCenterX() - ball.getRadius() <= player[0].getX() + player[1].getWidth() ) ||
                    ( turn == 1 && ball.getCenterX() + ball.getRadius() >= player[1].getX() )){

                if( (turn == 0 && ball.isCollidingTo(player[0])) || (turn == 1 && ball.isCollidingTo(player[1])) )  {
                    changeTurn();
                    ball.bounce(Double.POSITIVE_INFINITY);
                }
                else if( (turn == 0 && ball.getCenterX() == 0) || (turn == 1 && ball.getCenterX() == PLAYGROUND_WIDTH )) {
                    ball.reset();
                    changeTurn();
                    player[turn].incrementScore();
                    if(player[turn].getScore().equals("3")){
                        displayWinner(turn);
                        ballAnim.stop();
                    }
                }
            }
            moveBall();
        });
        ballAnim = new Timeline(ballMovement);
        ballAnim.setCycleCount(Animation.INDEFINITE);
        ballAnim.play();
    }

    void displayWinner(int playerCode){
        displayPane.setDisable(false);

        Text winnerMsg = new Text(player[playerCode].getName() + " won!");
        winnerMsg.setFont(Font.font( "Monospace", FontWeight.EXTRA_BOLD, 40));
        winnerMsg.setFill(Color.GREEN);

        Button closeBtn = new Button("Close");

        VBox vpane = new VBox(winnerMsg, closeBtn);
        vpane.setAlignment(Pos.CENTER);
        vpane.setSpacing(10);

        Scene endScene = new Scene(vpane, 400, 300);

        Stage endStage = new Stage();
        endStage.setScene(endScene);
        endStage.setAlwaysOnTop(true);
        endStage.initStyle(StageStyle.UNDECORATED);

        closeBtn.setOnAction(e->{
            endStage.close();
            close();
        });
        endStage.show();
    }

    void addMoveControls(KeyEvent e){
        if(e.getCode() == KeyCode.UP){
            if(gameMode == -1)
                player[0].moveUp();
            else {
                player[gameMode].moveUp();
                sendMove("0");
            }
        }
        else if(e.getCode() == KeyCode.DOWN){
            if(gameMode == -1)
                player[0].moveDown();
            else{
                player[gameMode].moveDown();
                sendMove("1");
            }
        }
        else if(gameMode == -1){
            if(e.getCode() == KeyCode.W)
                player[1].moveUp();
            else if(e.getCode() == KeyCode.S)
                player[1].moveDown();
        }
    }


    void moveBall(){
        if(turn == 0)
            ball.move(-1);
        else
            ball.move(1);
    }

    void moveUp(){
        player[turn].moveUp();
    }

    void changeTurn(){
        turn = (turn + 1) % 2;
    }

    void moveDown(){
        player[turn].moveDown();
    }
}
