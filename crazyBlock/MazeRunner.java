package crazyBlock;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.util.Random;

public class MazeRunner extends Application {
    private Pane playground;
    private Player player;
    private Grid gridBoard;
    private Circle target;
    final static int cellHeight = 20;
    final static int cellWidth = 20;
    private int gridRows = 25;
    private int gridColumns = 40;

    public MazeRunner(int gridRows, int gridColumns){
        this.gridRows = gridRows;
        this.gridColumns = gridColumns;
        gridBoard = new Grid(gridRows, gridColumns);
    }

    public MazeRunner(){ gridBoard = new Grid(gridRows, gridColumns); }

    public static void main(String s[]){
        launch(s);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        playground = new Pane();

        setupPlayground();

        Button playBtn = new Button("Play");
        playBtn.setAlignment(Pos.CENTER);
        playBtn.setOnAction(e->{
            //System.out.println(grid);
            //block.moveOnPath(gridBoard.getShortestPathToTarget(block.getPosX(), block.getPosY()));
            player.moveOnPath(gridBoard.getShortestPathToTarget(player.getPosX(), player.getPosY()));
        });

        Button visualizebfsBtn = new Button("Visualize BFS");
        visualizebfsBtn.setOnAction(e->{
            PathFindingVisualizer visualizer = new PathFindingVisualizer(this);
            visualizer.runPathFinderBFS();
        });

        Button visualizedfsBtn = new Button("Visualize DFS");
        visualizedfsBtn.setOnAction(e->{
            PathFindingVisualizer visualizer = new PathFindingVisualizer(this);
            visualizer.runPathFinderDFS();
        });

        HBox visualizeBtnPane = new HBox(visualizebfsBtn, visualizedfsBtn);
        visualizeBtnPane.setSpacing(20);
        visualizeBtnPane.setAlignment(Pos.CENTER);

        VBox optionPane = new VBox(playBtn, visualizeBtnPane);
        optionPane.setAlignment(Pos.CENTER);
        optionPane.setSpacing(20);

        RadioButton darkmodeOption = new RadioButton("darkmode");

        VBox gamePane = new VBox(playground, optionPane, darkmodeOption);
        gamePane.setAlignment(Pos.CENTER);
        darkmodeOption.setAlignment(Pos.CENTER_RIGHT);
        gamePane.setSpacing(20);
        darkmodeOption.setOnAction(e->{
            if(darkmodeOption.isSelected()){
                gamePane.setStyle("-fx-background: rgb(43,43,43);");
            }
            else{
                gamePane.setStyle("-fx-background: white;");
            }
        });


        gamePane.setPadding(new Insets(20));
        //initObstacles();

        Scene scene = new Scene(gamePane);
        scene.setOnKeyPressed(this::movePlayer);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Maze Runner");
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    void movePlayer(KeyEvent e){
        int dir = -1;
        if(e.getCode() == KeyCode.NUMPAD8)
            dir = 0;
        else if(e.getCode() == KeyCode.NUMPAD6)
            dir = 1;
        else if(e.getCode() == KeyCode.NUMPAD2)
            dir = 2;
        else if(e.getCode() == KeyCode.NUMPAD4)
            dir = 3;
        else
            return;

        int []dx = {0, 1, 0, -1};
        int []dy = {-1, 0, 1, 0};
        int gvalue = gridBoard.getXY(player.getPosX()+dx[dir], player.getPosY()+dy[dir]);
        if( gvalue == 0 || gvalue == -1){
            player.move(dir);
            if(gvalue == -1){
                if(player instanceof Snake)
                    ((Snake) player).growTail();
                changeTargetLocation();
            }
        }
    }

    void setupPlayground(){
        target = new Circle(200,200, cellHeight/2);
        target.setFill(Color.YELLOW);
        gridBoard.setXY(9,9, -1);

        Rectangle groundBox = new Rectangle(10,10,  cellWidth*gridColumns,cellHeight*gridRows);
        groundBox.setFill(Color.CYAN);
        /*groundBox.setStyle("    -fx-stroke: green;" +
                "    -fx-stroke-width: 5;" +
                "    -fx-stroke-dash-array: 12 2 4 2;" +
                "    -fx-stroke-dash-offset: 6;" +
                "    -fx-stroke-line-cap: butt;");
        */

        playground.getChildren().addAll(groundBox, target);

        player = new Snake(playground, gridBoard, 1, 150.0, 150.0);

        //player = new Block(playground);

        groundBox.setOnMouseClicked(e->{
            int []center = getGridCenter(e.getX(), e.getY());
            if(gridBoard.getXY((center[0]/cellWidth) -1, (center[1]/cellHeight) -1) == 0){
                if(e.isAltDown())
                    addObstacle(center[0], center[1]);
                else
                    changeTargetLocation(center[0], center[1]);
            }
            //System.out.println(target.getCenterX() + ","+ target.getCenterY());
        });

        addRandomObstacles(150);
    }

    void changeTargetLocation(int x, int y){
        gridBoard.setXY( (int)(target.getCenterX()/cellWidth) - 1,(int)(target.getCenterY()/cellHeight)  - 1 , 0);
        target.setCenterX(x);
        target.setCenterY(y);
        gridBoard.setXY((x/cellWidth) - 1,(y/cellHeight) -1 , -1);
    }

    void addObstacle(int x, int y){
        addObstacle(x, y, Color.BLACK);
    }

    void addObstacle(int x, int y, Rectangle r){
        gridBoard.setXY((x/cellWidth) - 1,(y/cellHeight) -1 , 5);
        playground.getChildren().add(r);
    }

    void addObstacle(int x,int y, Color c){
        Rectangle box = new Rectangle(x-10, y-10, cellWidth, cellHeight);
        box.setOnMouseClicked(e->{
            int gloc[] = getGridLoc(box.getX(), box.getY());
            gridBoard.setXY(gloc[0], gloc[1], 0);
            playground.getChildren().remove(box);
        });
        box.setFill(c);
        addObstacle(x,y,box);
    }

    void initObstacles(){
        Rectangle[] obstacles = new Rectangle[2];
        obstacles[0] = new Rectangle(190, 90, 220, cellHeight);
        obstacles[1] = new Rectangle(190, 290, 220, cellHeight);
        playground.getChildren().addAll(obstacles);
        for(int i=9;i<20;i++){
            gridBoard.setXY(i, 4, 5);
            gridBoard.setXY(i, 14, 5);
        }
    }

    void changeTargetLocation(){
        Random rand = new Random();
        int x = rand.nextInt(cellWidth*gridColumns - 10) + 10;
        int y = rand.nextInt(cellHeight*gridRows - 10) + 10;
        int []xy = getGridCenter(x,y);
        if(gridBoard.getXY((xy[0]/cellWidth)-1, (xy[1]/cellHeight)-1) == 0) {
            changeTargetLocation(xy[0],xy[1]);
        }
        else
            changeTargetLocation();
    }

    void addRandomObstacles(int size){
        Random rand = new Random();
        for(int i=0;i<size;i++){
            int x = rand.nextInt(cellWidth*gridColumns - 10) + 10;
            int y = rand.nextInt(cellHeight*gridRows - 10) + 10;
            int []xy = getGridCenter(x,y);
            if(gridBoard.getXY((xy[0]/cellWidth)-1, (xy[1]/cellHeight)-1) == 0)
                addObstacle(xy[0],xy[1]);
        }
    }

    public static int[] getGridCenter(double x, double y){
        int xc = (int)x + (cellWidth/2);
        int yc = (int)y + (cellHeight/2);
        xc -= (xc%cellWidth);
        yc -= (yc%cellHeight);
        return new int[]{xc, yc};
    }

    public static int[] getGridLoc(double x, double y){
        int []gloc = getGridCenter(x,y);
        gloc[0] = (gloc[0]/cellWidth) - 1;
        gloc[1] = (gloc[1]/cellHeight) - 1;
        return gloc;
    }

    public Pane getPlayground() {
        return playground;
    }

    public void setPlayground(Pane playground) {
        this.playground = playground;
    }

    public void setPlayer(Player p){
        player = p;
    }

    public Player getPlayer(){
        return player;
    }

    public Grid getGridBoard() {
        return gridBoard;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public void setGridBoard(Grid gridBoard) {
        this.gridBoard = gridBoard;
    }

    public Circle getTarget() {
        return target;
    }

    public void setTarget(Circle target) {
        this.target = target;
    }
}
