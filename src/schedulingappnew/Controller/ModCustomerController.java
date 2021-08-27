package schedulingappnew.Controller;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import schedulingappnew.Model.Customer;
import static schedulingappnew.Model.CustomerList.getCustList;
import static schedulingappnew.Model.MyConnector.getAddyId;
import static schedulingappnew.Model.MyConnector.getCityId;
import static schedulingappnew.Model.MyConnector.getCountryId;
import static schedulingappnew.Model.MyConnector.modCust;
import static schedulingappnew.Model.MyConnector.setCustActive;

public class ModCustomerController implements Initializable{

    @FXML private Label modCustTitle;
    @FXML private Label modCustNameLabel;
    @FXML private Label modCustAddy1Label;
    @FXML private Label modCustAddy2Label;
    @FXML private Label modCustCityLabel;
    @FXML private Label modCustZipLabel;
    @FXML private Label modCustCountryLabel;
    @FXML private Label modCustPhoneLabel;
    @FXML private TextField modCustNameTf;
    @FXML private TextField modCustAddy1Tf;
    @FXML private TextField modCustAddy2Tf;
    @FXML private TextField modCustCityTf;
    @FXML private TextField modCustZipTf;
    @FXML private TextField modCustCountryTf;
    @FXML private TextField modCustPhoneTf;
    @FXML private Button modCustSaveButton;
    @FXML private Button modCustCancelButton;
    
    private Customer cust;
    int custIndex = MainScreenController.getCustIndexMod();
   
    @Override
    public void initialize(URL url, ResourceBundle resource) {
        setLang();
        // retrieve customer data
        cust = getCustList().get(custIndex);
        String custName = cust.getCustName();
        String addy = cust.getAddress();
        String addy2 = cust.getAddress2();
        String city = cust.getCity();
        String country = cust.getCountry();
        String zipCode = cust.getZipCode();
        String phone = cust.getPhone();
        // Populate fields
        modCustNameTf.setText(custName);
        modCustAddy1Tf.setText(addy);
        modCustAddy2Tf.setText(addy2);
        modCustCityTf.setText(city);
        modCustCountryTf.setText(country);
        modCustZipTf.setText(zipCode);
        modCustPhoneTf.setText(phone);
    }    

    @FXML private void modCustSaveButtonHandler(ActionEvent event) {
        int custId = cust.getCustId();
        String custName = modCustNameTf.getText();
        String addy = modCustAddy1Tf.getText();
        String addy2 = modCustAddy2Tf.getText();
        String city = modCustCityTf.getText();
        String country = modCustCountryTf.getText();
        String zipCode = modCustZipTf.getText();
        String phone = modCustPhoneTf.getText();

        String error = Customer.isValidCust(custName, addy, city, country, zipCode, phone);
        // Check if error contains message
        if (error.length() > 0) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Customer", Locale.getDefault());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorModCust"));
            alert.setContentText(error);
            alert.showAndWait();
            return;
        }

        int modCustCheck = modCust(custId, custName, addy, addy2, city, country, zipCode, phone);
        // Check for active status
        if (modCustCheck == 1) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Customer", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorModCust"));
            alert.setContentText(resource.getString("errorCustAlreadyExists"));
            alert.showAndWait();

        }
        // Check for active status = 0
        else if (modCustCheck == 0) {
            int countryId = getCountryId(country);
            int cityId = getCityId(city, countryId);
            int addyId = getAddyId(addy, addy2, zipCode, phone, cityId);
            //activate customer
            setCustActive(custName, addyId);
        }
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

    @FXML private void modCustCancelButtonHandler(ActionEvent event) {
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Customer", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(resource.getString("confirmCancel"));
        alert.setHeaderText(resource.getString("confirmCancel"));
        alert.setContentText(resource.getString("confirmCancelMessage"));
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
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
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Customer", Locale.getDefault());
        modCustTitle.setText(resource.getString("ModCust"));
        modCustNameLabel.setText(resource.getString("CustName"));
        modCustNameTf.setPromptText(resource.getString("CustName"));
        modCustAddy1Label.setText(resource.getString("Addy"));
        modCustAddy1Tf.setPromptText(resource.getString("Addy"));
        modCustAddy2Label.setText(resource.getString("Addy2"));
        modCustAddy2Tf.setPromptText(resource.getString("Addy2"));
        modCustCityLabel.setText(resource.getString("City"));
        modCustCityTf.setPromptText(resource.getString("City"));
        modCustCountryLabel.setText(resource.getString("Country"));
        modCustCountryTf.setPromptText(resource.getString("Country"));
        modCustZipLabel.setText(resource.getString("ZipCode"));
        modCustZipTf.setPromptText(resource.getString("ZipCode"));
        modCustPhoneLabel.setText(resource.getString("Phone"));
        modCustPhoneTf.setPromptText(resource.getString("Phone"));
        modCustSaveButton.setText(resource.getString("Save"));
        modCustCancelButton.setText(resource.getString("Cancel"));
    }
}
