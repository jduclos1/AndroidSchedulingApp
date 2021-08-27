package schedulingappnew.Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import static schedulingappnew.Model.MyConnector.validateCreds;

public class LoginScreenController implements Initializable{

    @FXML private AnchorPane loginScreen;
    @FXML private TextField userTextField;
    @FXML private TextField passTextField;
    @FXML private Button submitButton;
    @FXML private Label userLabel;
    @FXML private Label passLabel;
    @FXML private Label titleLabel;
    @FXML private Label errorMessage;
    public static int dbErrors = 0;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLang();
    }
    
    @FXML private void submitButtonHandler(ActionEvent event) throws SQLException {
        String userName = userTextField.getText().toLowerCase();
        String passWord = passTextField.getText();
        passTextField.setText("");
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Login", Locale.getDefault());
        
        if (userName.equals("") || passWord.equals("")){
            errorMessage.setText(resource.getString("emptyUserPass"));
            return;
        } 
        //Check user login creds
        boolean checkCreds = validateCreds(userName, passWord);
        if (checkCreds){
           try {
                //Display main screen
                Parent mainScreen = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene mainScene = new Scene(mainScreen);
                Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainStage.setTitle(resource.getString("TitleBar"));
                mainStage.setScene(mainScene); 
                mainStage.show();
                mainStage.centerOnScreen();
                
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        else if (dbErrors > 0) {
            //display a connection error message
            errorMessage.setText(resource.getString("connError"));
        }
        else {
            errorMessage.setText(resource.getString("wrongUserPw"));
        }
    }
    
    public static void dbErrorCount(){
        dbErrors++;
    }
    
    private void setLang(){
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Login", Locale.getDefault());
        titleLabel.setText(resource.getString("Title"));
        userLabel.setText(resource.getString("UserName"));
        passLabel.setText(resource.getString("Password"));
        submitButton.setText(resource.getString("submit"));
    }
}
