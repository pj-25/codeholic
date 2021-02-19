package crazyBlock;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;

public class Snake implements Player{
    private LinkedList<Block> body;
    private LinkedList<Integer> dirVector;
    private Pane displayPane;
    private final int dirCode[] = {0,1,2,3};
    final private int sideLength = 20;
    private int moveCount = 0;
    private Grid grid;
    int speed = 50;

    public Snake(Pane displayPane, Grid grid){
        this(displayPane, grid,1);
    }

    public Snake(Pane displayPane,Grid grid, int dir){
        body = new LinkedList<>();
        this.displayPane = displayPane;
        this.grid = grid;
        this.dirVector = new LinkedList<>();
        dirVector.add(dir);
        body.addLast(new Block(displayPane));
    }

    public Snake(Pane displayPane, Grid grid, int dir, Double x, Double y){
        this(displayPane, grid, dir);
        body.getFirst().setBodyXY(x,y);
        fillHead(Color.RED);
    }

    @Override
    public int getPosX() {
        return getHead().getPosX();
    }

    @Override
    public int getPosY() {
        return getHead().getPosY();
    }

    @Override
    public void setPosX(int x) {
        getHead().setPosX(x);
    }

    @Override
    public void setPosY(int y) {
        getHead().setPosY(y);
    }

    public void growHead(int dir){
        final int[] dx = {0, 1, 0, -1};
        final int[] dy = {-1, 0, 1, 0};
        Block connectingBlock = body.getFirst();
        Block bodyBlock = new Block(connectingBlock.getBody().getX() + (dx[dir]*sideLength), connectingBlock.getBody().getY() + (dy[dir] * sideLength), sideLength, sideLength);
        bodyBlock.draw(displayPane);
        body.addFirst(bodyBlock);
        dirVector.addFirst(dirCode[dir]);
        grid.setXY(bodyBlock.getPosX(), bodyBlock.getPosY(), 4);
    }

    public void growHead(){
        growHead(dirCode[dirVector.getFirst()]);
    }

    public void growTail(){
        final int[] dx = {0, -1, 0, 1};
        final int[] dy = {1, 0, -1, 0};
        Block connectingBlock = body.getLast();
        Block bodyBlock = new Block(connectingBlock.getBody().getX() + (dx[dirVector.getLast()]*sideLength), connectingBlock.getBody().getY() + (dy[dirVector.getLast()] * sideLength), sideLength, sideLength);
        body.addLast(bodyBlock);
        dirVector.addLast(dirVector.getLast());
    }

    public void shrinkTail(){
        Block block = body.removeLast();
        displayPane.getChildren().remove(block.getBody());
        grid.setXY(block.getPosX(), block.getPosY(), 0);
        dirVector.removeLast();
    }

    public void shrinkHead(){
        Block block = body.removeFirst();
        displayPane.getChildren().remove(block.getBody());
        grid.setXY(block.getPosX(), block.getPosY(), 0);
        dirVector.removeFirst();
    }

    @Override
    public void move(int dir){
        fillHead(Color.GREEN);
        growHead(dir);
        shrinkTail();
        fillHead(Color.RED);
        //fillHead();
    }

    public void fillHead(Color c){
        getHead().getBody().setFill(c);
    }

    @Override
    public void moveOnPath(ArrayList<Integer> path){
        if(path==null)
            return;
        moveCount = 0;
        Timeline moveAnim = new Timeline();
        KeyFrame moveFrame = new KeyFrame(Duration.millis(speed),e->{
            if(moveCount == path.size()){
                growTail();
                grid.setXY(getHead().getPosX(), getHead().getPosY(), 0);
                moveAnim.stop();
            }
            else{
                move(path.get(moveCount++));
            }
        });
        moveAnim.getKeyFrames().add(moveFrame);
        moveAnim.setCycleCount(Animation.INDEFINITE);
        moveAnim.play();
    }

    @Override
    public void onKeyEvent(KeyEvent e) {
        if(e.getCode() == KeyCode.UP)
            move(0);
        else if(e.getCode() == KeyCode.RIGHT)
            move(1);
        else if(e.getCode() == KeyCode.DOWN)
            move(2);
        else if(e.getCode() == KeyCode.LEFT)
            move(3);
    }

    public Block getHead(){
        return body.get(0);
    }
    public LinkedList<Block> getBody() {
        return body;
    }

    public void setBody(LinkedList<Block> body) {
        this.body = body;
    }

    public Pane getDisplayPane() {
        return displayPane;
    }

    public void setDisplayPane(Pane displayPane) {
        this.displayPane = displayPane;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }
}
