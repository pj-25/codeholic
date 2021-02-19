package crazyBallGame;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Player extends Rectangle{
    private String name;
    private static final int step = 20;
    private StringProperty score;
    private int playerCode;

    public Player(String playerName, int playerCode){
        name = playerName;
        this.playerCode = playerCode;
    }

    public Player(String playerName, int playerCode, double x, double y, double width, double height){
        super(x,y,width, height);
        name = playerName;
        this.playerCode = playerCode;
        setFill(Color.RED);
        score = new SimpleStringProperty("0");
    }

    public int getPlayerCode() {
        return playerCode;
    }

    public void setPlayerCode(int playerCode) {
        this.playerCode = playerCode;
    }

    public String getScore() {
        return score.get();
    }

    public StringProperty scoreProperty() {
        return score;
    }

    public void setScore(String score) {
        this.score.set(score);
    }

    public void incrementScore(){
        score.setValue((Integer.parseInt(score.getValue()) + 1) + "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void moveUp(){
        if(getY() - step >= 0){
            setY(getY() - step);
        }
    }

    public void moveDown(){
        if(getY() + getHeight() + step <= CrazyBallGame.PLAYGROUND_HEIGHT){
            setY(getY() + step);
        }
    }

    static Rectangle createPlayerBody(double x, double y, double width, double height){
        Rectangle player = new Rectangle(x, y, width, height);
        player.setFill(Color.RED);
        //player.setStyle("-fx-border-width: 5px; -fx-border-color: black; -fx-border-style: solid; -fx-background-radius: 20px;");
        return player;
    }
}
