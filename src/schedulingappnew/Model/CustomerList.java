package schedulingappnew.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomerList {
    
    private static ObservableList<Customer> custList = FXCollections.observableArrayList();

    public static ObservableList<Customer> getCustList() {
        return custList;
    }
    
}
