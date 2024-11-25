package OtherClasses;

import controllers.customer.HomeFormController;
import db.DBConnection;
import db.DBOperations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class RestaurantTable extends HomeFormController {
    private static int tableId = 0;
    private static int capacity;
    private Integer tableIdInstanceVar;
    private Integer capacityInstanceVar;
    private static Map<Integer, Boolean> tableAvailability = new HashMap<>();


    public RestaurantTable() {
    }

    //Methods

    public static ObservableList<RestaurantTable> getObsListTables(){
        ObservableList<RestaurantTable> restaurantTables = FXCollections.observableArrayList();
        restaurantTables.clear();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from tables");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int tableId = resultSet.getInt(1);
                int capacity = resultSet.getInt(2);
                RestaurantTable restaurantTable = new RestaurantTable();
                restaurantTable.setTableIdInstanceVar(tableId);
                restaurantTable.setCapacityInstanceVar(capacity);
                restaurantTables.add(restaurantTable);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return restaurantTables;
    }

    public void showTableView() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/TableViewForm.fxml"));
        Parent parent = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.setTitle("Select a Table");
        stage.initModality(Modality.APPLICATION_MODAL);
        // Not setting owner to make it independent from the main window
        stage.showAndWait();
    }

    public void checkTableAvailability(LocalDate startDate,LocalDate endDate,int startTime,int endTime,int numberOfGuests,String weekDaysInLetters){
        //Pick tables one by one
        for (int i = 0; i < 13; i++) {
            tableAvailability.put(i+1, true); // Assume all tables are available initially
            tableId = i+1;
            boolean isAvailable = true; // Assume all tables are temporary available initially
            //check whether if there is a booking existing with tableID
            ResultSet bookingsByTableID = DBOperations.getBookingsByTableID(tableId);
            try {
                while (bookingsByTableID.next()) {
                    if (endDate == null){
                        // Check for one-time booking
                        isAvailable = checkOneTimeBooking(bookingsByTableID,startDate,startTime,endTime);
                    } else {
                        // Check for recurring booking
                        isAvailable = checkRecurringBooking(bookingsByTableID,startDate,endDate,startTime,endTime,weekDaysInLetters);
                    }
                    if (!isAvailable) {
                        break;
                    }
                } //If not, it is temporary available
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // If the table is temporarily available, check the seating capacity
            capacity = DBOperations.getTableSeatingCapacity(tableId);
            if (isAvailable && (numberOfGuests > capacity)){
                isAvailable = false;
            }
            // Update the availability status in the map
            tableAvailability.put(tableId,isAvailable);
        }
        tableId = 0;
    }

    private boolean checkOneTimeBooking(ResultSet resultSet, LocalDate inputStartDate, int inputStartTime, int inputEndTime) throws SQLException {
        LocalDate dbStartDate = resultSet.getDate("start_date").toLocalDate();
        LocalDate dbEndDate = null;
        if (resultSet.getDate("end_date")!=null){
            dbEndDate = resultSet.getDate("end_date").toLocalDate();
        }
        //If the booking in the database is also a one-time booking
        if (dbEndDate == null) {
            //Check date conflicting
            if (inputStartDate.isEqual(dbStartDate)){
                //Check time conflicting(Returns true if it is not conflicting)
                return isTimeNotConflict(resultSet, inputStartTime, inputEndTime);
            } else {return true;}
        } else {
            //If the booking in the database is recurring booking
            String expandedDbWeekDays = weekDaysDecrypt(resultSet.getString("weekDays"));
            //Iterate through week days
            for (String day : expandedDbWeekDays.split(" ")) {
                LocalDate currentDbDate = dbStartDate;
                //Iterate through all dates
                while (!currentDbDate.isAfter(dbEndDate)){
                    //Check date conflicting
                    if (day.equals(currentDbDate.getDayOfWeek().toString()) && currentDbDate.isEqual(inputStartDate)){
                        //Check time conflicting(Returns true if it is not conflicting)
                        return isTimeNotConflict(resultSet, inputStartTime, inputEndTime);
                    }
                    currentDbDate = currentDbDate.plusDays(1);
                }
            }
        }

        return true;
    }

    private boolean checkRecurringBooking(ResultSet resultSet, LocalDate inputStartDate, LocalDate inputEndDate, int inputStartTime, int inputEndTime, String weekDaysInLetters) throws SQLException {
        LocalDate dbStartDate = resultSet.getDate("start_date").toLocalDate();
        LocalDate dbEndDate = null;
        if (resultSet.getDate("end_date")!=null){
            dbEndDate = resultSet.getDate("end_date").toLocalDate();
        }
        //If the booking in the database is a one-time booking
        if (dbEndDate == null) {
            String expandedInputWeekDays = weekDaysDecrypt(weekDaysInLetters);
            //Iterate through input week days
            for (String inputDay : expandedInputWeekDays.split(" ")) {
                LocalDate currentInputDate = inputStartDate;
                //Iterate through all dates
                while (!currentInputDate.isAfter(inputEndDate)){
                    //Check date conflicting
                    if (inputDay.equals(currentInputDate.getDayOfWeek().toString()) && currentInputDate.isEqual(dbStartDate)){
                        //Check time conflicting(Returns true if it is not conflicting)
                        return isTimeNotConflict(resultSet, inputStartTime, inputEndTime);
                    }
                    currentInputDate = currentInputDate.plusDays(1);
                }
            }
        } else {
            //If the booking in the database is also a recurring booking
            String expandedDbWeekDays = weekDaysDecrypt(resultSet.getString("weekDays"));
            //Iterate through db week days
            for (String dbDay : expandedDbWeekDays.split(" ")) {
                LocalDate currentDbDate = dbStartDate;
                //Iterate through all dates
                while (!currentDbDate.isAfter(dbEndDate)){
                    //this is for the iterating input days
                    if (dbDay.equals(currentDbDate.getDayOfWeek().toString())) {
                        String expandedInputWeekDays = weekDaysDecrypt(weekDaysInLetters);
                        //Iterate through input week days
                        for (String inputDay : expandedInputWeekDays.split(" ")) {
                            LocalDate currentInputDate = inputStartDate;
                            //Iterate through all dates
                            while (!currentInputDate.isAfter(inputEndDate)){
                                //Check date conflicting
                                if (inputDay.equals(currentInputDate.getDayOfWeek().toString()) && currentInputDate.isEqual(currentDbDate)){
                                    //Check time conflicting(Returns true if it is not conflicting)
                                    return isTimeNotConflict(resultSet, inputStartTime, inputEndTime);
                                }
                                currentInputDate = currentInputDate.plusDays(1);
                            }
                        }
                    }
                    currentDbDate = currentDbDate.plusDays(1);
                }
            }
        }
        return true;
    }

    private boolean isTimeNotConflict(ResultSet resultSet, int inputStartTime, int inputEndTime) throws SQLException {
        int dbStartTime = resultSet.getInt("start_time");
        int dbEndTime = resultSet.getInt("end_time");
        return (inputStartTime >= dbEndTime) || (inputEndTime <= dbStartTime);
    }

    // Method to decrypt weekDaysInLetters
    private String weekDaysDecrypt(String weekDaysInLetters) {
        StringBuilder weekDaysExpanded = new StringBuilder();
        if (weekDaysInLetters.contains("A")) weekDaysExpanded.append("Monday ");
        if (weekDaysInLetters.contains("B")) weekDaysExpanded.append("Tuesday ");
        if (weekDaysInLetters.contains("C")) weekDaysExpanded.append("Wednesday ");
        if (weekDaysInLetters.contains("D")) weekDaysExpanded.append("Thursday ");
        if (weekDaysInLetters.contains("E")) weekDaysExpanded.append("Friday ");
        if (weekDaysInLetters.contains("F")) weekDaysExpanded.append("Saturday ");
        if (weekDaysInLetters.contains("G")) weekDaysExpanded.append("Sunday ");
        return weekDaysExpanded.toString().trim().toUpperCase();
    }


    //Getters and setters

    public static int getTableId() {
        return tableId;
    }

    public static void setTableId(int tableId) {
        RestaurantTable.tableId = tableId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setTableIdInstanceVar(Integer tableIdInstanceVar) {
        this.tableIdInstanceVar = tableIdInstanceVar;
    }

    public void setCapacityInstanceVar(Integer capacityInstanceVar) {
        this.capacityInstanceVar = capacityInstanceVar;
    }

    public Integer getTableIdInstanceVar() {
        return tableIdInstanceVar;
    }

    public Integer getCapacityInstanceVar() {
        return capacityInstanceVar;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public static Map<Integer, Boolean> getTableAvailability() {
        return tableAvailability;
    }
}
