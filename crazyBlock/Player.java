package crazyBlock;

import javafx.event.Event;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;

public interface Player {
    int getPosX();
    int getPosY();
    void setPosX(int x);
    void setPosY(int y);
    void onKeyEvent(KeyEvent e);
    void move(int dir);
    void moveOnPath(ArrayList<Integer> path);
}
