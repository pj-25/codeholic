package graphVisualizer;


import javafx.animation.Animation;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class GraphVisualizer extends Application {
    private Graph graph;
    private Stage primaryStage;
    private Pane displayPane;

    private String savedFile = null;

    private int selectedNode = -1;

    DoubleProperty nodeRadius;

    public static void main(String[] s){
        launch(s);
    }

    @Override
    public void start(Stage mainStage){
        this.primaryStage = mainStage;
        Rectangle board = new Rectangle(0,0, 500,400);
        board.setFill(Color.rgb(43,43,43));
        board.setOnMouseClicked(e->addNode(e.getX(), e.getY()));

        displayPane = new Pane(board);
        graph = new Graph(displayPane);

        Button actionBtn = new Button("Pause");
        Button stopBtn = new Button("Stop");

        VBox animPane = new VBox(actionBtn, stopBtn);
        animPane.setPadding(new Insets(10));
        animPane.setSpacing(10);
        animPane.setAlignment(Pos.CENTER);
        animPane.setDisable(true);

        Button runDFSBtn = new Button("Run DFS");
        runDFSBtn.setOnAction(e->{
            if(selectedNode != -1) {
                animPane.setDisable(false);
                AlgoVisualizer dfsVisualizer = new AlgoVisualizer(graph);
                dfsVisualizer.runDFS(selectedNode);
                stopBtn.setOnAction(s-> dfsVisualizer.stop());
                actionBtn.setOnAction(p->{
                    if(actionBtn.getText().equals("Play")){
                        dfsVisualizer.play();
                        actionBtn.setText("Pause");
                    }   
                    else{
                        dfsVisualizer.pause();
                        actionBtn.setText("Play");
                    }
                });
                selectedNode = -1;
            }
        });

        Button runBFSBtn = new Button("Run BFS");
        runBFSBtn.setOnAction(e->{
            if(selectedNode != -1) {
                AlgoVisualizer dfsVisualizer = new AlgoVisualizer(graph);
                dfsVisualizer.runBFS(selectedNode);
                selectedNode = -1;
            }
        });

        Button saveBtn = new Button("Save Graph");
        saveBtn.setOnAction(e-> promptToSave());

        Button loadBtn = new Button("Load Graph");
        loadBtn.setOnAction(e->promptToLoad());

        Button clearBtn = new Button("Clear All");
        clearBtn.setOnAction(e->{
            displayPane.getChildren().clear();
            displayPane.getChildren().add(board);
            graph = new Graph(displayPane);
        });

        Label radiusSliderLbl = new Label("Radius");
        Slider radiusSlider = new Slider(15, 25, 5);
        radiusSlider.setBlockIncrement(5);
        radiusSlider.setShowTickMarks(true);
        radiusSlider.setShowTickLabels(true);
        radiusSlider.setMajorTickUnit(5);
        nodeRadius = new SimpleDoubleProperty(15);
        nodeRadius.bind(radiusSlider.valueProperty());
        radiusSliderLbl.setLabelFor(radiusSlider);

        VBox optionPane = new VBox(saveBtn, loadBtn, runDFSBtn, runBFSBtn, radiusSliderLbl, radiusSlider, clearBtn);
        optionPane.setSpacing(10);
        //optionPane.setAlignment(Pos.CENTER);
        optionPane.setPadding(new Insets(10));
        optionPane.setMinWidth(120);


        VBox sidePane = new VBox(optionPane, animPane);

        ScrollPane mainWindow = new ScrollPane();
        mainWindow.setContent(displayPane);
        mainWindow.setPrefViewportHeight(300);


        HBox mainPane = new HBox(sidePane, mainWindow);
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setSpacing(10);

        Scene visualBoard = new Scene(mainPane, 630,400);
        board.widthProperty().bind(visualBoard.widthProperty());
        board.heightProperty().bind(visualBoard.heightProperty());


        visualBoard.getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), ()->{
            if(graph.size() > 0){
                if(savedFile != null) {
                    try {
                        saveGraph(savedFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                    promptToSave();
            }
        });


        primaryStage.setScene(visualBoard);
        primaryStage.setTitle("Graph Visualizer");
        primaryStage.show();
    }

    void addNode(double x, double y){
        addNode(x, y, nodeRadius.doubleValue());
        //System.out.println(graph);
    }

    void addNode(double x, double y, double radius){
        graph.addNode(x,y,radius);
        Node node = graph.getNode(graph.size()-1);
        displayPane.getChildren().get(displayPane.getChildren().size()-1).setOnMouseClicked(e->{
            if(selectedNode == -1){
                selectedNode = node.getNodeID();
                node.getShape().setFill(Color.DARKRED);
            }
            else if(selectedNode != node.getNodeID()){
                addEdge(selectedNode, node.getNodeID());
                graph.getNode(selectedNode).getShape().setFill(Color.RED);
                selectedNode = -1;
            }
        });
    }

    void saveGraph(File sfile) throws IOException{
        FileWriter fout = new FileWriter(sfile);
        fout.append(graph.size() + "\n");
        for(Node node:graph.getNodeList()){
            fout.append(node.getShape().getCenterX() + " " + node.getShape().getCenterY() + " " + node.getShape().getRadius() + "\n");
        }
        fout.append(graph.getEdgeList().size() + "\n");
        for(ArrayList<Integer> edge:graph.getEdgeList()){
            fout.append(edge.get(0) + " " + edge.get(1) +"\n");
        }
        this.savedFile = sfile.getName();
        fout.close();

        popupMsg("Saved!");
    }

    void popupMsg(String msg){
        Popup popup = new Popup();
        Label label = new Label(msg);
        label.setFont(Font.font(25));
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-radius: 50px; -fx-background-color: rgba(0,0,0, 0.3);");
        label.setTextFill(Color.WHITE);
        label.setPadding(new Insets(20));
        popup.getContent().add(label);
        //popup.setX((primaryStage.getWidth()/2) - 120);
        //popup.setY((primaryStage.getHeight()/3));
        popup.setAutoHide(true);
        popup.show(primaryStage);
    }


    void saveGraph(String filename) throws IOException {
        File savedFile = new File(filename);
        saveGraph(savedFile);
    }

    void loadGraph(String filename) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(filename));
        int n = scan.nextInt();

        graph= new Graph(displayPane);

        //displayPane.getChildren().clear();
        for(int i=0;i<n;i++){
            double x = scan.nextDouble();
            double y = scan.nextDouble();
            double r = scan.nextDouble();
            addNode(x, y, r);
        }
        int m = scan.nextInt();
        for(int i=0;i<m;i++){
            int u = scan.nextInt();
            int v = scan.nextInt();
            addEdge(u, v);
        }
        savedFile = filename;
    }

    void promptToLoad(){
        FileChooser loader = new FileChooser();
        loader.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graphs(*.gph)", "*.gph"));
        loader.setTitle("Load Graph");
        File loadFile = loader.showOpenDialog(primaryStage);
        if(loadFile != null){
            try {
                loadGraph(loadFile.getName());
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
    }

    void promptToSave(){
        if(graph.size()>0){
            FileChooser saveWindow = new FileChooser();
            saveWindow.setTitle("Save Graph");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graphs(*.gph)", "*.gph");
            saveWindow.getExtensionFilters().add(extFilter);

            File sfile = saveWindow.showSaveDialog(primaryStage);

            if(sfile != null){
                try {
                    saveGraph(sfile.getName()+".gph");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    /*
    void promptToSave(){
        PromptWindow saveWindow = new PromptWindow("Name", "Save");
        saveWindow.getButton().setOnAction(e->{
            if(!saveWindow.getTextField().getText().isEmpty()){
                try {
                    saveGraph(saveWindow.getTextField().getText() + ".gph");
                    saveWindow.getWindowStage().close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        saveWindow.run();
    }

    void promptToLoad(){
        PromptWindow loadWindow = new PromptWindow("Name", "Load");
        loadWindow.getButton().setOnAction(e->{
            if(!loadWindow.getTextField().getText().isEmpty()){
                try{
                    loadGraph(loadWindow.getTextField().getText() + ".gph");
                    loadWindow.getWindowStage().close();
                }
                catch (FileNotFoundException fileNotFoundException){
                    //fileNotFoundException.printStackTrace();
                    loadWindow.getTextField().setText("**Invalid name!");
                }
            }
        });
        loadWindow.getTextField().setOnMouseClicked(e->loadWindow.getTextField().clear());
        //loadWindow.run();

    }*/

    void addEdge(int u, int v){
        graph.addUndirectedEdge(u,v);
    }
}
