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
import schedulingappnew.MainApp;
import schedulingappnew.Model.Customer;
import static schedulingappnew.Model.MyConnector.addNewCust;

public class AddCustomerController implements Initializable{

    @FXML private Label addCustTitle;
    @FXML private Label addCustNameLabel;
    @FXML private Label addCustAddy1Label;
    @FXML private Label addCustAddy2Label;
    @FXML private Label addCustCityLabel;
    @FXML private Label addCustZipLabel;
    @FXML private Label addCustCountryLabel;
    @FXML private Label addCustPhoneLabel;
    @FXML private TextField addCustNameTf;
    @FXML private TextField addCustAddy1Tf;
    @FXML private TextField addCustAddy2Tf;
    @FXML private TextField addCustCityTf;
    @FXML private TextField addCustZipTf;
    @FXML private TextField addCustCountryTf;
    @FXML private TextField addCustPhoneTf;
    @FXML private Button addCustSaveButton;
    @FXML private Button addCustCancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setLang();
    }    

    
    @FXML private void addCustSaveButtonHandler(ActionEvent event) {
        //collect user input
        String name = addCustNameTf.getText();
        String addy = addCustAddy1Tf.getText();
        String addy2 = addCustAddy2Tf.getText();
        String city = addCustCityTf.getText();
        String country = addCustCountryTf.getText();
        String zipCode = addCustZipTf.getText();
        String phone = addCustPhoneTf.getText();
        // Validate fields
        String error = Customer.isValidCust(name, addy, city, country, zipCode, phone);
        // error messages
        if (error.length() > 0) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Customer", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorAddingCust"));
            alert.setContentText(error);
            alert.showAndWait();
            return;
        }
        // No errors, add customer then return to mainscreen
        try {
            addNewCust(name, addy, addy2, city, country, zipCode, phone);
                
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
    
    //cancels and returns to main screen
    @FXML private void addCustCancelButtonHandler(ActionEvent event) {
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
    
    @FXML private void setLang() {
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Customer", Locale.getDefault());
        addCustTitle.setText(resource.getString("AddCust"));
        addCustNameLabel.setText(resource.getString("CustName"));
        addCustNameTf.setPromptText(resource.getString("CustName"));
        addCustAddy1Label.setText(resource.getString("Addy"));
        addCustAddy1Tf.setPromptText(resource.getString("Addy"));
        addCustAddy2Label.setText(resource.getString("Addy2"));
        addCustAddy2Tf.setPromptText(resource.getString("Addy2"));
        addCustCityLabel.setText(resource.getString("City"));
        addCustCityTf.setPromptText(resource.getString("City"));
        addCustCountryLabel.setText(resource.getString("Country"));
        addCustCountryTf.setPromptText(resource.getString("Country"));
        addCustZipLabel.setText(resource.getString("ZipCode"));
        addCustZipTf.setPromptText(resource.getString("ZipCode"));
        addCustPhoneLabel.setText(resource.getString("Phone"));
        addCustPhoneTf.setPromptText(resource.getString("Phone"));
        addCustSaveButton.setText(resource.getString("Save"));
        addCustCancelButton.setText(resource.getString("Cancel"));    
    }
}
