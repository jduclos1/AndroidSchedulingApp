package schedulingappnew.Model;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {
    
    private final IntegerProperty custId;
    private final StringProperty custName;
    private final IntegerProperty active;
    private final IntegerProperty addressId;
    private final StringProperty address;
    private final StringProperty address2;
    private final StringProperty zipCode;
    private final StringProperty phone;
    private final IntegerProperty cityId;
    private final StringProperty city;
    private final IntegerProperty countryId;
    private final StringProperty country;

    public Customer() {
        custId = new SimpleIntegerProperty();
        custName = new SimpleStringProperty();
        active = new SimpleIntegerProperty();
        addressId = new SimpleIntegerProperty();
        address = new SimpleStringProperty();
        address2 = new SimpleStringProperty();
        zipCode = new SimpleStringProperty();
        phone = new SimpleStringProperty();
        cityId = new SimpleIntegerProperty();
        city = new SimpleStringProperty();
        countryId = new SimpleIntegerProperty();
        country = new SimpleStringProperty();
    }
    
    //Setters:
    public void setCustId(int custId){
        this.custId.set(custId);
    }
    
    public void setCustName(String custName){
        this.custName.set(custName);
    }
    
    public void setActive(int active){
        this.active.set(active);
    }
    
    public void setAddressId(int addressId){
        this.addressId.set(addressId);
    }
    
    public void setAddress(String address){
        this.address.set(address);
    }
    
    public void setAddress2(String address2){
        this.address2.set(address2);
    }
    
    public void setZipCode(String zipCode){
        this.zipCode.set(zipCode);
    }
    
    public void setPhone(String phone){
        this.phone.set(phone);
    }
    
    public void setCityId(int cityId){
        this.cityId.set(cityId);
    }
    
    public void setCity(String city){
        this.city.set(city);
    }
    
    public void setCountryId(int countryId){
        this.countryId.set(countryId);
    }
    
    
    public void setCountry(String country){
        this.country.set(country);
    }
    
    //Getters:
    public int getCustId(){
        return this.custId.get();
    }
    
    public String getCustName(){
        return this.custName.get();
    }
    
    public int getActive(){
        return this.active.get();
    }
    
    public int getAddressId(){
        return this.addressId.get();
    }
    
    public String getAddress(){
        return this.address.get();
    }
    
    public String getAddress2(){
        return this.address2.get();
    }
    
    public String getZipCode(){
        return this.zipCode.get();
    }
    
    public String getPhone(){
        return this.phone.get();
    }
    
    public int getCityId(){
        return this.cityId.get();
    }
    
    public String getCity(){
        return this.city.get();
    }
    
    public int getCountryId(){
        return this.countryId.get();
    }
    
    public String getCountry(){
        return this.country.get();
    }
    
    public IntegerProperty custIdProperty(){
        return custId;
    }
    
    public StringProperty custNameProperty(){
        return custName;
    }
    
    public IntegerProperty activeProperty(){
        return active;
    }
    
    public IntegerProperty addressIdProperty(){
        return addressId;
    }
    
    public StringProperty addressProperty(){
        return address;
    }
    
    public StringProperty address2Property(){
        return address2;
    }
    
    public StringProperty zipCodeProperty(){
        return zipCode;
    }
    
    public StringProperty phoneProperty(){
        return phone;
    }
    
    public IntegerProperty cityIdProp(){
        return cityId;
    }
    
    public StringProperty cityProperty(){
        return city;
    }
    
    public IntegerProperty countryIdProperty(){
        return countryId;
    }
    
    public StringProperty countryProperty(){
        return country;
    }

    public static String isValidCust(String custName, String addy, String city, String country, String zip, String phone){
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Customer", Locale.getDefault());
        
        String error = "";
        
        if (custName.length() == 0) {
            error = error + resource.getString("errorName");
        }
        if (addy.length() == 0) {
            error = error + resource.getString("errorAddy");
        }
        if (phone.length() == 0) {
            error = error + resource.getString("errorPhone");
        }
        if (city.length() == 0) {
            error = error + resource.getString("errorCity");
        }
        if (zip.length() == 0) {
            error = error + resource.getString("errorZip");
        }
        if (country.length() == 0) {
            error = error + resource.getString("errorCountry");
        }
        return error;
    }


    
}
