package View;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Mathieu on 08/06/2018.
 */
public class FileQueueView extends ScrollPane implements Observer {
    private VBox vBox;
    private Image fileImage;
    private Map<Integer, ProgressIndicator> pIndics;


    public FileQueueView()
    {
        vBox = new VBox();
        this.setContent(vBox);
        pIndics = new HashMap<Integer, ProgressIndicator>();

        FileInputStream fileImgStream = null;

    }


    @Override
    public void update(Observable o, Object arg) {
        FileQueueView saved = this;
        Platform.runLater(new Runnable() {
            @Override public void run() {
                String[] args = (String[]) arg;
                if (args[0] == "addFileTransfer")
                {
                    HBox hBox = new HBox();
                        ImageView fileIcon = new ImageView(fileImage);
                            fileIcon.setFitWidth(20);
                            fileIcon.setFitHeight(20);
                        hBox.getChildren().add(fileIcon);
                        Label filename = new Label(args[2]);
                        hBox.getChildren().add(filename);
                        ProgressIndicator progress = new ProgressIndicator(0);
                            pIndics.put(Integer.parseInt(args[1]), progress);
                            progress.getStyleClass().add("progress");
                        hBox.getChildren().add(progress);
                    vBox.getChildren().add(hBox);
                }
                else if (args[0] == "fileTransferStatus")
                {
                    double uploaded = Double.parseDouble(args[2]);
                    double filesize = Double.parseDouble(args[3]);

                    pIndics.get(Integer.parseInt(args[1])).setProgress(uploaded/filesize);


                }
            }
        });
    }
}
