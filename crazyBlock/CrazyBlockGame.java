package crazyBlock;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.*;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.util.*;
import java.io.FileNotFoundException;
import java.util.*;



public class CrazyBlockGame extends Application {
    Stage psref, gameplay;
    Timeline snakeanim;
    Rectangle snake;
    Line l[];
    int moveDir, score;

    public static void main(String[] s) {
        launch(s);
    }
    @Override
    public void start(Stage stage) {
        psref = stage;
        stage.setScene(menuScene(stage));
        stage.setTitle("CrazySnake");
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        stage.setOnCloseRequest(e ->
        {
            e.consume();
            confirmationMsg();
        });
    }
    void eatFood(Circle food, Label disscore) {
        Random r = new Random();
        int v;
        disscore.setText(Integer.toString(++score));
        while ((v = r.nextInt(1110)) <= 40) ;
        food.setCenterX(v);
        while ((v = r.nextInt(720)) <= 40) ;
        food.setCenterY(v);
    }

    void gameOver(int score) {
        Stage gos = new Stage();
        Text gtxt = new Text("Game Over");
        gtxt.setFont(Font.font("Showcard Gothic", FontWeight.BOLD, FontPosture.REGULAR, 70));
        gtxt.setFill(Color.GREEN);

        Text scoretxt = new Text("Score:" + Integer.toString(score));
        scoretxt.setFont(Font.font("Papyrus", FontWeight.BOLD, FontPosture.REGULAR, 50));
        Button pbtn = new Button("Play Again");
        pbtn.setOnAction(e ->
        {
            gos.close();
            gameplay.close();
            playScene();
        });
        Button mbtn = new Button("Main Menu");
        mbtn.setOnAction(e ->
        {
            gos.close();
            gameplay.close();
        });
        HBox hpane = new HBox(10, pbtn, mbtn);
        hpane.setAlignment(Pos.CENTER);
        VBox vpane = new VBox(10, gtxt, scoretxt, hpane);
        vpane.setPadding(new Insets(20));
        vpane.setAlignment(Pos.CENTER);
        Scene s = new Scene(vpane);

        // gos.initModality(Modality.APPLICATION_MODAL);
        gos.initStyle(StageStyle.UNDECORATED);
        gos.setScene(s);
        gos.toFront();
        gos.show();
    }

    void moveRight() {
        moveDir = 2;
        snake.setX(snake.getX() + 2);
    }

    void moveLeft() {
        moveDir = 4;
        snake.setX(snake.getX() - 2);
    }

    void moveUp() {
        moveDir = 1;
        snake.setY(snake.getY() - 2);
    }

    void moveDown() {
        moveDir = 3;
        snake.setY(snake.getY() + 2);
    }

    void loadingScene(Stage stage) {
        Stage loadstage = new Stage();
        Rectangle lbar = new Rectangle(200, 385, 10, 30);
        lbar.setFill(Color.RED);
        KeyFrame load = new KeyFrame(Duration.millis(20), e -> lbar.setWidth(lbar.getWidth() + 5));
        Timeline loading = new Timeline(load);
        loading.setCycleCount(170);
        loading.play();
        loading.setOnFinished(e ->
        {
            loadstage.close();
            playScene();
        });
        Pane p = new Pane(lbar);
        Scene loadanim = new Scene(p, Color.WHEAT);

        loadstage.setScene(loadanim);
        loadstage.setFullScreen(true);
        loadstage.show();
    }

    Scene playScene() {
        score = 0;
        Label scorelbl = new Label(Integer.toString(score));
        scorelbl.setFont(Font.font("Papyrus", 50));

        l = new Line[4];
        l[0] = new Line(20, 20, 1160, 20);
        l[1] = new Line(1160, 20, 1160, 760);
        l[2] = new Line(1160, 760, 20, 760);
        l[3] = new Line(20, 760, 20, 20);

        Rectangle playground = new Rectangle(0, 0, 1110, 720);
        playground.setFill(Color.LIGHTGREEN);
        playground.setStroke(Color.GREEN);
        playground.setStrokeWidth(10);
        playground.setStrokeType(StrokeType.OUTSIDE);

        snake = new Rectangle(25, 25, 30, 30);
        snake.setFill(Color.RED);

        Circle food = new Circle(776, 532, 10);
        food.setFill(Color.GREEN);
        ArrayList<Shape> obstacle = new ArrayList<Shape>();
        obstacle.addAll(level1());
        for (Line y : l)
            obstacle.add(y);
        KeyFrame snakemove = new KeyFrame(Duration.millis(20), e ->
        {
            for (Shape x : obstacle) {
                if (snake.getBoundsInParent().intersects(x.getBoundsInParent())) {
                    System.out.println("GamOver...");
                    snakeanim.stop();
                    gameplay.close();
                    gameOver(score);
                }
            }
            if (snake.getBoundsInParent().intersects(food.getBoundsInParent())) {
                //snakemove.getTime().bind(snakemove.getTime().subtract(Duration.millis(800) ) );
                eatFood(food, scorelbl);
            } else
                switch (moveDir) {
                    case 1:
                        moveUp();
                        break;
                    case 2:
                        moveRight();
                        break;
                    case 3:
                        moveDown();
                        break;
                    case 4:
                        moveLeft();
                }
        });

        snakeanim = new Timeline(snakemove);
        snakeanim.setCycleCount(Animation.INDEFINITE);
        moveDir = 3;
        snakeanim.play();


        Text scoretxt = new Text("Score:");
        scoretxt.setFont(Font.font("Papyrus", 40));


        Group gcomp = new Group();

        Button pbtn = new Button("Pause");
        pbtn.setOnAction(e -> {
            if (snakeanim.getCurrentRate() == 0) {
                pbtn.setText("Pause");
                snakeanim.play();
            } else {
                pbtn.setText("Play");
                snakeanim.pause();
            }
        });

        Button exit=new Button("MAIN MENU");
        exit.setOnAction(event->{
            snakeanim.stop();
            gameplay.close();
        });
        VBox controlpane = new VBox(scoretxt, scorelbl, pbtn,exit);
        controlpane.setAlignment(Pos.TOP_CENTER);

        HBox hpane = new HBox(10, playground, controlpane);

        gcomp.getChildren().addAll(hpane, snake, food);
        gcomp.getChildren().addAll(l);
        gcomp.getChildren().addAll(level1());


        hpane.setPadding(new Insets(20));
        Scene game = new Scene(gcomp);

        game.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.RIGHT)
                moveDir = 2;
            else if (e.getCode() == KeyCode.LEFT)
                moveDir = 4;
            else if (e.getCode() == KeyCode.UP)
                moveDir = 1;
            else if (e.getCode() == KeyCode.DOWN)
                moveDir = 3;
        });
        gameplay = new Stage();
        gameplay.setFullScreen(true);
        gameplay.setScene(game);
        gameplay.show();
        return game;
    }

    Scene menuScene(Stage stage)
    {
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.CORNSILK), new Stop(0.8, Color.WHEAT));
        Rectangle box = new Rectangle(150, 150, 200, 300);
        box.setFill(Color.LIGHTGREEN);
        Rectangle background = new Rectangle(0, 0, 650, 500);
        background.setFill(lg);

        Text label=new Text("Crazy Block");
        Text label7=new Text("PLAY GAME");
        Text label2=new Text("SETTINGS");
        Text label3=new Text("HELP");
        Text label4=new Text("CREDITS");
        Text label6=new Text("EXIT");

        label2.setFont(Font.font("Terminal",20));
        label3.setFont(Font.font("Terminal",20));
        label4.setFont(Font.font("Terminal",20));
        label7.setFont(Font.font("Terminal",20));
        label6.setFont(Font.font("Terminal",20));

        label.setFill(Paint.valueOf("RED"));
        label2.setFill(Paint.valueOf("RED"));
        label3.setFill(Paint.valueOf("RED"));
        label4.setFill(Paint.valueOf("RED"));
        label7.setFill(Paint.valueOf("RED"));
        label6.setFill(Paint.valueOf("RED"));

        Button b1=new Button(" ",label);
        Button b2=new Button("",label7);
        Button b4=new Button("",label2);
        Button b5=new Button("",label3);
        Button b6=new Button("",label4);
        Button b8=new Button("",label6);

        b1.setStyle("-fx-background-color:Transparent");
        b2.setStyle("-fx-background-color:Transparent");
        b4.setStyle("-fx-background-color:Transparent");
        b5.setStyle("-fx-background-color:Transparent");
        b6.setStyle("-fx-background-color:Transparent");
        b8.setStyle("-fx-background-color:Transparent");


        b2.setOnAction(event -> {
            loadingScene(stage);
        });
        b6.setOnAction(event -> {
            try {
                stage.setScene(credits(stage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        b4.setOnAction(event -> {
            try {
                stage.setScene(Setting(stage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        b5.setOnAction(event -> {
            try {
                stage.setScene(help(stage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        b8.setOnAction(event -> confirmationMsg());

        label.setFont(Font.font("Showcard Gothic", FontWeight.BOLD, FontPosture.REGULAR, 70));
        label.setFill(Color.GREEN);

        VBox vBox=new VBox(b1,b2,b4,b5,b6,b8);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(20));

        StackPane bpane = new StackPane(background, box, vBox);
        return new Scene(bpane);
    }

    ArrayList<Shape> level1() {
        ArrayList<Shape> group = new ArrayList<>();
        Rectangle o[] = new Rectangle[23];
        o[0] = new Rectangle(210, 31, 30, 90);
        o[1] = new Rectangle(90, 240, 90, 30);
        o[2] = new Rectangle(330, 360, 30, 150);
        o[3] = new Rectangle(120, 450, 30, 150);
        o[4] = new Rectangle(839, 89, 240, 30);
        o[5] = new Rectangle(1050, 150, 60, 30);
        o[6] = new Rectangle(540, 270, 90, 90);
        o[7] = new Rectangle(840, 540, 30, 120);
        o[8] = new Rectangle(810, 450, 150, 30);
        o[9] = new Rectangle(510, 510, 60, 60);
        o[10] = new Rectangle(300, 60, 90, 30);
        o[11] = new Rectangle(360, 90, 90, 30);
        o[12] = new Rectangle(750, 240, 90, 30);
        o[13] = new Rectangle(450, 180, 90, 30);
        o[14] = new Rectangle(450, 210, 30, 60);
        o[15] = new Rectangle(270, 540, 120, 30);
        o[16] = new Rectangle(390, 540, 30, 150);
        o[17] = new Rectangle(660, 360, 90, 30);
        o[18] = new Rectangle(630, 360, 30, 120);
        o[19] = new Rectangle(750, 120, 30, 30);
        o[20] = new Rectangle(720, 120, 30, 120);
        o[21] = new Rectangle(1049, 600, 30, 90);
        o[22] = new Rectangle(989, 660, 90, 30);
        for (int i = 0; i < 23; i++) {

            o[i].setFill
                    (Color.FIREBRICK);
            group.add(o[i]);
        }
        return group;
    }

    void confirmationMsg() {
        Stage cs = new Stage();
        Label lbl = new Label("Quit??");
        Button by = new Button();
        by.setText("Yes");
        by.setOnAction(e -> {
            cs.close();
            psref.close();
        });
        Button bn = new Button();
        bn.setText("No");
        bn.setOnAction(e -> cs.close());
        VBox vp = new VBox(10);
        HBox hp = new HBox(10, by, bn);
        vp.getChildren().addAll(lbl, hp);
        vp.setAlignment(Pos.CENTER);
        vp.setPadding(new Insets(20));
        Scene s = new Scene(vp);
        cs.setTitle("confirm?");
        cs.setScene(s);
        cs.setResizable(false);
        cs.initStyle(StageStyle.UTILITY);
        cs.initModality(Modality.APPLICATION_MODAL);
        cs.show();
    }


    Scene credits(Stage stage) throws FileNotFoundException
    {
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.CORNSILK), new Stop(0.8, Color.WHEAT));
        Rectangle box = new Rectangle(150, 150, 330, 330);
        box.setFill(Color.LIGHTGREEN);
        Rectangle background = new Rectangle(0, 0, 650, 500);
        background.setFill(lg);

        Label credit1=new Label("Artists");
        Label credit2=new Label("Prashant Joshi");
        Label credit3=new Label("Deep Lad");
        Label credit4=new Label("Riya Bhatiya");
        Label credit5=new Label("Mayur Jodhani");
        Label credit6=new Label("QA Manager");
        Label credit7=new Label("Prashant Joshi");
        Label credit8=new Label("QA Project Co-ordinator");
        Label credit9=new Label("Deep Lad");

        credit1.setFont(Font.font("Papyrus",20));
        credit6.setFont(Font.font("Papyrus",20));
        credit8.setFont(Font.font("Papyrus",20));

        Text lableexit=new Text("MAIN MENU");
        lableexit.setFont(Font.font("Terminal",20));
        lableexit.setFill(Paint.valueOf("RED"));
        Button button=new Button("",lableexit);
        button.setStyle("-fx-background-color:Transparent");
        button.setOnAction(event -> {
            stage.setScene(menuScene(stage));
        });

        VBox vBox=new VBox(5,credit1,credit2,credit3,credit4,credit5,credit6,credit7,credit8,credit9,button);
        vBox.setAlignment(Pos.CENTER);
        VBox vBox1=new VBox(10,vBox);

        vBox1.setAlignment(Pos.CENTER);
        StackPane bpane = new StackPane(background, box, vBox);
        return new Scene(bpane);
    }
    Scene Setting(Stage stage) throws FileNotFoundException
    {
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.CORNSILK), new Stop(0.8, Color.WHEAT));
        Rectangle box = new Rectangle(150, 150, 250, 200);
        box.setFill(Color.LIGHTGREEN);
        Rectangle background = new Rectangle(0, 0, 650, 500);
        background.setFill(lg);

        GridPane gridPane=new GridPane();
        gridPane.setHgap(50);
        gridPane.setVgap(10);

        Text label=new Text("Level");
        label.setFill(Paint.valueOf("#000000"));
        label.setTextAlignment(TextAlignment.LEFT);
        ChoiceBox<String> comboBox2=new ChoiceBox<String>();
        comboBox2.getItems().addAll("1","2","3");
        comboBox2.getSelectionModel().select("1");
        comboBox2.setDisable(true);


        Text label4=new Text("Language");
        label4.setFill(Paint.valueOf("#000000"));
        ChoiceBox<String> comboBox=new ChoiceBox<String>();
        comboBox.getItems().addAll("ENGLISH","HINDI");
        comboBox.getSelectionModel().select("ENGLISH");
        comboBox.setDisable(true);

        Text label5=new Text("Difficulty");
        label5.setFill(Paint.valueOf("#000000"));
        ChoiceBox<String> comboBox5=new ChoiceBox<String>();
        comboBox5.getItems().addAll("EASY","MEDIUM","HARD");
        comboBox5.getSelectionModel().select("EASY");

        Text lableexit=new Text("MAIN MENU");
        lableexit.setFont(Font.font("Terminal",20));
        lableexit.setFill(Paint.valueOf("RED"));
        Button button=new Button("",lableexit);
        button.setStyle("-fx-background-color:Transparent");
        button.setOnAction(event ->{
            stage.setScene(menuScene(stage));
        });

        gridPane.addRow(1,label,comboBox2);
        gridPane.addRow(4,label4,comboBox);
        gridPane.addRow(5,label5,comboBox5);

        button.setAlignment(Pos.TOP_LEFT);
        VBox vBox=new VBox(10,gridPane,button);
        gridPane.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);

        StackPane bpane = new StackPane(background, box, vBox);
        return new Scene(bpane);
    }
    Scene help(Stage stage) throws FileNotFoundException
    {
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, Color.CORNSILK), new Stop(0.8, Color.WHEAT));
        Rectangle box = new Rectangle(150, 150, 650, 200);
        box.setFill(Color.LIGHTGREEN);
        Rectangle background = new Rectangle(0, 0, 650, 500);
        background.setFill(lg);

        Label label=new Label("Use \"Plat Game\" in the \"Game\" menu of the main window to start the game.\n In the game window, push the \"Pause\" button to pause the Game. All meals, including \nthe cyan ones,must be eaten before you may pass to the next round. During the Block's movement,\n the \"Pause\" button pauses the game; press \"Play\" to continue.");
        label.setFont(Font.font("Terminal",15));
        Text lableexit=new Text("MAIN MENU");
        label.setTextAlignment(TextAlignment.CENTER);
        lableexit.setFont(Font.font("Terminal",20));
        lableexit.setFill(Paint.valueOf("RED"));
        Button button=new Button("",lableexit);
        button.setStyle("-fx-background-color:Transparent");
        button.setOnAction(event -> {
            stage.setScene(menuScene(stage));
        });

        VBox vBox=new VBox(20,label,button);

        vBox.setAlignment(Pos.CENTER);

        StackPane bpane = new StackPane(background, box, vBox);
        return new Scene(bpane);
    }

}