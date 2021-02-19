package crazyBlock;

import java.util.Scanner;

public class TestGrid {
    public static void main(String s[]){
        int r, c;
        Scanner scan = new Scanner(System.in);
        r = scan.nextInt();
        c = scan.nextInt();
        int [][]g = new int[r][c];
        int posx, posy;
        posx = scan.nextInt();
        posy = scan.nextInt();
        g[posx][posy] = 1;
        int tx, ty;
        tx = scan.nextInt();
        ty = scan.nextInt();
        g[tx][ty] = -1;
        Grid grid = new Grid(g);
        System.out.println(grid.getShortestPathToTarget(posx, posy));
    }
}
