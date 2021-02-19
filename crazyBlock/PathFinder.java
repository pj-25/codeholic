package crazyBlock;

import javafx.scene.shape.Path;

public class PathFinder {
    private int [][]grid;

    public PathFinder(){
        grid = new int[50][50];
    }

    public PathFinder(int r, int c){
        grid = new int[r][c];
    }

    public PathFinder(int [][]g){
        grid = g;
    }

}
