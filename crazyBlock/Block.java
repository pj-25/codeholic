package crazyBlock;

import javafx.animation.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;

public class Block implements Player{
    interface Movement {
        void move();
    }

    private Rectangle body;
    private int posX;
    private int posY;
    int moveCount = 0;
    Timeline blockAnim;
    int speed = 50;

    public Block() {
        this(10, 10, 20, 20);
        posX = posY = 0;
    }

    public Block(Pane displayPane){
        this();
        draw(displayPane);
    }

    public Block(double x, double y, double h, double w) {
        body = new Rectangle(x, y, h, w);
        setPosXY();
    }

    public Block(double x, double y, double h, double w, Color c){
        this(x,y,h,w);
        body.setFill(c);
    }

    public Block(double x, double y, double h, double w, Pane displayPane) {
        this(x,y,h,w);
        draw(displayPane);
    }

    public Rectangle getBody() {
        return body;
    }

    public void setBodyXY(double x, double y){
        body.setX(x);
        body.setY(y);
        setPosXY();
    }

    public void setBody(Rectangle body) {
        this.body = body;
    }

    public void setPosXY(){
        int []xy= MazeRunner.getGridLoc(body.getX(), body.getY());
        posX = xy[0];
        posY = xy[1];
    }

    public void draw(Pane surface) {
        surface.getChildren().add(body);
        body.setFill(Color.RED);
        //Random rand = new Random();
        //body.setOnMouseClicked(e->body.setFill(Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256))));
    }

    void moveUp() {
        posY--;
        body.setY(body.getY() - body.getHeight());
    }

    void moveDown() {
        posY++;
        body.setY(body.getY() + body.getHeight());
    }

    void moveRight() {
        posX++;
        body.setX(body.getX() + body.getWidth());
    }

    void moveLeft() {
        posX--;
        body.setX(body.getX() - body.getWidth());
    }

    @Override
    public void move(int dir){
        switch (dir){
            case 0:
                moveUp();
                break;
            case 1:
                moveRight();
                break;
            case 2:
                moveDown();
                break;
            case 3:
                moveLeft();
                break;
        }
    }

    @Override
    public int getPosX() {
        return posX;
    }

    @Override
    public void setPosX(int posX) {
        this.posX = posX;
    }

    @Override
    public int getPosY() {
        return posY;
    }

    @Override
    public void setPosY(int posY) {
        this.posY = posY;
    }

    @Override
    public void onKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DOWN)
            moveBlock(this::moveDown);
        else if (keyEvent.getCode() == KeyCode.UP)
            moveBlock(this::moveUp);
        else if (keyEvent.getCode() == KeyCode.LEFT)
            moveBlock(this::moveLeft);
        else if (keyEvent.getCode() == KeyCode.RIGHT)
            moveBlock(this::moveRight);
    }

    void moveBlock(Movement m) {
        moveBlock(m, 1);
    }

    void moveBlock(Movement m, int steps) {
        KeyFrame blockmovement = new KeyFrame(Duration.millis(speed), e -> m.move());
        Timeline blockanim = new Timeline(blockmovement);
        blockanim.setCycleCount(steps);
        blockanim.play();
        blockanim.setOnFinished(e -> blockanim.stop());
    }

    @Override
    public void moveOnPath(ArrayList<Integer> path) {
        //System.out.println(path);
        if (path == null)
            return;

        KeyFrame blockmovement = new KeyFrame(Duration.millis(speed), e -> {
            if(moveCount==path.size()){
                blockAnim.stop();
                moveCount = 0;
            }
            else {
                int moveDir = path.get(moveCount++);
                move(moveDir);
            }
        });

        blockAnim = new Timeline(blockmovement);
        blockAnim.setCycleCount(Animation.INDEFINITE);
        blockAnim.play();
    }


}
