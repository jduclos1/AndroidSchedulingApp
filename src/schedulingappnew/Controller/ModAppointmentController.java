package schedulingappnew.Controller;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static schedulingappnew.Model.AppList.getAppList;
import schedulingappnew.Model.Appointment;
import schedulingappnew.Model.Customer;
import schedulingappnew.Model.CustomerList;
import static schedulingappnew.Model.CustomerList.getCustList;
import static schedulingappnew.Model.MyConnector.addNewApp;
import static schedulingappnew.Model.MyConnector.modApp;

public class ModAppointmentController implements Initializable{

    @FXML private Label modAppScreenTitleLabel;
    @FXML private Label modAppTitleLabel;
    @FXML private Label modAppLocationLabel;
    @FXML private Label modAppDescLabel;
    @FXML private Label modAppURLLabel;
    @FXML private Label modAppDateLabel;
    @FXML private Label modAppStartLabel;
    @FXML private Label modAppEndLabel;
    @FXML private TextField modAppTitleTf;
    @FXML private TextField modAppLocationTf;
    @FXML private TextField modAppDescTf;
    @FXML private TextField modAppURLTf;
    @FXML private TextField modAppStartHourTF;
    @FXML private TextField modAppStartMinTF;
    @FXML private TextField modAppEndHourTF;
    @FXML private TextField modAppEndMinTF;
    @FXML private DatePicker modAppDateTF;
    @FXML private ChoiceBox<String> modAppEndAmPmMenu;
    @FXML private ChoiceBox<String> modAppStartAmPmMenu;
    @FXML private TableView<Customer> addModAppTableView;
    @FXML private TableView<Customer> delModAppTableView;
    @FXML private Button saveAppModButton;
    @FXML private Button cancelAppModButton;
    @FXML private Button addAppModButton;
    @FXML private Button delAppModButton;
    @FXML private TextField modAppContactTf;
    @FXML private Label modAppContactLabel;
    @FXML private TableColumn<Customer, String> addModAppNameCol;
    @FXML private TableColumn<Customer, String> addModAppPhoneCol;
    @FXML private TableColumn<Customer, String> addModAppCityCol;
    @FXML private TableColumn<Customer, String> addModAppCountryCol;
    @FXML private TableColumn<Customer, String> delModAppNameCol;
    @FXML private TableColumn<Customer, String> delModAppPhoneCol;
    @FXML private TableColumn<Customer, String> delModAppCityCol;
    @FXML private TableColumn<Customer, String> delModAppCountryCol;
    private ObservableList<Customer> currentCustomers = FXCollections.observableArrayList();
    private Appointment app;
    int appIndex = AppSummaryController.getAppIndexMod();
    @FXML private String AM;
    @FXML private String PM;
    
    @Override
    public void initialize(URL url, ResourceBundle resource) {
        setLang();
        // Get appointment to be modified
        app = getAppList().get(appIndex);
        // Get appoinment data
        String title = app.getTitle();
        String desc = app.getDesc();
        String location = app.getLocation();
        String contact = app.getContact();
        String URL = app.getUrl();
        Date appDate = app.getStartDate();
  
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(appDate);
        int appYear = cal.get(Calendar.YEAR);
        int appMonth = cal.get(Calendar.MONTH) + 1;
        int appDay = cal.get(Calendar.DAY_OF_MONTH);
        LocalDate appLocalDate = LocalDate.of(appYear, appMonth, appDay);
        // Split strings into hour, minute and AM/PM 
        String startString = app.getStartString();
        String startHour = startString.substring(0,2);
        if (Integer.parseInt(startHour) < 10) {
            startHour = startHour.substring(1,2);
        }
        String startMin = startString.substring(3,5);
        String startAmPm = startString.substring(6,8);
        String endString = app.getEndString();
        String endHour = endString.substring(0,2);
        if (Integer.parseInt(endHour) < 10) {
            endHour = endHour.substring(1,2);
        }
        String endMin = endString.substring(3,5);
        String endAmPm = endString.substring(6,8);
        int custId = app.getCustId();
        ObservableList<Customer> custList = getCustList();
        for (Customer cust : custList) {
            if (cust.getCustId() == custId) {
                currentCustomers.add(cust);
            }
        }
        // Populate fields w/ appointment data
        modAppTitleTf.setText(title);
        modAppDescTf.setText(desc);
        modAppLocationTf.setText(location);
        modAppContactTf.setText(contact);
        modAppURLTf.setText(URL);
        modAppDateTF.setValue(appLocalDate);
        modAppStartHourTF.setText(startHour);
        modAppStartMinTF.setText(startMin);
        modAppStartAmPmMenu.setValue(startAmPm);
        modAppEndHourTF.setText(endHour);
        modAppEndMinTF.setText(endMin);
        modAppEndAmPmMenu.setValue(endAmPm);
        //data for tables
        addModAppNameCol.setCellValueFactory(cellData -> cellData.getValue().custNameProperty());
        addModAppCityCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        addModAppCountryCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        addModAppPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        delModAppNameCol.setCellValueFactory(cellData -> cellData.getValue().custNameProperty());
        delModAppCityCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        delModAppCountryCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        delModAppPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        // Refresh tables
        refreshModAppAddTable();
        refreshModAppDelTable();
    }    

    @FXML private void saveAppModButtonHandler(ActionEvent event) {
        Customer cust = null;
        if (currentCustomers.size() == 1) {
            cust = currentCustomers.get(0);
        }
        int appId = app.getAppId();
        String title = modAppTitleTf.getText();
        String desc = modAppDescTf.getText();
        String location = modAppLocationTf.getText();
        String contact = modAppContactTf.getText();

        if (contact.length() == 0 && cust != null) {
            contact = cust.getCustName() + ", " + cust.getPhone();
        }
        String url = modAppURLTf.getText();
        LocalDate appDate = modAppDateTF.getValue();
        String startHour = modAppStartHourTF.getText();
        String startMin = modAppStartMinTF.getText();
        String startAmPm = modAppStartAmPmMenu.getSelectionModel().getSelectedItem();
        String endHour = modAppEndHourTF.getText();
        String endMin = modAppEndMinTF.getText();
        String endAmPm = modAppEndAmPmMenu.getSelectionModel().getSelectedItem();
        // Submit appointment data for validation
        String error = Appointment.isAppValid(cust, title, desc, location,
                appDate, startHour, startMin, startAmPm, endHour, endMin, endAmPm);
        
        if (error.length() > 0) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorModApp"));
            alert.setContentText(error);
            alert.showAndWait();
            return;
        }
        SimpleDateFormat localDate = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        localDate.setTimeZone(TimeZone.getDefault());
        Date startLocal = null;
        Date endLocal = null;

        try {
            startLocal = localDate.parse(appDate.toString() + " " + startHour + ":" + startMin + " " + startAmPm);
            endLocal = localDate.parse(appDate.toString() + " " + endHour + ":" + endMin + " " + endAmPm);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        ZonedDateTime startTime = ZonedDateTime.ofInstant(startLocal.toInstant(), ZoneId.of("UTC"));
        ZonedDateTime endTime = ZonedDateTime.ofInstant(endLocal.toInstant(), ZoneId.of("UTC"));
        if (modApp(appId, cust, title, desc, location, contact, url, startTime, endTime)) {
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

    @FXML private void cancelAppModButtonHandler(ActionEvent event) {
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(resource.getString("confirmCancel"));
        alert.setHeaderText(resource.getString("confirmCancel"));
        alert.setContentText(resource.getString("confirmCancelMessage"));
        Optional<ButtonType> result = alert.showAndWait();

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

    @FXML private void addAppModButtonHandler(ActionEvent event) {
        Customer cust = addModAppTableView.getSelectionModel().getSelectedItem();
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
        if (cust == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorAddingCust"));
            alert.setContentText(resource.getString("errorAddingCustSelectOne"));
            alert.showAndWait();
            return;
        }
        if (currentCustomers.size() > 0) {
             Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorAddingCust"));
            alert.setContentText(resource.getString("errorAddingCustOne"));
            alert.showAndWait();
            return;
        }
        currentCustomers.add(cust);
        refreshModAppAddTable();
    }

    @FXML private void delAppModButtonHandler(ActionEvent event) {
        Customer cust = delModAppTableView.getSelectionModel().getSelectedItem();
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
            refreshModAppDelTable();
        }

    }

    private void refreshModAppAddTable() {
        addModAppTableView.setItems(CustomerList.getCustList());
    }

    private void refreshModAppDelTable() {
        delModAppTableView.setItems(currentCustomers);
    }
    
    private void setLang(){
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
        modAppScreenTitleLabel.setText(resource.getString("ModApp"));
        modAppTitleLabel.setText(resource.getString("Title"));
        modAppTitleTf.setPromptText(resource.getString("Title"));
        modAppDescLabel.setText(resource.getString("Desc"));
        modAppDescTf.setPromptText(resource.getString("Desc"));
        modAppLocationLabel.setText(resource.getString("Location"));
        modAppLocationTf.setPromptText(resource.getString("Location"));
        modAppContactLabel.setText(resource.getString("Contact"));
        modAppContactTf.setPromptText(resource.getString("Contact"));
        modAppURLLabel.setText(resource.getString("Url"));
        modAppURLTf.setPromptText(resource.getString("Url"));
        modAppDateLabel.setText(resource.getString("Date"));
        modAppStartLabel.setText(resource.getString("StartTime"));
        modAppEndLabel.setText(resource.getString("EndTime"));
        addModAppNameCol.setText(resource.getString("NameCol"));
        addModAppCityCol.setText(resource.getString("CityCol"));
        addModAppCountryCol.setText(resource.getString("CountryCol"));
        addModAppPhoneCol.setText(resource.getString("PhoneCol"));
        delModAppNameCol.setText(resource.getString("NameCol"));
        delModAppCityCol.setText(resource.getString("CityCol"));
        delModAppCountryCol.setText(resource.getString("CountryCol"));
        delModAppPhoneCol.setText(resource.getString("PhoneCol"));
        addAppModButton.setText(resource.getString("Add"));
        delAppModButton.setText(resource.getString("Delete"));
        saveAppModButton.setText(resource.getString("Save"));
        cancelAppModButton.setText(resource.getString("Cancel"));
    }
}