/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servmonfx;

import java.lang.reflect.Field;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Linas Martusevicius
 * 
 * The main entry point for this JavaFX application.
 */
public class ServMonFx extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainViewFXML.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("View/MainViewFXML.css");
        stage.setScene(scene);
        stage.show();

        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.library.path", "lib/");
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        launch(args);
    }

}
