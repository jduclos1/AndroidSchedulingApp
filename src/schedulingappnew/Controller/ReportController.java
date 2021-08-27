package schedulingappnew.Controller;

import java.io.IOException;
import java.net.URL;
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
import javafx.stage.Stage;
import schedulingappnew.Model.MyConnector;

public class ReportController implements Initializable{

    @FXML private Label reportsLabel;
    @FXML private Label reportsTypeLabel;
    @FXML private Label reportsScheduleLabel;
    @FXML private Label reportsMeetingLabel;
    @FXML private Button reportsTypeButton;
    @FXML private Button reportsScheduleButton;
    @FXML private Button reportsMeetingButton;
    @FXML private Button reportsExitButton;

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        setLang();
    }    
    
/****no longer needed because of the lambda expressions below****
    @FXML private void reportsTypeButtonHandler(ActionEvent event) {
        MyConnector.getAppTypeByMonthReport();
    }

    @FXML private void reportsScheduleButtonHandler(ActionEvent event) {
        MyConnector.getScheduleConsultants();
    }

    @FXML private void reportsMeetingButtonHandler(ActionEvent event) {
        MyConnector.getNextMeetingsByCust();
    }
*/
    
    @FXML private void reportsExitButtonHandler(ActionEvent event) throws IOException {
        // Return to main screen
        Parent mainScreen = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
        Scene mainScene = new Scene(mainScreen);
        Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.show();
        mainStage.centerOnScreen();
    }
    
    private void setLang(){
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Reports", Locale.getDefault());
        reportsLabel.setText(resource.getString("Reports"));
        reportsTypeLabel.setText(resource.getString("AppTypesByMonth"));
        reportsTypeButton.setText(resource.getString("Generate"));
        reportsScheduleLabel.setText(resource.getString("ConsultantSchedule"));
        reportsScheduleButton.setText(resource.getString("Generate"));
        reportsMeetingLabel.setText(resource.getString("CustSchedule"));
        reportsMeetingButton.setText(resource.getString("Generate"));
        reportsExitButton.setText(resource.getString("Exit"));
        //Lambda expression used to assign actions to 3 buttons - removing the need for 3 functions commented out above
        reportsTypeButton.setOnAction(event -> MyConnector.getAppTypeByMonthReport());
        reportsScheduleButton.setOnAction(event -> MyConnector.getScheduleConsultants());
        reportsMeetingButton.setOnAction(event -> MyConnector.getNextMeetingsByCust());
    }
}
