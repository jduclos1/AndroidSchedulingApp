package schedulingappnew.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppList {
    
    private static ObservableList<Appointment> appList = FXCollections.observableArrayList();

    public static ObservableList<Appointment> getAppList() {
        return appList;
        
    }
    
}
