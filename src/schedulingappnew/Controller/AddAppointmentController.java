package schedulingappnew.Controller;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import schedulingappnew.Model.Appointment;
import schedulingappnew.Model.Customer;
import schedulingappnew.Model.CustomerList;
import static schedulingappnew.Model.MyConnector.addNewApp;

public class AddAppointmentController implements Initializable{

    @FXML private Label addAppScreenTitleLabel;
    @FXML private Label addAppTitleLabel;
    @FXML private Label addAppDescrLabel;
    @FXML private Label addAppLocationLabel;
    @FXML private Label addAppContactLabel;
    @FXML private Label addAppURLLabel;
    @FXML private Label addAppDateLabel;
    @FXML private Label addAppStartLabel;
    @FXML private Label addAppEndLabel;
    @FXML private TextField addAppTitleTf;
    @FXML private TextField addAppLocationTf;
    @FXML private TextField addAppContactTf;
    @FXML private TextField addAppURLTf;
    @FXML private TextField addAppDescrTf;
    @FXML private DatePicker addAppDateTf;
    @FXML private TextField addAppStartHourTF;
    @FXML private TextField addAppStartMinTF;
    @FXML private TextField addAppEndHourTF;
    @FXML private TextField addAppEndMinTF;
    @FXML private ChoiceBox<String> addAppStartAmPmMenu;
    @FXML private ChoiceBox<String> addAppEndAmPmMenu;
    @FXML private String AM;
    @FXML private String PM;
    @FXML private TableView<Customer> addAppTableView;
    @FXML private TableColumn<Customer, String> addAppNameCol;
    @FXML private TableColumn<Customer, String> addAppPhoneCol;
    @FXML private TableColumn<Customer, String> addAppCityCol;
    @FXML private TableColumn<Customer, String> addAppCountryCol;
    @FXML private TableView<Customer> delAppTableView;
    @FXML private TableColumn<Customer, String> delAppNameCol;
    @FXML private TableColumn<Customer, String> delAppPhoneCol;
    @FXML private TableColumn<Customer, String> delAppCityCol;
    @FXML private TableColumn<Customer, String> delAppCountryCol;
    @FXML private Button addAppButton;
    @FXML private Button delAppButton;
    @FXML private Button saveAppButton;
    @FXML private Button cancelAppButton;
    //create a list of customers for tables
    private ObservableList<Customer> currentCustomers = FXCollections.observableArrayList();  
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {      
        setLang();
        System.out.println("set language");
        addAppNameCol.setCellValueFactory(cellData -> cellData.getValue().custNameProperty());
        addAppCityCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        addAppCountryCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        addAppPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        delAppNameCol.setCellValueFactory(cellData -> cellData.getValue().custNameProperty());
        delAppCityCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        delAppCountryCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        delAppPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        
        updateAddCustAddTableView();
        updateAddCustDelTableView();
        System.out.println("updated");
    }    

    @FXML private void addAppButtonHandler(ActionEvent event) {
        Customer cust = addAppTableView.getSelectionModel().getSelectedItem();
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
        //check for empty customer
        if (cust == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorAddingCust"));
            alert.setContentText(resource.getString("errorAddingCustSelectOne"));
            alert.showAndWait();
            return;
        }
        //check for existing customers
        if (currentCustomers.size() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorAddingCust"));
            alert.setContentText(resource.getString("errorAddingCustOne"));
            alert.showAndWait();
            return;
        }
        currentCustomers.add(cust);
        updateAddCustDelTableView();
    }

    @FXML private void delAppButtonHandler(ActionEvent event) {
        Customer cust = delAppTableView.getSelectionModel().getSelectedItem();
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
        if (cust == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorRemCust"));
            alert.setContentText(resource.getString("errorRemCustMessage"));
            alert.showAndWait();
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(resource.getString("confirmRemove"));
        alert.setHeaderText(resource.getString("confirmRemoveCust"));
        alert.setContentText(resource.getString("confirmRemoveCustMessage"));
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            currentCustomers.remove(cust);
            // Update table - customer appointment removed
            updateAddCustDelTableView();
        }
    }

    @FXML private void saveAppButtonHandler(ActionEvent event) {
        Customer cust = null;
        
        if (currentCustomers.size() == 1) {
            cust = currentCustomers.get(0);
        }
        // Accept all inputs
        String title = addAppTitleTf.getText();
        String desc = addAppDescrTf.getText();
        String location = addAppLocationTf.getText();
        String contact = addAppContactTf.getText();
        if (contact.length() == 0 && cust != null) {
            contact = cust.getCustName() + ", " + cust.getPhone();
        }
        String url = addAppURLTf.getText();
        LocalDate appDate = addAppDateTf.getValue();
        String startHour = addAppStartHourTF.getText();
        String startMin = addAppStartMinTF.getText();
        String startAmPm = addAppStartAmPmMenu.getSelectionModel().getSelectedItem();
        String endHour = addAppEndHourTF.getText();
        String endMin = addAppEndMinTF.getText();
        String endAmPm = addAppEndAmPmMenu.getSelectionModel().getSelectedItem();
        
        String error = Appointment.isAppValid(cust, title, desc, location, appDate, 
                startHour, startMin, startAmPm, endHour, endMin, endAmPm);
        
        //change all error tags
        if (error.length() > 0) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
            // Show alert with errorMessage
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorAddingApp"));
            alert.setContentText(error);
            alert.showAndWait();
            return;
        }

        //Date formatter
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        format.setTimeZone(TimeZone.getDefault());
        Date startLocal = null;
        Date endLocal = null;
        
        try {
            startLocal = format.parse(appDate.toString() + " " + startHour + ":" + startMin + " " + startAmPm);
            endLocal = format.parse(appDate.toString() + " " + endHour + ":" + endMin + " " + endAmPm);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        // Create ZonedDateTime out of Date objects
        ZonedDateTime startFormat = ZonedDateTime.ofInstant(startLocal.toInstant(), ZoneId.of("UTC"));
        ZonedDateTime endFormat = ZonedDateTime.ofInstant(endLocal.toInstant(), ZoneId.of("UTC"));
        // Submit information to be added to database. Check if 'true' is returned
        if (addNewApp(cust, title, desc, location, contact, url, startFormat, endFormat)) {
            try {
                // Return to main screen
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
    }

    @FXML private void cancelAppButtonHandler(ActionEvent event) {
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());  
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(resource.getString("confirmCancel"));
        alert.setHeaderText(resource.getString("confirmCancel"));
        alert.setContentText(resource.getString("confirmCancelMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // OK = main screen
        if(result.get() == ButtonType.OK) {
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
    }
    
    private void setLang(){
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
        addAppScreenTitleLabel.setText(resource.getString("AddApp"));
        addAppTitleLabel.setText(resource.getString("Title"));
        addAppTitleTf.setPromptText(resource.getString("Title"));
        addAppDescrLabel.setText(resource.getString("Desc"));
        addAppDescrTf.setPromptText(resource.getString("Desc"));
        addAppLocationLabel.setText(resource.getString("Location"));
        addAppLocationTf.setPromptText(resource.getString("Location"));
        addAppContactLabel.setText(resource.getString("Contact"));
        addAppContactTf.setPromptText(resource.getString("Contact"));
        addAppURLLabel.setText(resource.getString("Url"));
        addAppURLTf.setPromptText(resource.getString("Url"));
        addAppDateLabel.setText(resource.getString("Date"));
        addAppStartLabel.setText(resource.getString("StartTime"));
        addAppEndLabel.setText(resource.getString("EndTime"));
        addAppNameCol.setText(resource.getString("NameCol"));
        addAppCityCol.setText(resource.getString("CityCol"));
        addAppCountryCol.setText(resource.getString("CountryCol"));
        addAppPhoneCol.setText(resource.getString("PhoneCol"));
        delAppNameCol.setText(resource.getString("NameCol"));
        delAppCityCol.setText(resource.getString("CityCol"));
        delAppCountryCol.setText(resource.getString("CountryCol"));
        delAppPhoneCol.setText(resource.getString("PhoneCol"));
        addAppButton.setText(resource.getString("Add"));
        delAppButton.setText(resource.getString("Delete"));
        saveAppButton.setText(resource.getString("Save"));
        cancelAppButton.setText(resource.getString("Cancel"));
        
    }
    
    private void updateAddCustAddTableView() {
        addAppTableView.setItems(CustomerList.getCustList());
    }
    
    private void updateAddCustDelTableView() {
        delAppTableView.setItems(currentCustomers);
    }
}
