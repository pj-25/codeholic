package crazyBallGame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class BouncingBall extends Circle{
    private double pathSlope = 1;
    private double pathIntercept = 0;
    private final int steps = 1;

    public BouncingBall(double x, double y, double r){
        super(x,y,r);
        setFill(Color.YELLOW);
    }

    public void reset(){
        setCenterY(CrazyBallGame.PLAYGROUND_HEIGHT/2.0);
        setCenterX(CrazyBallGame.PLAYGROUND_WIDTH/2.0);
        pathSlope = 1;
        pathIntercept = 0;
    }

    public double getPathSlope() {
        return pathSlope;
    }

    public void setPathSlope(double pathSlope) {
        this.pathSlope = pathSlope;
    }

    public double getPathIntercept() {
        return pathIntercept;
    }

    public void setPathIntercept(double pathIntercept) {
        this.pathIntercept = pathIntercept;
    }


    public void bounce(double surfaceSlope){
        double incidentAngle;
        if(surfaceSlope == Double.POSITIVE_INFINITY)
            incidentAngle = Math.atan(pathSlope);
        else
            incidentAngle = Math.atan( ( 1 + surfaceSlope * pathSlope) / (surfaceSlope - pathSlope) );
        double tanTheta = Math.tan(incidentAngle);
        double tan2Theta = 2 * tanTheta / (1 - (tanTheta*tanTheta));
        pathSlope = ( pathSlope + tan2Theta) / ( 1 - (tan2Theta * pathSlope));
        pathIntercept = (getCenterY() - (pathSlope * getCenterX()));
    }

    public void move(int dir){
        double nextX = getCenterX() + dir * steps;
        double nextY = (pathSlope * nextX) + pathIntercept;
        setCenterX(nextX);
        setCenterY(nextY);
    }

    public boolean isCollidingTo(Shape shape){
        return Shape.intersect(this, shape).getBoundsInLocal().getWidth() != -1;
    }

}
