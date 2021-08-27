package schedulingappnew.Model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Appointment {
    private IntegerProperty appId;
    private IntegerProperty custId;
    private StringProperty title;
    private StringProperty location;
    private StringProperty desc;
    private StringProperty contact;
    private StringProperty url;
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
    private Date startDate;
    private Date endDate;
    private StringProperty createdBy;
    private StringProperty startString;
    private StringProperty endString;
    private StringProperty dateString;

    public static String isAppValid(Customer cust, String title, String desc, String location, LocalDate appDate, String startHour, 
            String startMin, String startAmPm, String endHour, String endMin, String endAmPm) {
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/Appointment", Locale.getDefault());
        
        String error = "";
        try {
            if (cust == null) {
                error = error + resource.getString("errorCust");
            }
            if (title.length() == 0) {
                error = error + resource.getString("errorTitle");
            }
            if (desc.length() == 0) {
                error = error + resource.getString("errorDesc");
            }
            if (location.length() == 0) {
                error = error + resource.getString("errorLocation");
            }
            if (Integer.parseInt(startHour) < 1 || Integer.parseInt(startHour) > 12 || Integer.parseInt(endHour) < 1 || Integer.parseInt(endHour) > 12 ||
                    Integer.parseInt(startMin) < 0 || Integer.parseInt(startMin) > 59 || Integer.parseInt(endMin) < 0 || Integer.parseInt(endMin) > 59) {
                error = error + resource.getString("errorInvalidTime");
            }
            if ((Integer.parseInt(startHour) < 9 && startAmPm.equals("AM")) || (Integer.parseInt(endHour) < 9 && endAmPm.equals("AM")) ||
                    (Integer.parseInt(startHour) >= 5 && Integer.parseInt(startHour) < 12 && startAmPm.equals("PM")) || (Integer.parseInt(endHour) >= 5 && Integer.parseInt(endHour) < 12 && endAmPm.equals("PM")) ||
                    (Integer.parseInt(startHour) == 12 && startAmPm.equals("AM")) || (Integer.parseInt(endHour)) == 12 && endAmPm.equals("AM")) {
                error = error + resource.getString("errorOutsideHours");
            }
            if ((startAmPm.equals("PM") && endAmPm.equals("AM")) || (startAmPm.equals(endAmPm) && Integer.parseInt(startHour) != 12 && Integer.parseInt(startHour) > Integer.parseInt(endHour)) ||
                    (startAmPm.equals(endAmPm) && startHour.equals(endHour) && Integer.parseInt(startMin) > Integer.parseInt(endMin))) {
                error = error + resource.getString("errorStartAfterEnd");
            }
            if (appDate == null || startHour.equals("") || startMin.equals("") || startAmPm.equals("") ||
                    endHour.equals("") || endMin.equals("") || endAmPm.equals("")) {
                error = error + resource.getString("errorIncomplete");
            }
            if (appDate.getDayOfWeek().toString().toUpperCase().equals("SATURDAY") || appDate.getDayOfWeek().toString().toUpperCase().equals("SUNDAY")) {
                error = error + resource.getString("errorWeekend");
            }
        }
        catch (NumberFormatException e) {
            error = error + resource.getString("errorInteger");
        }
        finally {
            return error;
        }
    }

    Appointment(int appId, int custId, String title, String desc, String location, String contact, String url, Timestamp startTS, 
            Timestamp endTS, Date startDate, Date endDate, String createdBy) {
        this.appId = new SimpleIntegerProperty(appId);
        this.custId = new SimpleIntegerProperty(custId);
        this.title = new SimpleStringProperty(title);
        this.desc = new SimpleStringProperty(desc);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.url = new SimpleStringProperty(url);
        this.startTimestamp = startTS;
        this.endTimestamp = endTS;
        this.startDate = startDate;
        this.endDate = endDate;
        
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        this.dateString = new SimpleStringProperty(format.format(startDate));
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a z");
        this.startString = new SimpleStringProperty(formatTime.format(startDate));
        this.endString = new SimpleStringProperty(formatTime.format(endDate));
        this.createdBy = new SimpleStringProperty(createdBy);
    }
    
    //Getters
    public int getAppId() {
        return this.appId.get();
    }
    
    public int getCustId() {
        return this.custId.get();
    }
    
    public String getTitle() {
        return this.title.get();
    }
    
    public String getDesc() {
        return this.desc.get();
    }
    
    public String getLocation() {
        return this.location.get();
    }
    
    public String getContact() {
        return this.contact.get();
    }
    
    public String getUrl() {
        return this.url.get();
    }
    
    public Timestamp getStartTimestamp() {
        return this.startTimestamp;
    }
    
    public Timestamp getEndTimestamp() {
        return this.endTimestamp;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }
    
    public String getDateString() {
        return this.dateString.get();
    }
    
    public String getStartString() {
        return this.startString.get();
    }
    
    public String getEndString() {
        return this.endString.get();
    }
    
    public String getCreatedBy() {
        return this.createdBy.get();
    }
    
    //Properties
    public IntegerProperty appIdProp() {
        return this.appId;
    }

    public IntegerProperty custIdProp() {
        return this.custId;
    }

    public StringProperty titleProp() {
        return this.title;
    }

    public StringProperty descProp() {
        return this.desc;
    }

    public StringProperty locationProp() {
        return this.location;
    }

    public StringProperty contactProp() {
        return this.contact;
    }

    public StringProperty urlProp() {
        return this.url;
    }

    public StringProperty dateStringProp() {
        return this.dateString;
    }

    public StringProperty startStringProp() {
        return this.startString;
    }
    
    public StringProperty endStringProp() {
        return this.endString;
    }

    public StringProperty createdByProp() {
        return this.createdBy;
    }
    
    //Setters
    public void setAppId(int appId) {
        this.appId.set(appId);
    }

    public void setCustId(int custId) {
        this.custId.set(custId);
    }

    public void setTitle(String title) {
        this.title.set(title);
    }
    
    public void setDesc(String desc) {
        this.desc.set(desc);
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public void setUrl(String url) {
        this.url.set(url);
    }
    
    public void setStartTimestamp(Timestamp startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(Timestamp endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public void setCreatedBy (String createdBy) {
        this.createdBy.set(createdBy);
    }
}
