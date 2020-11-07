package interfaz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/ventana.fxml"));
        primaryStage.setTitle("Conteos de votos");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    //Claves int o String?

    public static void main(String[] args) {
        launch(args);
    }
}
