package schedulingappnew;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jduclos1
 */
public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Login", Locale.getDefault());
        
        //*******Uncomment To test different Locales********************
         //Locale.setDefault(new Locale.Builder().setLanguage("fr").build());
         //TimeZone.setDefault(TimeZone.getTimeZone("PST"));
       
        primaryStage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("Controller/LoginScreen.fxml"));
        primaryStage.setTitle(resource.getString("TitleBar"));;
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
