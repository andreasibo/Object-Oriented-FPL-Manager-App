package FPLManager.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FPLManagerApp extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("FPLManager App");
        primaryStage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/FPLManager/view/App.fxml"))));
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        FPLManagerApp.launch(args);
    }

}
