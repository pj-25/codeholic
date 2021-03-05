package chitChat.chitChatDownloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ProgressBar;

public class DownloadChitChat extends Application {

    private ProgressBar progressBar = new ProgressBar();
    private Label status;
    private final String DOWNLOAD_LINK = "https://github.com/pj-25/codeholic/raw/main/chitChatAppV2.jar";
    private Button downloadBtn;

    private boolean isPaused = false;
    private final Object pauseLock = new Object();

    @Override
    public void start(Stage primaryStage){
        downloadBtn = new Button("Download");
        downloadBtn.setOnAction(e->{
            try{
                String btnStatus = downloadBtn.getText();
                if(btnStatus.equals("Download") || btnStatus.equals("Resume")){
                    if(!isPaused){
                        download();
                    }
                    else{
                        resume();
                    }
                    downloadBtn.setText("Pause");
                }
                else if(btnStatus.equals("Pause")){
                    downloadBtn.setText("Resume");
                    isPaused = true;
                }
                else{
                    launchChitChat();
                }
            }
            catch (IOException x){
                status.setText("Unable to connect with Server :(");
            }
        });
        Label fileType = new Label("Linux/Ubuntu (15.3MB)");
        status = new Label("Click above to download");
        fileType.setFont(Font.font("Monospace", FontWeight.EXTRA_BOLD, 20));
        fileType.setTextFill(Color.GREEN);

        progressBar.setMinWidth(400);

        VBox vbox = new VBox(progressBar, downloadBtn, fileType, status);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        StackPane spane = new StackPane(vbox);
        spane.setAlignment(Pos.CENTER);
        spane.getStyleClass().add("login-background");

        Scene mainScene = new Scene(spane, 600, 400);
        mainScene.getStylesheets().add("chitChat/chitChatApp/style.css");

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("ChitChat");
        primaryStage.show();
    }

    public float getFileSize(){
        URLConnection urlConnection = null;
        try{
            URL url = new URL(DOWNLOAD_LINK);
            urlConnection = url.openConnection();
            ((HttpURLConnection) urlConnection).setRequestMethod("HEAD");
            urlConnection.getInputStream();
            return urlConnection.getContentLength();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if(urlConnection!=null)
                ((HttpURLConnection) urlConnection).disconnect();
        }
    }

    public void resume(){
        synchronized (pauseLock){
            isPaused = false;
            pauseLock.notifyAll();
        }
    }

    public void launchChitChat() {
        try{
            Process runChitChat = Runtime.getRuntime().exec("java -jar chitChatApp.jar");
        }
        catch (IOException e){
            status.setText("Unable to launch ChitChat :(");
        }
    }

    public void download() throws IOException{
        progressBar.setProgress(0);

        URL url = new URL(DOWNLOAD_LINK);
        URLConnection urlConnection = url.openConnection();

        final float TOTAL_SIZE = getFileSize();
        System.out.println(TOTAL_SIZE);

        InputStream inStream = urlConnection.getInputStream();
        FileOutputStream fout = new FileOutputStream("chitChatApp.jar");

        status.setText("Download in progress...");

        new Thread(()->{
            try{
                isPaused = false;
                float dsize = 0;
                byte []data = new byte[1024];
                int fp;
                while((fp=inStream.read(data, 0,1024))>=0){
                    synchronized (pauseLock){
                        while(isPaused){
                            pauseLock.wait();
                        }
                    }
                    fout.write(data, 0, fp);
                    dsize += fp;
                    progressBar.setProgress(dsize / (TOTAL_SIZE));
                }
                inStream.close();
                fout.close();
                Platform.runLater(()-> {
                    downloadBtn.setText("Launch");
                    status.setText("Downloaded successfully!");
                });
            }
            catch (IOException | InterruptedException x){
                status.setText("Error in download :(");
            }
        }).start();
    }

    public static void main(String []s) throws IOException {
        launch(s);
    }
}