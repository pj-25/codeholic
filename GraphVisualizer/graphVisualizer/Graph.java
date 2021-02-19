package graphVisualizer;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;

import java.lang.Math;

public class Graph {
    private Pane displayPane;
    private ArrayList<Node> nodeList;
    private ArrayList<ArrayList<Integer>> edgeList;

    //parameterized constructor to initialize graph
    public Graph(Pane displayPane, int n){
        nodeList = new ArrayList<>();
        this.displayPane = displayPane;
        for(int i=0;i<n;i++){
            nodeList.add(new Node(i));
        }
        edgeList = new ArrayList<>();
    }

    //default constructor
    public Graph(Pane displayPane){
        this(displayPane, 0);
    }

    public int size(){
        return nodeList.size();
    }

    public Node getNode(int i){
        return nodeList.get(i);
    }

    public Pane getDisplayPane() {
        return displayPane;
    }

    public void setDisplayPane(Pane displayPane) {
        this.displayPane = displayPane;
    }

    public ArrayList<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(ArrayList<Node> nodeList) {
        this.nodeList = nodeList;
    }

    void addUndirectedEdge(int u, int v){
        Line edge = createEdge(getNode(u), getNode(v));
        getNode(u).addAdjacentNode(getNode(v), displayPane);
        getNode(v).addAdjacentNode(getNode(u), edge);
        addEdge(u,v);
    }

    void addEdge(int u, int v){
        ArrayList<Integer> e = new ArrayList<>();
        e.add(u);
        e.add(v);
        edgeList.add(e);
    }

    void addDirectedEdge(int u, int v){
        getNode(u).addAdjacentNode(getNode(v), displayPane);
    }

    void drawEdge(int u, int v){
        Node fromNode = getNode(u);
        Node toNode = getNode(v);
        fromNode.addAdjacentNode(toNode, displayPane);
    }


    static Line createEdge(Node fromNode, Node toNode){
        double x1 = fromNode.getShape().getCenterX();
        double y1 = fromNode.getShape().getCenterY();
        double x2 = toNode.getShape().getCenterX();
        double y2 = toNode.getShape().getCenterY();

        int s = 1;
        if(x1 > x2)
            s = -1;

        double theta = Math.atan((y2 - y1)/(x2 - x1));

        double r = fromNode.getShape().getRadius();
        double xl1 = x1 + (s * r * Math.cos(theta));
        double yl1 = y1 + (s * r * Math.sin(theta));
        r = toNode.getShape().getRadius();
        double xl2 = x2 - (s * r * Math.cos(theta));
        double yl2 = y2 - (s * r * Math.sin(theta));

        return createEdge(xl1, yl1, xl2, yl2);
    }

    static Line createEdge(double fromX, double fromY, double toX, double toY){
        Line edge = new Line(fromX, fromY, toX, toY);
        //edge.setOpacity(0.5);
        return edge;
    }

    void addNode(double x, double y, double r){
        nodeList.add(new Node(size(), x,y,r, displayPane));
    }

    void resetNodesColor(){
        for(Node node:nodeList){
            node.getShape().setFill(Color.RED);
        }
    }

    ArrayList<AdjacentNode> getAdjacentNodes(int i){
        return nodeList.get(i).getAdjacentNodes();
    }

    @Override
    public String toString(){
        String str = "";
        for(int i=0;i<nodeList.size();i++){
            str += i + ": [";
            for(AdjacentNode node:nodeList.get(i).getAdjacentNodes()){
                str += node + "  ";
            }
            str += "]\n";
        }
        return str;
    }

    ArrayList<Integer> runDFS(int s){
        boolean []visited = new boolean[size()];
        ArrayList<Integer> dfsTraversal = new ArrayList<>();
        dfs(dfsTraversal, s, visited);
        return dfsTraversal;
    }

    void dfs(ArrayList<Integer> dfsTraversal, int i, boolean[] visited){
        dfsTraversal.add(i);
        visited[i] = true;
        for(AdjacentNode adjnode: getAdjacentNodes(i)){
            if(!visited[adjnode.getAdjNode().getNodeID()]){
                dfs(dfsTraversal, adjnode.getAdjNode().getNodeID(), visited);
            }
        }
    }

    void setDisable(boolean status){
        displayPane.setDisable(status);
    }

    ArrayList<ArrayList<Integer>> getEdgeList(){
        return edgeList;
    }
}
