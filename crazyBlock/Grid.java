package crazyBlock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Grid {
    private int[][] grid;

    public Grid(int r, int c) {
        grid = new int[r][c];
    }

    public Grid(int[][] g) {
        grid = g;
    }
    public Grid(Grid cgrid){
        this(cgrid.grid);
    }
    public void setXY(int x, int y, int v) {
        grid[y][x] = v;
    }

    public int getXY(int x, int y){
        if(inRange(y,x))
            return grid[y][x];
        return -999;
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getWidth(){
        return grid[0].length;
    }

    public int getHeight(){
        return grid.length;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    ArrayList<Integer> getShortestPathToTarget(int x, int y) {
        int[][] cgrid = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(grid[i], 0, cgrid[i], 0, grid[i].length);
        }
        Queue<int[]> queue = new LinkedList<>();
        int xy[] = {x, y};
        queue.add(xy);
        cgrid[y][x] = -2;

        int []dr = {-1, 0, 1, 0};
        int []dc = {0, 1, 0, -1};
        int r=0,c=0;
        boolean isFound = false;
        while (!queue.isEmpty() && !isFound) {
            xy = queue.poll();
            for (int i = 0; i < 4; i++) {
                r = xy[1] + dr[i];
                c = xy[0] + dc[i];
                if (!inRange(r,c))
                    continue;
                if (cgrid[r][c] == 0) {
                    cgrid[r][c] = i+1;
                    queue.add(new int[]{c,r});
                }
                else if(cgrid[r][c]==-1){
                    cgrid[r][c] = i+1;
                    isFound = true;
                    break;
                }
            }
        }

        if(!isFound)
            return null;

        /*for(int i=0;i< cgrid.length;i++){
            for(int j=0;j< cgrid[0].length;j++)
                System.out.print(cgrid[i][j] + " ");
            System.out.println();
        }*/
        ArrayList<Integer> path = new ArrayList<>();
        int dir = 0;
        while(cgrid[r][c]!=-2){
            dir = (cgrid[r][c] + 1)%4;
            path.add((dir+2)%4);
            r += dr[dir];
            c += dc[dir];
        }

        for(int i=0;i<path.size()/2;i++){
            int d = path.get(i);
            path.set(i, path.get(path.size()-i-1));
            path.set(path.size()-i-1, d);
        }
        return path;
    }

    public boolean inRange(int r, int c){
        return !(r < 0 || r >= grid.length || c < 0 || c >= grid[0].length);
    }

    @Override
    public String toString() {
        String str = "";
        for(int i=0;i< grid.length;i++){
            for(int j=0;j< grid[0].length;j++)
                str += (grid[i][j] + " ");
            str += "\n";
        }
        return str;
    }
}
