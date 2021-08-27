package schedulingappnew.Controller;

import Calendar.MonthlyView;
import Calendar.WeeklyView;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Locale;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import schedulingappnew.Model.Customer;
import static schedulingappnew.Model.CustomerList.getCustList;
import static schedulingappnew.Model.MyConnector.setCustInactive;
import static schedulingappnew.Model.MyConnector.loginAppNotification;
import static schedulingappnew.Model.MyConnector.refreshAppList;
import static schedulingappnew.Model.MyConnector.refreshCustList;

public class MainScreenController implements Initializable{
    @FXML private GridPane mainGrid;
    @FXML private Button reportsButton;
    @FXML private Button mainExitButton;
    @FXML private TableView<Customer> custTable;
    @FXML private TableColumn<Customer, String> nameCol;
    @FXML private TableColumn<Customer, String> phoneCol;
    @FXML private TableColumn<Customer, String> addy1Col;
    @FXML private TableColumn<Customer, String> addy2Col;
    @FXML private TableColumn<Customer, String> cityCol;
    @FXML private TableColumn<Customer, String> countryCol; 
    @FXML private Button addCustButton;
    @FXML private Button modCustButton;
    @FXML private Button addAppButton;
    @FXML private Button appSumButton;
    @FXML private Button remCustButton;
    @FXML private Button todayButton;
    @FXML private Button weekViewButton;
    private boolean month = true;
    private MonthlyView monthView;
    private WeeklyView weekView;
    private VBox monthViewBox;
    private VBox weekViewBox;
    private static int custIndex;
    
    @Override
    public void initialize(URL url, ResourceBundle resource) {
        setLang();
        refreshAppList();
        // Create calendar to add to mainGrid
        monthView = new MonthlyView(YearMonth.now());
        monthViewBox = monthView.getView();
        mainGrid.add(monthViewBox, 0, 0);
        // Assign data to table
        nameCol.setCellValueFactory(cellData -> cellData.getValue().custNameProperty());
        addy1Col.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        addy2Col.setCellValueFactory(cellData -> cellData.getValue().address2Property());
        cityCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        countryCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        // Refresh table
        refreshCustTable();
        loginAppNotification();
    }    
    
    //Opens add customer page
    @FXML private void addCustHandler(ActionEvent event) throws SQLException {
        try {      
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MainScreen", Locale.getDefault());
            Parent addCustParent = FXMLLoader.load(getClass().getResource("AddCustomer.fxml"));
            Scene addCustScene = new Scene(addCustParent);
            Stage addCustStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            addCustStage.setTitle(resource.getString("TitleBar"));
            addCustStage.setScene(addCustScene);
            addCustStage.show();
            addCustStage.centerOnScreen();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Opens modify customer page
    @FXML private void modCustHandler(ActionEvent event) {
        // select customer from table 
        Customer custToMod = custTable.getSelectionModel().getSelectedItem();
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MainScreen", Locale.getDefault());
        // was customer selected
        if (custToMod == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorModCust"));
            alert.setContentText(resource.getString("errorModCustMessage"));
            alert.showAndWait();
            return;
        }
        custIndex = getCustList().indexOf(custToMod);
        // Open modify customer screen
        try {
            Parent modCustParent = FXMLLoader.load(getClass().getResource("ModCustomer.fxml"));
            Scene modCustScene = new Scene(modCustParent);
            Stage modCustStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modCustStage.setTitle(resource.getString("TitleBar"));
            modCustStage.setScene(modCustScene);
            modCustStage.show();
            modCustStage.centerOnScreen();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Remove customer from database
    @FXML private void remCustHandler(ActionEvent event) {
        // get selected customer
        Customer custToRem = custTable.getSelectionModel().getSelectedItem();
        // was a customer selected
        if (custToRem == null) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MainScreen", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorRemCust"));
            alert.setContentText(resource.getString("errorRemCustMessage"));
            alert.showAndWait();
            return;
        }
        // customer to be removed
        setCustInactive(custToRem);
    }
    
    //opens add appointment page
    @FXML private void addAppHandler(ActionEvent event) {
        try {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MainScreen", Locale.getDefault());
            Parent addAppParent = FXMLLoader.load(getClass().getResource("AddAppointment.fxml"));
            Scene addAppScene = new Scene(addAppParent);
            Stage addAppStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            addAppStage.setTitle(resource.getString("TitleBar"));
            addAppStage.setScene(addAppScene);
            addAppStage.show();
            addAppStage.centerOnScreen();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Opens appointment summary page
    @FXML private void appSumHandler(ActionEvent event) {
        try {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MainScreen", Locale.getDefault());
            Parent appSummParent = FXMLLoader.load(getClass().getResource("AppSummary.fxml"));
            Scene appSummScene = new Scene(appSummParent);
            Stage appSummStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appSummStage.setTitle(resource.getString("TitleBar"));
            appSummStage.setScene(appSummScene);
            appSummStage.show();
            appSummStage.centerOnScreen();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Opens the report page
    @FXML private void reportsHandler(ActionEvent event) {
        try {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MainScreen", Locale.getDefault());
            Parent reportParent = FXMLLoader.load(getClass().getResource("Report.fxml"));
            Scene reportScene = new Scene(reportParent);
            Stage reportStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            reportStage.setTitle(resource.getString("TitleBar"));
            reportStage.setScene(reportScene);
            reportStage.show();
            reportStage.centerOnScreen();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
  
    //refreshes the table view
    public void refreshCustTable(){
        refreshCustList();
        custTable.setItems(getCustList());
    }

    //opens calendar to current week/day
    @FXML private void todayButtonHandler(ActionEvent event) {
        // Checks for monthly view
        if (month) {
            mainGrid.getChildren().remove(monthViewBox);
            YearMonth currentYearMonth = YearMonth.now();
            monthView = new MonthlyView(currentYearMonth);
            monthViewBox = monthView.getView();
            mainGrid.add(monthViewBox, 0, 0);
        }
        //calendar is in weekly view
        else {
            // Remove current calendar
            mainGrid.getChildren().remove(weekViewBox);
            // Get current date
            LocalDate currentLocalDate = LocalDate.now();
            // Create a new calendar with the current date
            weekView = new WeeklyView(currentLocalDate);
            weekViewBox = weekView.getView();
            mainGrid.add(weekViewBox, 0, 0);
        }
    }

    //toggles monthly and weekly views of calendar
    @FXML private void weekViewButtonHandler(ActionEvent event) {
        // Checks for monthly view
        if (month) {
            // Remove current calendar
            mainGrid.getChildren().remove(monthViewBox);
            YearMonth currentYearMonth = monthView.getCurrentYearMonth();
            // first day of month
            LocalDate currentLocalDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(), 1);
            weekView = new WeeklyView(currentLocalDate);
            weekViewBox = weekView.getView();
            mainGrid.add(weekViewBox, 0, 0);
            weekViewButton.setText(ResourceBundle.getBundle("schedulingappnew.Resources/MainScreen", Locale.getDefault()).getString("monthViewButton"));
            month = false;
        }
        // If calendar is currently in weekly view
        else {
            // Remove current calendar
           mainGrid.getChildren().remove(weekViewBox);
            // Get calendar's current date
            LocalDate currentLocalDate = weekView.getCurrentLocalDate();
            // Convert date to year-month
            YearMonth currentYearMonth = YearMonth.from(currentLocalDate);
            // Create and set new calendar with year-month
            monthView = new MonthlyView(currentYearMonth);
            monthViewBox = monthView.getView();
            mainGrid.add(monthViewBox, 0, 0);
            weekViewButton.setText(ResourceBundle.getBundle("schedulingappnew.Resources/MainScreen", Locale.getDefault()).getString("weekViewButton"));

            month = true;
        }
    }
    
    public static int getCustIndexMod() {
        return custIndex;
    }
    
    private void setLang() {
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MainScreen", Locale.getDefault());
        addCustButton.setText(resource.getString("addCustButton"));
        modCustButton.setText(resource.getString("modCustButton"));
        remCustButton.setText(resource.getString("remCustButton"));
        addAppButton.setText(resource.getString("addAppButton"));
        appSumButton.setText(resource.getString("appSumButton"));
        todayButton.setText(resource.getString("todayButton"));    
        weekViewButton.setText(resource.getString("weekViewButton"));
        reportsButton.setText(resource.getString("reportsButton"));
        mainExitButton.setText(resource.getString("mainExitButton"));
        nameCol.setText(resource.getString("nameCol"));
        phoneCol.setText(resource.getString("phoneCol"));
        addy1Col.setText(resource.getString("addy1Col"));    
        addy2Col.setText(resource.getString("addy2Col"));    
        cityCol.setText(resource.getString("cityCol"));        
        countryCol.setText(resource.getString("countryCol"));    
    }

    //exits application
    @FXML private void exitAppHandler(ActionEvent event) {
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MainScreen", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(resource.getString("confirmExit"));
        alert.setHeaderText(resource.getString("confirmExit"));
        alert.setContentText(resource.getString("confirmExitMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

}
