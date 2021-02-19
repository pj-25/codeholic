package crazyBlock;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class PathFindingVisualizer{
    private boolean isFound;
    private final int [][]cgrid;
    private Timeline visualizer;
    private final MazeRunner mazeRunner;
    private ArrayList<Rectangle> paths;
    private int targetr,targetc;
    private int [] nodeXY;

    public PathFindingVisualizer(MazeRunner mazeRunner){
        cgrid = new int[mazeRunner.getGridBoard().getHeight()][mazeRunner.getGridBoard().getWidth()];
        this.mazeRunner = mazeRunner;
        for (int i = 0; i < mazeRunner.getGridBoard().getHeight(); i++) {
            System.arraycopy(mazeRunner.getGridBoard().getGrid()[i], 0, cgrid[i], 0, mazeRunner.getGridBoard().getWidth());
        }
        paths = new ArrayList<>();
        visualizer = new Timeline();
    }


    public void runPathFinderBFS() {
        Queue<int[]> queue = new LinkedList<>();
        nodeXY = new int[]{mazeRunner.getPlayer().getPosX(), mazeRunner.getPlayer().getPosY()};
        queue.add(nodeXY);
        cgrid[nodeXY[1]][nodeXY[0]] = -2;

        int []dr = {-1, 0, 1, 0};
        int []dc = {0, 1, 0, -1};

        KeyFrame visualizerKeyFrame = new KeyFrame(Duration.millis(5), e->{
            if (!queue.isEmpty() && !isFound) {
                nodeXY = queue.poll();
                for (int i = 0; i < 4; i++) {
                    targetr = nodeXY[1] + dr[i];
                    targetc = nodeXY[0] + dc[i];
                    if (isNotInGrid())
                        continue;
                    if (cgrid[targetr][targetc] == 0) {
                        cgrid[targetr][targetc] = i+1;
                        addPathBox(targetr,targetc, Color.LIGHTPINK);
                        queue.add(new int[]{targetc, targetr});
                    }
                    else if(cgrid[targetr][targetc]==-1){
                        cgrid[targetr][targetc] = i+1;
                        isFound = true;
                        break;
                    }
                }
            }
            else
            {
                if(isFound)
                    runPath();
                else
                {
                    visualizer.stop();
                    mazeRunner.getPlayground().getChildren().removeAll(paths);
                }
            }
        });
        visualizer=new Timeline(visualizerKeyFrame);
        visualizer.setCycleCount(Animation.INDEFINITE);
        //visualizer.setOnFinished(a->crazyBlock.getPlayground().getChildren().removeAll(paths));
        visualizer.play();
    }

    boolean isNotInGrid(){
        return targetr < 0 || targetr >= cgrid.length || targetc < 0 || targetc >= cgrid[0].length;
    }

    void runPathFinderDFS(){
        Stack<int[]> stack = new Stack<>();
        int []dr = {-1, 0, 1, 0};
        int []dc = {0, 1, 0, -1};
        nodeXY = new int[]{mazeRunner.getPlayer().getPosX(), mazeRunner.getPlayer().getPosY()};
        cgrid[nodeXY[1]][nodeXY[0]] = -2;
        stack.push(nodeXY);

        KeyFrame visualizerKeyFrame = new KeyFrame(Duration.millis(5), e->{
            if(!stack.isEmpty() && !isFound){
                nodeXY = stack.peek();
                int i;
                for(i=0;i<4;i++){
                    targetr = nodeXY[1] + dr[i];
                    targetc = nodeXY[0] + dc[i];
                    if(isNotInGrid())
                        continue;
                    if(cgrid[targetr][targetc] == 0){
                        addPathBox(targetr, targetc, Color.LIGHTPINK);
                        cgrid[targetr][targetc] = i+1;
                        stack.push(new int[]{targetc, targetr});
                        break;
                    }
                    else if(cgrid[targetr][targetc] == -1){
                        isFound = true;
                        break;
                    }
                }
                if(i==4){
                    stack.pop();
                }
            }
            else{
                if(isFound)
                    runPath();
                else
                {
                    visualizer.stop();
                    mazeRunner.getPlayground().getChildren().removeAll(paths);
                }
            }
        });
        visualizer = new Timeline(visualizerKeyFrame);
        visualizer.setCycleCount(Animation.INDEFINITE);
        visualizer.play();
    }

    void generateRandomMaze(){
        Stack<int []> stack = new Stack<>();
        int []dr = {-1, 0, 1, 0};
        int []dc = {0, 1, 0, -1};
        nodeXY = new int[]{0,0};

        stack.push(nodeXY);
        
        Random random = new Random();
        Grid gridBoard = mazeRunner.getGridBoard();
        gridBoard.setXY(0,0,-2);
        KeyFrame visualizerKeyFrame = new KeyFrame(Duration.millis(5), e->{
            if(!stack.isEmpty()){
                nodeXY = stack.peek();
                int i;
                int randm = random.nextInt(4);
                for(i=0;i<1;i++){
                    int randdir = random.nextInt(4);
                    targetr = nodeXY[1] + dr[randdir];
                    targetc = nodeXY[0] + dc[randdir];
                    if(isNotInGrid())
                        continue;
                    if(gridBoard.getXY(targetc,targetr) == 0){
                        addBoxToGrid(createCell(targetr, targetc, Color.BROWN));
                        gridBoard.setXY(targetc, targetr, 5);
                        stack.push(new int[]{targetc, targetr});
                        break;
                    }
                }
                if(i==1){
                    stack.pop();
                }
            }
            else{
                visualizer.stop();
                gridBoard.setXY(0,0,0);
            }
        });
        visualizer = new Timeline(visualizerKeyFrame);
        visualizer.setCycleCount(Animation.INDEFINITE);
        visualizer.play();
    }


    void runPath() {
        ArrayList<Integer> path = new ArrayList<>();
        int[] dr = {-1, 0, 1, 0};
        int[] dc = {0, 1, 0, -1};
        KeyFrame pathMaker = new KeyFrame(Duration.millis(20), e -> {
            if (cgrid[targetr][targetc] != -2) {
                int dir = (cgrid[targetr][targetc] + 1) % 4;
                path.add((dir + 2) % 4);
                targetr += dr[dir];
                targetc += dc[dir];
                addPathBox(targetr, targetc, Color.ORANGE);
            } else {
                reverse(path);
                mazeRunner.getPlayer().moveOnPath(path);
                visualizer.stop();
                mazeRunner.getPlayground().getChildren().removeAll(paths);
            }
        });
        visualizer.getKeyFrames().remove(0);
        visualizer.getKeyFrames().add(pathMaker);
        visualizer.playFromStart();
    }

    void reverse(ArrayList<Integer> path){
        for (int i = 0; i < path.size() / 2; i++) {
            int d = path.get(i);
            path.set(i, path.get(path.size() - i - 1));
            path.set(path.size() - i - 1, d);
        }
    }

    void addPathBox(int r,int c, Color fill){
        Rectangle box = createCell(r,c,fill);
        paths.add(box);
        addBoxToGrid(box);
    }

    Rectangle createCell(int r, int c, Color fill){
        Rectangle box = new Rectangle(((c+1)*20)-10, ((r+1)*20) -10, 20, 20);
        box.setFill(fill);
        return box;
    }

    void addBoxToGrid(Rectangle box){
        mazeRunner.getPlayground().getChildren().add(box);
    }
}

