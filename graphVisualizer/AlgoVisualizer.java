package graphVisualizer;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class AlgoVisualizer {
    private Graph graph;
    private boolean visited[];
    private Timeline visualizer;
    private boolean isRunning = false;

    public AlgoVisualizer(Graph g){
        graph = g;
    }

    public Timeline getVisualizer() {
        return visualizer;
    }

    public void setVisualizer(Timeline visualizer) {
        this.visualizer = visualizer;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void runBFS(int startNode){
        isRunning = true;
        Queue<Integer> queue = new LinkedList<>();
        visited = new boolean[graph.size()];

        graph.setDisable(true);

        queue.add(startNode);
        queue.add(startNode);
        visited[startNode] = true;
        graph.getNode(startNode).getShape().setFill(Color.ORANGE);

        KeyFrame bfsKeyFrame = new KeyFrame(Duration.seconds(1), e->{
            if(queue.size()>1){
                graph.getNode(queue.poll()).getShape().setStrokeWidth(0);
                int currentNode = queue.peek();
                graph.getNode(currentNode).getShape().setStroke(Color.DEEPPINK);
                graph.getNode(currentNode).getShape().setStrokeWidth(5);

                for(AdjacentNode anode: graph.getAdjacentNodes(currentNode)){
                    if(!visited[anode.getAdjNode().getNodeID()]){
                        visited[anode.getAdjNode().getNodeID()] = true;
                        queue.add(anode.getAdjNode().getNodeID());
                        anode.getAdjNode().getShape().setFill(Color.ORANGE);
                    }
                }
            }
            else{
                stop();
                isRunning = false;
                graph.getNode(queue.poll()).getShape().setStrokeWidth(0);
            }
        });
        visualizer = new Timeline(bfsKeyFrame);
        visualizer.setCycleCount(Animation.INDEFINITE);
        visualizer.setAutoReverse(false);
        visualizer.play();

    }

    public void runDFS(int startNode){
        isRunning = true;
        Stack<Integer> stack = new Stack<>();
        visited = new boolean[graph.size()];

        graph.setDisable(true);
        stack.push(startNode);

        KeyFrame dfsKeyFrame = new KeyFrame(Duration.seconds(1), e->{
            if(!stack.isEmpty()){
                int currentNode = stack.peek();
                visited[currentNode] = true;

                graph.getNode(currentNode).getShape().setStrokeWidth(5);
                graph.getNode(currentNode).getShape().setStroke(Color.DEEPPINK);

                if(stack.size() > 1){
                    graph.getNode(stack.get(stack.size()-2)).getShape().setStrokeWidth(0);
                }

                int i;
                for(i=0;i < graph.getAdjacentNodes(currentNode).size();i++){
                    Node node = graph.getAdjacentNodes(currentNode).get(i).getAdjNode();
                    if(!visited[node.getNodeID()]){
                        stack.push(node.getNodeID());
                        graph.getNode(currentNode).getShape().setFill(Color.DARKORANGE);
                        break;
                    }
                }
                if(i == graph.getAdjacentNodes(currentNode).size()){
                    int node = stack.pop();
                    graph.getNode(node).getShape().setFill(Color.GREY);
                    graph.getNode(node).getShape().setStrokeWidth(0);
                }
            }
            else{
                stop();

            }
        });
        visualizer = new Timeline(dfsKeyFrame);
        visualizer.setCycleCount(Animation.INDEFINITE);

        visualizer.play();
    }

    void play(){
        if(!isRunning)
        {
            isRunning = true;
            visualizer.play();
        }
    }

    void stop(){
        if(isRunning){
            graph.setDisable(false);
            graph.resetNodesColor();
            visualizer.stop();
            isRunning = false;
        }
    }

    void pause(){
        if(isRunning){
            isRunning = false;
            visualizer.pause();
        }
    }
}
