package leafFinder.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
    Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/foundationWindow.fxml"));
        Scene scene = new Scene(loader.load());
        this.primaryStage = stage;
        primaryStage.setTitle("Leaf Finder System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
