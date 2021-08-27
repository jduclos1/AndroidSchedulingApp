package Calendar;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import schedulingappnew.Model.AppList;
import schedulingappnew.Model.Appointment;

public class MonthlyView {
    private Text monthTitle;
    private ArrayList<AnchorNode> calDayPanes = new ArrayList<>(35); 
    private YearMonth currentYearMonth;
    private VBox monthViewBox;

    public MonthlyView(YearMonth yearMonth) {
        currentYearMonth = yearMonth;
        //Create a gridpane
        GridPane cal = new GridPane();
        cal.setPrefSize(600,400);
        cal.setGridLinesVisible(true);

        for (int i=0; i<5; i++) {
            // Create 7 columns
            for (int j=0; j<7; j++) {
                AnchorNode an = new AnchorNode();
                an.setPrefSize(200,200);
                cal.add(an, j, i);
                calDayPanes.add(an);
            }
        }

        // Create Array
        Text[] daysOfWeek;
        ResourceBundle resource = ResourceBundle.getBundle("schedulingappnew.Resources/CalText", Locale.getDefault());
        daysOfWeek = new Text[]{new Text(resource.getString("Monday")), new Text(resource.getString("Tuesday")), new Text(resource.getString("Wednesday")),
                new Text(resource.getString("Thursday")), new Text(resource.getString("Friday")), new Text(resource.getString("Saturday")),
                new Text(resource.getString("Sunday"))};
        
        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(600);
        int col = 0;
        for (Text day : daysOfWeek) {
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(200,10);
            ap.setBottomAnchor(day, 5.0);
            day.setWrappingWidth(100);
            day.setTextAlignment(TextAlignment.CENTER);
            ap.getChildren().add(day);
            dayLabels.add(ap, col++, 0);
        }

        //Set title, add buttons
        monthTitle = new Text();
        Button backAMonthButton = new Button("<");
        //Lambda expressions to assign button handlers for the calendar
        backAMonthButton.setOnAction(event -> backAMonth());
        Button forwardAMonthButton = new Button(">");
        //Lambda expressions to assign button handlers for the calendar
        forwardAMonthButton.setOnAction(event -> forwardAMonth());
        
        // Create Title/Button HBox
        HBox titleBar = new HBox(backAMonthButton, monthTitle, forwardAMonthButton);
        titleBar.setAlignment(Pos.BASELINE_CENTER);

        populateCal(yearMonth);

        monthViewBox = new VBox(titleBar, dayLabels, cal);
    }
    
    private void backAMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCal(currentYearMonth);
    }

    private void forwardAMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCal(currentYearMonth);
    }

    public void populateCal(YearMonth yearMonth) {
        // Get starting date
        LocalDate calDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        
        while (!calDate.getDayOfWeek().toString().equals("MONDAY")) {
            calDate = calDate.minusDays(1);
        }

        // Set Title = month
        String thisMonth = new DateFormatSymbols().getMonths()[yearMonth.getMonthValue()-1];
        String properMonth = thisMonth.substring(0,1).toUpperCase() + thisMonth.substring(1);
        monthTitle.setText("  " + properMonth + " " + String.valueOf(yearMonth.getYear()) + "  ");

        // fill days and apps
        for (AnchorNode an : calDayPanes) {
            // Clear existing
            if (an.getChildren().size() != 0) {
                an.getChildren().remove(0,an.getChildren().size());
            }
            // add day
            Text date = new Text(String.valueOf(calDate.getDayOfMonth()));
            an.setDate(calDate);
            an.setTopAnchor(date, 5.0);
            an.setLeftAnchor(date, 5.0);
            an.getChildren().add(date);
            
            // how many apps
            ObservableList<Appointment> appList = AppList.getAppList();
            int calDateYear = calDate.getYear();
            int calDateMonth = calDate.getMonthValue();
            int calDateDay = calDate.getDayOfMonth();
            int appCount = 0;
            for (Appointment app : appList) {
                Date appDate = app.getStartDate();
                Calendar cal  = Calendar.getInstance(TimeZone.getDefault());
                cal.setTime(appDate);
                int appYear = cal.get(Calendar.YEAR);
                int appMonth = cal.get(Calendar.MONTH) + 1;
                int appDay = cal.get(Calendar.DAY_OF_MONTH);
                if (calDateYear == appYear && calDateMonth == appMonth && calDateDay == appDay) {
                    appCount++;
                }
            }

            if (appCount != 0) {
                Text appsForDay = new Text(String.valueOf(appCount));
                appsForDay.setFont(Font.font(30));
                appsForDay.setFill(Color.BLUE);

                an.getChildren().add(appsForDay);
                an.setTopAnchor(appsForDay, 20.0);
                an.setLeftAnchor(appsForDay, 40.0);
            }
            // Increment date
            calDate = calDate.plusDays(1);
        }
    }

    public VBox getView() {
        return monthViewBox;
    }

    public YearMonth getCurrentYearMonth() {
        return currentYearMonth;
    }
    
    
    
    
}
