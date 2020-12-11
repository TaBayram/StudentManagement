package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Student Management System");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(1200);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Assets/appIcon.png")));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
