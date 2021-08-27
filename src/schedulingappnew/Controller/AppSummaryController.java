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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import schedulingappnew.Model.AppList;
import static schedulingappnew.Model.AppList.getAppList;
import schedulingappnew.Model.Appointment;
import schedulingappnew.Model.MyConnector;
import static schedulingappnew.Model.MyConnector.refreshAppList;

public class AppSummaryController implements Initializable{
    @FXML private Button sumCustDataButton;
    @FXML private Button sumModButton;
    @FXML private Button sumDelButton;
    @FXML private Button sumCancelButton;
    @FXML private TableView<Appointment> sumTable;
    @FXML private TableColumn<Appointment, String> sumTitleCol;
    @FXML private TableColumn<Appointment, String> sumContactCol;
    @FXML private TableColumn<Appointment, String> sumDateCol;
    @FXML private Label sumTitleLabel;
    @FXML private Label sumLocationLabel;
    @FXML private Label sumContactLabel;
    @FXML private Label sumURLLabel;
    @FXML private Label sumDateLabel;
    @FXML private Label sumStartLabel;
    @FXML private Label sumEndLabel;
    @FXML private Label sumCreatedLabel;
    @FXML private Label sumDescLabel;
    private static int appIndex;

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        setLang();
        //populate the table
        sumTitleCol.setCellValueFactory(cellData -> cellData.getValue().titleProp());
        sumDateCol.setCellValueFactory(cellData -> cellData.getValue().dateStringProp());
        sumContactCol.setCellValueFactory(cellData -> cellData.getValue().contactProp());
        refreshAddAppTable();
    }    
    
    //retrieves appointment data and populates fields
    @FXML private void sumCustDataButtonHandler(ActionEvent event) {
        Appointment app = sumTable.getSelectionModel().getSelectedItem();
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
        if (app == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorGettingInfo"));
            alert.setContentText(resource.getString("errorGettingInfoMessage"));
            alert.showAndWait();
        }
        else {
            sumTitleLabel.setText(resource.getString("Title") + ": " + app.getTitle());
            sumDescLabel.setText(resource.getString("Desc") + ": " + app.getDesc());
            sumLocationLabel.setText(resource.getString("Location") + ": " + app.getLocation());
            sumContactLabel.setText(resource.getString("Contact") + ": " + app.getContact());
            sumURLLabel.setText(resource.getString("Url") + ": " + app.getUrl());
            sumDateLabel.setText(resource.getString("Date") + ": " + app.getDateString());
            sumStartLabel.setText(resource.getString("StartTime") + ": " + app.getStartString());
            sumEndLabel.setText(resource.getString("EndTime") + ": " + app.getEndString());
            sumCreatedLabel.setText(resource.getString("CreatedBy") + ": " + app.getCreatedBy());
        }
    }

    //Opens appointment modify page
    @FXML private void sumModButtonHandler(ActionEvent event) throws IOException {
        Appointment appToMod = sumTable.getSelectionModel().getSelectedItem();
        if (appToMod == null) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorModAppt"));
            alert.setContentText(resource.getString("errorModAppSelect"));
            alert.showAndWait();
            return;
        }
        appIndex = getAppList().indexOf(appToMod);
        // Open modify appointment screen
        Parent modifyAppParent = FXMLLoader.load(getClass().getResource("ModAppointment.fxml"));
        Scene modifyAppScene = new Scene(modifyAppParent);
        Stage modifyAppStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        modifyAppStage.setScene(modifyAppScene);
        modifyAppStage.show();
        modifyAppStage.centerOnScreen();
    }

    //Delete appointment
    @FXML private void sumDelButtonHandler(ActionEvent event) {
        Appointment appToDel = sumTable.getSelectionModel().getSelectedItem();
        if (appToDel == null) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorDelApp"));
            alert.setContentText(resource.getString("errorDelAppMessage"));
            alert.showAndWait();
            return;
        }
        MyConnector.delApp(appToDel);
    }

    //returns to mainscreen
    @FXML private void sumCancelButtonHandler(ActionEvent event) {
        try {
            Parent mainScreen = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            Scene mainScene = new Scene(mainScreen);
            Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainStage.setScene(mainScene);
            mainStage.show();
            mainStage.centerOnScreen();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //refreshes the table view
    public void refreshAddAppTable() {
        refreshAppList();
        sumTable.setItems(AppList.getAppList());
    }
    
    //retrieves appointment index id
    public static int getAppIndexMod() {
        return appIndex;
    }
    
    private void setLang(){
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
        sumTitleCol.setText(resource.getString("Title"));
        sumDateCol.setText(resource.getString("Date"));
        sumContactCol.setText(resource.getString("Contact"));
        sumCustDataButton.setText(resource.getString("GetInfo"));
        sumModButton.setText(resource.getString("Modify"));
        sumDelButton.setText(resource.getString("Delete"));
        sumCancelButton.setText(resource.getString("Exit"));
        sumTitleLabel.setText(resource.getString("Title") + ":");
        sumDescLabel.setText(resource.getString("Desc") + ":");
        sumLocationLabel.setText(resource.getString("Location") + ":");
        sumContactLabel.setText(resource.getString("Contact") + ":");
        sumURLLabel.setText(resource.getString("Url") + ":");
        sumDateLabel.setText(resource.getString("Date") + ":");
        sumStartLabel.setText(resource.getString("StartTime") + ":");
        sumEndLabel.setText(resource.getString("EndTime") + ":");
        sumCreatedLabel.setText(resource.getString("CreatedBy"));
    }
}
