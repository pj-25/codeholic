import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class AdjacentNode {
    private Node adjNode;
    private Line edge;

    public AdjacentNode(Node adjNode){
        this.adjNode = adjNode;
    }

    public AdjacentNode(Node adjNode, Line edge){
        this.adjNode = adjNode;
        this.edge = edge;
    }

    public AdjacentNode(Node adjNode, Line edge, Pane displayPane){
        this(adjNode, edge);
        drawEdge(displayPane);
    }

    public void drawEdge(Pane displayPane){
        displayPane.getChildren().add(edge);
    }

    @Override
    public String toString() {
        return adjNode.toString();
    }

    public Node getAdjNode() {
        return adjNode;
    }

    public void setAdjNode(Node adjNode) {
        this.adjNode = adjNode;
    }

    public Line getEdge() {
        return edge;
    }

    public void setEdge(Line edge) {
        this.edge = edge;
    }

}
