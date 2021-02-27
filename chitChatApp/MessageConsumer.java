package chitChat.chitChatApp;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MessageConsumer implements DataConsumer{
    private Pane displayPane;
    private ScrollPane scrollWindow;

    public MessageConsumer(ScrollPane scrollPane, Pane displayPane){
        this.scrollWindow = scrollPane;
        this.displayPane = displayPane;
    }

    public Pane getDisplayPane() {
        return displayPane;
    }

    public void setDisplayPane(Pane displayPane) {
        this.displayPane = displayPane;
    }

    public ScrollPane getScrollWindow() {
        return scrollWindow;
    }

    public void setScrollWindow(ScrollPane scrollWindow) {
        this.scrollWindow = scrollWindow;
    }

    public void displayMessage(String from, String msg, Pos pos){
        Label nameLbl = new Label(from + ":");
        nameLbl.setTextFill(Color.GREEN);
        Label msgLbl = new Label(msg);
        msgLbl.setTextFill(Color.WHITE);


        VBox msgPane = new VBox(nameLbl, msgLbl);
        msgLbl.setMinWidth(100);
        msgLbl.setAlignment(pos);
        msgPane.setPadding(new Insets(20));
        msgPane.setStyle("-fx-background-color: rgba(0,0,0, 0.2);");
        msgPane.setAlignment(pos);

        Platform.runLater(() -> {                               //**********
            displayPane.getChildren().add(msgPane);
            scrollWindow.setVvalue(1.0);
        });
    }

    @Override
    public void consume(String ...data){
        displayMessage(data[0], data[1], Pos.valueOf(data[2]));
    }
}
