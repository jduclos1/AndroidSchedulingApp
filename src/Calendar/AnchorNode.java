package Calendar;

import java.time.LocalDate;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;


public class AnchorNode extends AnchorPane{
    
    private LocalDate date;

    // Creates date panes
    public AnchorNode (Node... children) {
        super(children);
    }
    
    // Getters
    public LocalDate getDate() {
        return date;
    }
    
    // Setters
    public void setDate(LocalDate date) {
        this.date = date;
    }

    
}
