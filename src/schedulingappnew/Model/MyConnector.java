package schedulingappnew.Model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import schedulingappnew.Controller.AppSummaryController;
import schedulingappnew.Controller.LoginScreenController;

public class MyConnector {  
    private static final String driver = "com.mysql.jdbc.Driver";  
    private static final String user = "U03rKe";
    private static final String pass = "53688063891";
    private static final String url = "jdbc:mysql://52.206.157.109/U03rKe";
    private static final String db = "U03rKe";
    private static String currentUser;
    private static int openCount = 0;
    
    public static boolean validateCreds(String username, String password) throws SQLException {
        int userId = getUserId(username);
        boolean validPass = validatePass(userId, password);
        if (validPass){
            setCurrentUser(username);
            try {
                Path p = Paths.get("UsersLogFile.txt");
                Files.write(p, Arrays.asList("User " + currentUser + " logged in @ " + Date.from(Instant.now()).toString() + "."), 
                        StandardCharsets.UTF_8, Files.exists(p) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return true;
        }
        else {
            return false;
        }
    }

    private static boolean validatePass(int userId, String password) {
        try (Connection conn = DriverManager.getConnection(url, user, pass);
                Statement state = conn.createStatement()){
            
            //query password
            ResultSet passSet = state.executeQuery("SELECT password FROM user WHERE userID = " + userId);
            String dbPass = null;
            if (passSet.next()){
                dbPass = passSet.getString("password");
            }
            else {
                return false;
            }
            passSet.close();
            
            if (dbPass.equals(password)){
                return true;
            }
            else {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;           
        }
    }
    
    private static void setCurrentUser(String username) {
        currentUser = username;
    }
    
    //if no matching user is found; return -1
    private static int getUserId(String username) {
        try {
            Class.forName(driver);
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        int userId = -1;
        
        try (Connection conn = DriverManager.getConnection(url, user, pass); 
            Statement state = conn.createStatement()){
            
            //queries user/pw
            ResultSet userIds = state.executeQuery("SELECT userId FROM user WHERE userName = '" + username + "'");
            
            //sets userId
            if (userIds.next()){
                userId = userIds.getInt("userId");
            }
            userIds.close();
        }catch (SQLException e) {
           LoginScreenController.dbErrorCount();
        }
        return userId;
    }
     
    public static boolean addNewApp(Customer cust, String title, String desc, String location, String contact, String url, 
            ZonedDateTime start, ZonedDateTime end){
        //Create timestamps
        String startString = start.toString();
        startString = startString.substring(0, 10) + " " + startString.substring(11, 16) + ":00";
        Timestamp startTS = Timestamp.valueOf(startString);
        String endString = end.toString();
        endString = endString.substring(0, 10) + " " + endString.substring(11, 16) + ":00";
        Timestamp endTS = Timestamp.valueOf(endString);
    
        //check for overlapping appointments
        if (checkAppOverlap(startTS, endTS)){
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorAddingApp"));
            alert.setContentText(resource.getString("errorAppOverlaps"));
            alert.showAndWait();
            return false;
        }
        else {
            int custId = cust.getCustId();
            addApp(custId, title, desc, location, contact, url, startTS, endTS);
            return true;
        }
    }

    private static void addApp(int custId, String title, String desc, String location, String contact, String url, 
            Timestamp startTimestamp, Timestamp endTimestamp) {
        
        try (Connection conn = DriverManager.getConnection(MyConnector.url,user,pass);
            Statement state = conn.createStatement()) {
                ResultSet allAppIds = state.executeQuery("SELECT appointmentId FROM appointment ORDER BY appointmentId");
                int appId;
           
                // increment appId
            if (allAppIds.last()) {
                appId = allAppIds.getInt(1) + 1;
                allAppIds.close();
            }
            else {
                allAppIds.close();
                appId = 1;
            }
            // Create new entry with appointmentId value
            state.executeUpdate("INSERT INTO appointment VALUES (" + appId +", " + custId + ", '" + title + "', '" +
                    desc + "', '" + location + "', '" + contact + "', '" + url + "', '" + startTimestamp + "', '" + endTimestamp + "', " +
                    "CURRENT_DATE, '" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
        }
          
        catch (SQLException e) {
            // Create alert notifying user that a database connection is needed for this function
            ResourceBundle rb = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingApp"));
            alert.setContentText(rb.getString("errorReqDbase"));
            alert.showAndWait();
        }
    }
    
    private static boolean checkAppOverlap(Timestamp startTS, Timestamp endTS) {
        refreshAppList();
        
        ObservableList<Appointment> appList = AppList.getAppList();
        
        for (Appointment app: appList) {
            Timestamp existingStartTS = app.getStartTimestamp();
            Timestamp existingEndTS = app.getEndTimestamp();
            
            if (endTS.after(existingStartTS) && startTS.before(existingEndTS)) {
                return true;
            }
            if (startTS.equals(existingStartTS)) {
                return true;
            }
            if (startTS.after(existingStartTS) && endTS.before(existingEndTS)) {
                return true;
            }            
            if (startTS.after(existingStartTS) && startTS.before(existingEndTS)) {
                return true;
            }         
            if (startTS.before(existingStartTS) && endTS.after(existingEndTS)) {
                return true;
            }
            if (endTS.equals(existingEndTS)) {
                return true;
            }
        }
        return false;
    }

    public static void refreshAppList() {
        try (Connection conn = DriverManager.getConnection(url, user, pass);
            Statement state = conn.createStatement()) {
            // Retrieve appointmentList and clear
            ObservableList<Appointment> appList = AppList.getAppList();
            appList.clear();
            
            // Create a list of upcoming appointments
            ResultSet appResults = state.executeQuery("SELECT appointmentId FROM appointment WHERE start >= CURRENT_TIMESTAMP");
            ArrayList<Integer> appIdList = new ArrayList<>();
            while(appResults.next()) {
                appIdList.add(appResults.getInt(1));
            }
            
            for (int appId : appIdList) {
                // Retrieve appointment info from database
                appResults = state.executeQuery("SELECT customerId, title, description, location, contact, url, start, end, createdBy FROM appointment WHERE appointmentId = " + appId);
                appResults.next();
                int custId = appResults.getInt(1);
                String title = appResults.getString(2);
                String desc = appResults.getString(3);
                String location = appResults.getString(4);
                String contact = appResults.getString(5);
                String url = appResults.getString(6);
                Timestamp startTS = appResults.getTimestamp(7);
                Timestamp endTS = appResults.getTimestamp(8);
                String createdBy = appResults.getString(9);
                
                // Convert startTimestamp & endTimestamp
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                java.util.Date startDate = format.parse(startTS.toString());
                java.util.Date endDate = format.parse(endTS.toString());
                
                Appointment app = new Appointment(appId, custId, title, desc, location, contact, url, startTS, 
                        endTS, startDate, endDate, createdBy);
                //add appointment
                appList.add(app);
            }
        }

        catch (Exception e) {
            // Create alert notifying user that a database connection is needed for this function
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorAddingApp"));
            alert.setContentText(resource.getString("errorReqDbase"));
            alert.showAndWait();
        }
    }
    
    public static void addNewCust(String custName, String addy, String addy2,
                                   String city, String country, String zipCode, String phone) {
        
        try {
            int countryId = getCountryId(country);
            int cityId = getCityId(city, countryId);
            int addyId = getAddyId(addy, addy2, zipCode, phone, cityId);
            
            // Check for customer info
            if (checkForCust(custName, addyId)) {
                // Try-with-resources block for database connection
                try (Connection conn = DriverManager.getConnection(url, user, pass);
                     Statement state = conn.createStatement()) {
                    ResultSet results = state.executeQuery("SELECT active FROM customer WHERE " +
                            "customerName = '" + custName + "' AND addressId = " + addyId);
                    results.next();
                    int active = results.getInt(1);
                    // Check customer status - active/inactive
                    if (active == 1) {
                        // Alert for active customer
                        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(resource.getString("error"));
                        alert.setHeaderText(resource.getString("errorAddingCust"));
                        alert.setContentText(resource.getString("errorCustExists"));
                        alert.showAndWait();
                    } else if (active == 0) {
                        // Set customer to active if they are currently inactive
                        setCustActive(custName, addyId);
                    }
                }
            }
            // Add new customer entry if customer does not already exist
            else {
                addCust(custName, addyId);
            }
        }
        catch (SQLException e) {
            // Create alert notifying user that a database connection is needed for this function
            ResourceBundle rb = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingCust"));
            alert.setContentText(rb.getString("errorReqDbase"));
            alert.showAndWait();
        }
    }

    public static int getCountryId(String country) {
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement state = conn.createStatement()) {
            ResultSet getCountryIdSet = state.executeQuery("SELECT countryId FROM country WHERE country = '" + country + "'");
            // Check for existing country code
            if (getCountryIdSet.next()) {
                int countryId = getCountryIdSet.getInt(1);
                getCountryIdSet.close();
                return countryId;
            }
            else {
                getCountryIdSet.close();
                int countryId;
                ResultSet getCountryIdSet2 = state.executeQuery("SELECT countryId FROM country ORDER BY countryId");
                //increment id
                if (getCountryIdSet2.last()) {
                    countryId = getCountryIdSet2.getInt(1) + 1;
                    getCountryIdSet2.close();
                }
                // set id to 1
                else {
                    getCountryIdSet2.close();
                    countryId = 1;
                }
                state.executeUpdate("INSERT INTO country VALUES (" + countryId + ", '" + country + "', CURRENT_DATE, " +
                        "'" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
                return countryId;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getCityId(String city, int countryId) {
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement state = conn.createStatement()) {
            ResultSet getCityId = state.executeQuery("SELECT cityId FROM city WHERE city = '" + city + "' AND countryid = " + countryId);
            // Check for existing city code
            if (getCityId.next()) {
                int cityId = getCityId.getInt(1);
                getCityId.close();
                return cityId;
            }
            else {
                getCityId.close();
                int cityId;
                ResultSet getCityId2 = state.executeQuery("SELECT cityId FROM city ORDER BY cityId");
                //increment id
                if (getCityId2.last()) {
                    cityId = getCityId2.getInt(1) + 1;
                    getCityId2.close();
                }
                // set id to 1
                else {
                    getCityId2.close();
                    cityId = 1;
                }
                state.executeUpdate("INSERT INTO city VALUES (" + cityId + ", '" + city + "', " + countryId + ", CURRENT_DATE, " +
                        "'" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
                return cityId;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }      
    }

    public static int getAddyId(String addy, String addy2, String zipCode, String phone, int cityId) {
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement state = conn.createStatement()) {
            ResultSet getAddyId = state.executeQuery("SELECT addressId FROM address WHERE address = '" + addy + "' AND " +
                    "address2 = '" + addy2 + "' AND postalCode = '" + zipCode + "' AND phone = '" + phone + "' AND cityId = " + cityId);
            // Check for existing addy code
            if (getAddyId.next()) {
                int addressId = getAddyId.getInt(1);
                getAddyId.close();
                return addressId;
            }
            else {
                getAddyId.close();
                int addyId;
                ResultSet getAddyId2 = state.executeQuery("SELECT addressId FROM address ORDER BY addressId");
                //increment id
                if (getAddyId2.last()) {
                    addyId = getAddyId2.getInt(1) + 1;
                    getAddyId2.close();
                }
                // set id to 1
                else {
                    getAddyId2.close();
                    addyId = 1;
                }
                // Create new entry with new addressId value
                state.executeUpdate("INSERT INTO address VALUES (" + addyId + ", '" + addy + "', '" +addy2 + "', " + cityId + ", " +
                        "'" + zipCode + "', '" + phone + "', CURRENT_DATE, '" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
                return addyId;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static boolean checkForCust(String custName, int addyId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement state = conn.createStatement()) {
            ResultSet getCustId = state.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + custName + "' " +
                    "AND addressId = " + addyId);
            // Check for cust in dbase
            if (getCustId.next()) {
                getCustId.close();
                return true;
            }
            else {
                getCustId.close();
                return false;
            }
        }
    }

    public static void setCustActive(String custName, int addyId) {
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement state = conn.createStatement()) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorModCust"));
            alert.setContentText(resource.getString("errorSetToActive"));
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.get() == ButtonType.OK) {
                state.executeUpdate("UPDATE customer SET active = 1, lastUpdate = CURRENT_TIMESTAMP, " +
                        "lastUpdateBy = '" + currentUser + "' WHERE customerName = '" + custName + "' AND addressId = " + addyId);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void setCustInactive(Customer custToRem) {
        int custId = custToRem.getCustId();
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(resource.getString("confirmRem"));
        alert.setHeaderText(resource.getString("confirmRemCust"));
        alert.setContentText(resource.getString("confirmRemCustMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try (Connection conn = DriverManager.getConnection(url,user,pass);
                 Statement state = conn.createStatement()) {
                state.executeUpdate("UPDATE customer SET active = 0 WHERE customerId = " + custId);
            }
            catch (SQLException e) {
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle(resource.getString("error"));
                alert2.setHeaderText(resource.getString("errorModCust"));
                alert2.setContentText(resource.getString("errorReqDbase"));
                alert2.showAndWait();
            }
            refreshCustList();
        }
    }

    private static void addCust(String custName, int addyId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement state = conn.createStatement()) {
            ResultSet custIds = state.executeQuery("SELECT customerId FROM customer ORDER BY customerId");
            int custId;
            // increment custId
            if (custIds.last()) {
                custId = custIds.getInt(1) + 1;
                custIds.close();
            }
            else {
                custIds.close();
                custId = 1;
            }
            state.executeUpdate("INSERT INTO customer VALUES (" + custId + ", '" + custName + "', " + addyId + ", 1, " +
                    "CURRENT_DATE, '" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
        }
    }
    
    public static int modCust(int custId, String custName, String addy, String addy2,
                                      String city, String country, String zip, String phone) {
        try {
            int countryId = getCountryId(country);
            int cityId = getCityId(city, countryId);
            int addyId = getAddyId(addy, addy2, zip, phone, cityId);
            if (checkForCust(custName, addyId)) {
                int existingCustId = getCustId(custName, addyId);
                int activeStatus = getActiveStatus(existingCustId);
                return activeStatus;
            } else {
                updateCust(custId, custName, addyId);
                cleanDb();
                return -1;
            }
        }
        catch (SQLException e) {
            // Create alert notifying user that a database connection is needed for this function
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorModCust"));
            alert.setContentText(resource.getString("errorReqDbase"));
            alert.showAndWait();
            return -1;
        }
    }
    
    public static boolean modApp(int appId, Customer cust, String title, String desc, String location,
                                         String contact, String url, ZonedDateTime startTime, ZonedDateTime endTime) {
        try {
            String startTimeString = startTime.toString();
            startTimeString = startTimeString.substring(0, 10) + " " + startTimeString.substring(11, 16) + ":00";
            Timestamp startTS = Timestamp.valueOf(startTimeString);
            String endTimeString = endTime.toString();
            endTimeString = endTimeString.substring(0, 10) + " " + endTimeString.substring(11, 16) + ":00";
            Timestamp endTS = Timestamp.valueOf(endTimeString);
            if (checkAppOverlap2(startTS, endTS)) {
                ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(resource.getString("error"));
                alert.setHeaderText(resource.getString("errorModApp"));
                alert.setContentText(resource.getString("errorAppOverlaps"));
                alert.showAndWait();
                return false;
            } else {
                int custId = cust.getCustId();
                updateApp(appId, custId, title, desc, location, contact, url, startTS, endTS);
                return true;
            }
        }
        catch (Exception e) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorAddingApp"));
            alert.setContentText(resource.getString("errorReqDbase"));
            alert.showAndWait();
            return false;
        }
    }

    private static int getCustId(String custName, int addyId) throws SQLException {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement state = conn.createStatement()) {
            ResultSet results = state.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + custName + "' AND addressId = " + addyId);
            results.next();
            int custId = results.getInt(1);
            return custId;
        }
    }

    private static int getActiveStatus(int custId) throws SQLException {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement state = conn.createStatement()) {
            ResultSet results = state.executeQuery("SELECT active FROM customer WHERE customerId = " + custId);
            results.next();
            int active = results.getInt(1);
            return active;
        }
    }

    private static void cleanDb() {
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement state = conn.createStatement()) {
            // List all addyId's 
            ResultSet addyIdResults = state.executeQuery("SELECT DISTINCT addressId FROM customer ORDER BY addressId");
            ArrayList<Integer> addyIdListFromCust = new ArrayList<>();
            while (addyIdResults.next()) {
                addyIdListFromCust.add(addyIdResults.getInt(1));
            }
            // Create list of addressId's used in Address table
            addyIdResults = state.executeQuery("SELECT DISTINCT addressId FROM address ORDER BY addressId");
            ArrayList<Integer> addyIdListFromAddy = new ArrayList<>();
            while (addyIdResults.next()) {
                addyIdListFromAddy.add(addyIdResults.getInt(1));
            }
            // List unused addys
            for (int i = 0; i < addyIdListFromCust.size(); i++) {
                for (int j = 0; j < addyIdListFromAddy.size(); j++) {
                    if (addyIdListFromCust.get(i) == addyIdListFromAddy.get(j)) {
                        addyIdListFromAddy.remove(j);
                        j--;
                    }
                }
            }
    
            if (addyIdListFromAddy.isEmpty()) {}
            else {
                for (int addyId : addyIdListFromAddy) {
                    state.executeUpdate("DELETE FROM address WHERE addressId = " + addyId);
                }
            }

            // list cityIds
            ResultSet cityIdResults = state.executeQuery("SELECT DISTINCT cityId FROM address ORDER BY cityId");
            ArrayList<Integer> cityIdListFromAddy = new ArrayList<>();
            while (cityIdResults.next()) {
                cityIdListFromAddy.add(cityIdResults.getInt(1));
            }
            // list all cityId's
            cityIdResults = state.executeQuery("SELECT DISTINCT cityId FROM city ORDER BY cityId");
            ArrayList<Integer> cityIdListFromCity = new ArrayList<>();
            while (cityIdResults.next()) {
                cityIdListFromCity.add(cityIdResults.getInt(1));
            }
            // list unused cityids
            for (int i = 0; i < cityIdListFromAddy.size(); i++) {
                for (int j = 0; j < cityIdListFromCity.size(); j++) {
                    if (cityIdListFromAddy.get(i) == cityIdListFromCity.get(j)) {
                        cityIdListFromCity.remove(j);
                        j--;
                    }
                }
            }

            if (cityIdListFromCity.isEmpty()) {}
            else {
                for (int cityId : cityIdListFromCity) {
                    state.executeUpdate("DELETE FROM city WHERE cityId = " + cityId);
                }
            }

            // Create list of countryId's used in City table
            ResultSet countryIdResults = state.executeQuery("SELECT DISTINCT countryId FROM city ORDER BY countryId");
            ArrayList<Integer> countryIdListFromCity = new ArrayList<>();
            while (countryIdResults.next()) {
                countryIdListFromCity.add(countryIdResults.getInt(1));
            }
            // list all country ids
            countryIdResults = state.executeQuery("SELECT DISTINCT countryId FROM country ORDER BY countryId");
            ArrayList<Integer> countryIdListFromCountry = new ArrayList<>();
            while (countryIdResults.next()) {
                countryIdListFromCountry.add(countryIdResults.getInt(1));
            }
            // list unused countryids
            for (int i = 0; i < countryIdListFromCity.size(); i++) {
                for (int j = 0; j < countryIdListFromCountry.size(); j++) {
                    if (Objects.equals(countryIdListFromCity.get(i), countryIdListFromCountry.get(j))) {
                        countryIdListFromCountry.remove(j);
                        j--;
                    }
                }
            }

            if (countryIdListFromCountry.isEmpty()) {}
            else {
                for (int countryId : countryIdListFromCountry) {
                    state.executeUpdate("DELETE FROM country WHERE countryId = " + countryId);
                }
            }
        }
        catch (SQLException e) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorAddingApp"));
            alert.setContentText(resource.getString("errorReqDbase"));
            alert.showAndWait();
        }
    }

    private static void updateCust(int custId, String custName, int addyId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement state = conn.createStatement()) {
            state.executeUpdate("UPDATE customer SET customerName = '" + custName + "', addressId = " + addyId + ", " +
                    "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + currentUser + "' WHERE customerId = " + custId);
        }
    }

    public static void refreshCustList() {
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement state = conn.createStatement()) {
            // get customer list
            ObservableList<Customer> custList = CustomerList.getCustList();
            custList.clear();
            // Create list of customerId's for all active customers
            ResultSet custIdResults = state.executeQuery("SELECT customerId FROM customer WHERE active = 1");
            ArrayList<Integer> custIdList = new ArrayList<>();
            while (custIdResults.next()) {
                custIdList.add(custIdResults.getInt(1));
            }

            for (int custId : custIdList) {
                Customer cust = new Customer();
                ResultSet custResults = state.executeQuery("SELECT customerName, active, addressId FROM customer WHERE customerId = " + custId);
                custResults.next();
                String custName = custResults.getString(1);
                int active = custResults.getInt(2);
                int addyId = custResults.getInt(3);
                cust.setCustId(custId);
                cust.setCustName(custName);
                cust.setActive(active);
                cust.setAddressId(addyId);

                ResultSet addyResults = state.executeQuery("SELECT address, address2, postalCode, phone, cityId FROM address WHERE addressId = " + addyId);
                addyResults.next();
                String addy = addyResults.getString(1);
                String addy2 = addyResults.getString(2);
                String zip = addyResults.getString(3);
                String phone = addyResults.getString(4);
                int cityId = addyResults.getInt(5);
                cust.setAddress(addy);
                cust.setAddress2(addy2);
                cust.setZipCode(zip);
                cust.setPhone(phone);
                cust.setCityId(cityId);

                ResultSet cityResults = state.executeQuery("SELECT city, countryId FROM city WHERE cityId = " + cityId);
                cityResults.next();
                String city = cityResults.getString(1);
                int countryId = cityResults.getInt(2);
                cust.setCity(city);
                cust.setCountryId(countryId);
                ResultSet countryResults = state.executeQuery("SELECT country FROM country WHERE countryId = " + countryId);
                countryResults.next();
                String country = countryResults.getString(1);
                cust.setCountry(country);
                custList.add(cust);
            }
        }
        catch (SQLException e) {
            ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("error"));
            alert.setHeaderText(resource.getString("errorConnDbase"));
            alert.setContentText(resource.getString("errorConnDbaseMessage"));
            alert.show();
        }
    }

    private static void updateApp(int appId, int custId, String title, String desc, String location, 
            String contact, String url, Timestamp startTS, Timestamp endTS) throws SQLException {
        try (Connection conn = DriverManager.getConnection(MyConnector.url,user,pass);
             Statement state = conn.createStatement()) {
            state.executeUpdate("UPDATE appointment SET customerId = " + custId + ", title = '" + title + "', description = '" + desc + "', " +
                    "location = '" + location + "', contact = '" + contact + "', url = '" + url + "', start = '" + startTS + "', end = '" + endTS + "', " +
                    "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + currentUser + "' WHERE appointmentId = " + appId);
        }
    }

    private static boolean checkAppOverlap2(Timestamp startTS, Timestamp endTS) {
        int appIndex = AppSummaryController.getAppIndexMod();
        ObservableList<Appointment> appList = AppList.getAppList();
        appList.remove(appIndex);
        for (Appointment app: appList) {
            Timestamp existingStartTS = app.getStartTimestamp();
            Timestamp existingEndTS = app.getEndTimestamp();
            if (startTS.after(existingStartTS) && startTS.before(existingEndTS)) {
                return true;
            }
            if (endTS.after(existingStartTS) && endTS.before(existingEndTS)) {
                return true;
            }
            if (startTS.after(existingStartTS) && endTS.before(existingEndTS)) {
                return true;
            }
            if (startTS.before(existingStartTS) && endTS.after(existingEndTS)) {
                return true;
            }
            if (startTS.equals(existingStartTS)) {
                return true;
            }
            if (endTS.equals(existingStartTS)) {
                return true;
            }
        }
        return false;
    }
    
    public static void delApp(Appointment appDel) {
        int appId = appDel.getAppId();
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(resource.getString("confirmDelete"));
        alert.setHeaderText(resource.getString("confirmDeleteApp"));
        alert.setContentText(resource.getString("confirmDeleteAppMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try (Connection conn = DriverManager.getConnection(url,user,pass);
                 Statement state = conn.createStatement()) {
                state.executeUpdate("DELETE FROM appointment WHERE appointmentId =" + appId);
            }
            catch (Exception e) {
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle(resource.getString("error"));
                alert2.setHeaderText(resource.getString("errorModApp"));
                alert2.setContentText(resource.getString("errorReqDbase"));
                alert2.showAndWait();
            }
            refreshAppList();
        }
    }

    public static void loginAppNotification() {
        // has main screen been opened 
        if (openCount == 0) {

            ObservableList<Appointment> userApps = FXCollections.observableArrayList();
            for (Appointment app : AppList.getAppList()) {
                if (app.getCreatedBy().equals(currentUser)) {
                    userApps.add(app);
                }
            }
            // Are any appointments coming up in 15 minutes
            for (Appointment app : userApps) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(Date.from(Instant.now()));
                cal.add(Calendar.MINUTE, 15);
                Date notificationCutoff = cal.getTime();
                if (app.getStartDate().before(notificationCutoff)) {
                    ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MainScreen", Locale.getDefault());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(resource.getString("notifiUpcomingApps"));
                    alert.setHeaderText(resource.getString("notifiUpcomingApps"));
                    alert.setContentText(resource.getString("notifiUpcomingAppsMessage") + "\n" + resource.getString("Title") + ": " + 
                            app.getTitle() + "\n" + resource.getString("Desc") + ": " + app.getDesc() + "\n" + resource.getString("Location") +
                            ": " + app.getLocation() + "\n" + resource.getString("Contact") + ": " + app.getContact() + "\n" + 
                            resource.getString("Url") + ": " + app.getUrl() + "\n" + resource.getString("Date") + ": " + app.getDateString() + 
                            "\n" + resource.getString("StartTime") + ": " + app.getStartString() + "\n" + resource.getString("EndTime") + 
                            ": " + app.getEndString());
                    alert.showAndWait();
                }
            }
            openCount++;
        }
    }
    
    //3 reports
    public static void getAppTypeByMonthReport() {
        refreshAppList();
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
        
        String report = resource.getString("AppTypeByMonthTitle");
        ArrayList<String> monthsWithApps = new ArrayList<>();
        for (Appointment app : AppList.getAppList()) {
            Date startDate = app.getStartDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            String yearMonth = year + "-" + month;
            
            if (month < 10) {
                yearMonth = year + "-0" + month;
            }
            if (!monthsWithApps.contains(yearMonth)) {
                monthsWithApps.add(yearMonth);
            }
        }

        Collections.sort(monthsWithApps);
        for (String yearMonth : monthsWithApps) {
            int year = Integer.parseInt(yearMonth.substring(0,4));
            int month = Integer.parseInt(yearMonth.substring(5,7));

            int typeCount = 0;
            ArrayList<String> descs = new ArrayList<>();
            for (Appointment app : AppList.getAppList()) {

                Date startDate = app.getStartDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
 
                int appYear = cal.get(Calendar.YEAR);
                int appMonth = cal.get(Calendar.MONTH) + 1;

                if (year == appYear && month == appMonth) {
                    String desc = app.getDesc();

                    if (!descs.contains(desc)) {
                        descs.add(desc);
                        typeCount++;
                    }
                }
            }
                  
            report = report + "\r\n" + yearMonth  + ": " + typeCount + " ";
            report = report + resource.getString("Types");

            for (String desc : descs) {
                report = report + " " + desc + ",";
            }
            report = report.substring(0, report.length()-1);
        }
        // Print report, will overwtite.
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("report"));
            alert.setHeaderText(resource.getString("reportCreated"));
            alert.setContentText(resource.getString("report1CreatedLocation"));
            alert.showAndWait();
            
            Path path = Paths.get("AppTypeByMonth.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getScheduleConsultants() {
        refreshAppList();
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
        String report = resource.getString("ConsultantScheduleTitle");
        ArrayList<String> consultantsWithApps = new ArrayList<>();
 
        for (Appointment app : AppList.getAppList()) {
            String consultant = app.getCreatedBy();
            if (!consultantsWithApps.contains(consultant)) {
                consultantsWithApps.add(consultant);
            }
        }
        // Sort consultants
        Collections.sort(consultantsWithApps);
        for (String consultant : consultantsWithApps) {
            // Add consultant's name
            report = report + "\r\n" + consultant + ": ";
            for (Appointment app : AppList.getAppList()) {
                String appConsultant = app.getCreatedBy();
                if (consultant.equals(appConsultant)) {
                    String date = app.getDateString();
                    String title = app.getTitle();
                    Date startDate = app.getStartDate();
                    String startTime = startDate.toString().substring(11,16);
                    if (Integer.parseInt(startTime.substring(0,2)) > 12) {
                        startTime = Integer.parseInt(startTime.substring(0,2)) - 12 + startTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(startTime.substring(0,2)) == 12) {
                        startTime = startTime + "PM";
                    }
                    else {
                        startTime = startTime + "AM";
                    }
                    Date endDate = app.getEndDate();
                    String endTime = endDate.toString().substring(11,16);
                    if (Integer.parseInt(endTime.substring(0,2)) > 12) {
                        endTime = Integer.parseInt(endTime.substring(0,2)) - 12 + endTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(endTime.substring(0,2)) == 12) {
                        endTime = endTime + "PM";
                    }
                    else {
                        endTime = endTime + "AM";
                    }
                    // Get the timezone
                    String timeZone = startDate.toString().substring(20,23);

                    report = report + "\r\n" + date + ": " + title + resource.getString("From") + startTime + resource.getString("To") +
                            endTime + " " + timeZone + ". ";
                }
            }
        }
        //print
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("report"));
            alert.setHeaderText(resource.getString("reportCreated"));
            alert.setContentText(resource.getString("report2CreatedLocation"));
            alert.showAndWait();
            
            Path path = Paths.get("ConsultantSchedules.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void getNextMeetingsByCust() {
        refreshAppList();
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/MyConnector", Locale.getDefault());
        String report = resource.getString("CustScheduleTitle") + "\r\n";
        ArrayList<Integer> custIdsWithApps = new ArrayList<>();

        for (Appointment app : AppList.getAppList()) {
            int customerId = app.getCustId();
            if (!custIdsWithApps.contains(customerId)) {
                custIdsWithApps.add(customerId);
            }
        }
        
        Collections.sort(custIdsWithApps);
        refreshCustList();
        for (int custId : custIdsWithApps) {
            for (Customer cust : CustomerList.getCustList()) {
                int custIdCheck = cust.getCustId();
                if (custId == custIdCheck) {
                    // Add name
                    report = report + cust.getCustName() + ": ";
                }
            }
            for (Appointment app : AppList.getAppList()) {
                int appCustId = app.getCustId();

                if (custId == appCustId) {
                    String date = app.getDateString();
                    String desc = app.getDesc();
                    Date startDate = app.getStartDate();
                    String startTime = startDate.toString().substring(11,16);
                    if (Integer.parseInt(startTime.substring(0,2)) > 12) {
                        startTime = Integer.parseInt(startTime.substring(0,2)) - 12 + startTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(startTime.substring(0,2)) == 12) {
                        startTime = startTime + "PM";
                    }
                    else {
                        startTime = startTime + "AM";
                    }
                    Date endDate = app.getEndDate();
                    String endTime = endDate.toString().substring(11,16);
                    if (Integer.parseInt(endTime.substring(0,2)) > 12) {
                        endTime = Integer.parseInt(endTime.substring(0,2)) - 12 + endTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(endTime.substring(0,2)) == 12) {
                        endTime = endTime + "PM";
                    }
                    else {
                        endTime = endTime + "AM";
                    }
                    String timeZone = startDate.toString().substring(20,23);
                    report = report + "\r\n" + date + ": " + desc + resource.getString("From") + startTime + resource.getString("To") +
                            endTime + " " + timeZone + ". ";
                }
            }
        }
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resource.getString("report"));
            alert.setHeaderText(resource.getString("reportCreated"));
            alert.setContentText(resource.getString("report3CreatedLocation"));
            alert.showAndWait();
            
            Path path = Paths.get("CustomersSchedule.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
