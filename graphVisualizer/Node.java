package graphVisualizer;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Node {
    private int nodeID;
    private Circle shape;
    private ArrayList<AdjacentNode> adjacentNodes;

    public ArrayList<AdjacentNode> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(ArrayList<AdjacentNode> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    public Node(int n){
        this(n, null);
    }

    public Node(int n, double nodeX, double nodeY, double nodeR, Pane displayNode){
        this(n, createNode(nodeX, nodeY, nodeR));
        drawNode(displayNode);
    }

    public Node(int nid, Circle shape){
        nodeID = nid;
        this.shape = shape;
        adjacentNodes = new ArrayList<>();
    }

    public Node(int nid, Circle shape, Pane displayPane){
        this(nid, shape);
        drawNode(displayPane);
    }

    public void drawNode(Pane displayPane) {
        Text textNodeID = new Text(String.valueOf(nodeID));
        textNodeID.setFill(Color.YELLOW);
        textNodeID.setFont(Font.font(shape.getRadius()));
        StackPane nodePane = new StackPane(shape, textNodeID);
        nodePane.setLayoutX(shape.getCenterX() - shape.getRadius());
        nodePane.setLayoutY(shape.getCenterY() - shape.getRadius());
        displayPane.getChildren().add(nodePane);
    }

    public static Circle createNode(double x, double y, double r){
        Circle gnode = new Circle(x,y, r);
        gnode.setFill(Color.RED);
        return gnode;
    }

    void setNode(Pane displayPane, Circle gnode){
        shape = gnode;
        drawNode(displayPane);
    }

    public Circle getShape() {
        return shape;
    }

    public void setShape(Circle shape) {
        this.shape = shape;
    }

    int getNodeID(){
        return nodeID;
    }
    void setNodeID(int n){
        nodeID = n;
    }

    @Override
    public String toString() {
        return String.valueOf(nodeID);
    }

    public void addAdjacentNode(Node adjNode, Line edge){
        adjacentNodes.add(new AdjacentNode(adjNode, edge));
    }

    public void addAdjacentNode(Node adjNode, Line edge, Pane displayPane) {
        adjacentNodes.add(new AdjacentNode(adjNode, edge, displayPane));
    }

    public void addAdjacentNode(Node adjNode, Pane displayPane) {
        adjacentNodes.add(new AdjacentNode(adjNode, Graph.createEdge(this, adjNode), displayPane));
    }

}
