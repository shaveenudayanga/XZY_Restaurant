package controllers.admin;

import OtherClasses.Booking;
import OtherClasses.Customer;
import OtherClasses.RestaurantTable;
import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class ManageBookingsFormController{
    public AnchorPane paneManageBookings;
    public VBox vbxMain;
    public TableView<Booking> tblBookings;
    public TableColumn<Booking, String> clmBookingId;
    public TableColumn<Booking, LocalDate> clmBookingDate;
    public TableColumn<Booking, String> clmCustomerId;
    public TableColumn<Booking, Integer> clmTableId;
    public TableColumn<Booking, Integer> clmGuests;
    public TableColumn<Booking, LocalDate> clmStartDate;
    public TableColumn<Booking, LocalDate> clmEndDate;
    public TableColumn<Booking, String> clmDaysOfWeek;
    public TableColumn<Booking, Integer> clmTimeFrom;
    public TableColumn<Booking, Integer> clmTimeTo;
    public TableColumn<Booking, String> clmComments;
    public HBox btnsUpdateDeleteHBox;
    public TextField txtKeyword;
    public Button btnDoneAdd;
    public Button btnUpdate;
    public Button btnDelete;

    //Update
    public GridPane grdUpdatePopup;
    public DatePicker dtpUpdateStartDate;
    public DatePicker dtpUpdateEndDate;
    public CheckBox chcUpdateMon;
    public CheckBox chcUpdateTue;
    public CheckBox chcUpdateWed;
    public CheckBox chcUpdateThu;
    public CheckBox chcUpdateFri;
    public CheckBox chcUpdateSat;
    public CheckBox chcUpdateSun;
    public ComboBox<String> cmbUpdateTimeFrom;
    public ComboBox<String> cmbUpdateTimeTo;
    public ComboBox<Integer> cmbUpdateGuests;
    public Label lblUpdateTableId;
    public Button btnUpdateSelectTable;
    public TextArea txtUpdateComments;
    public Button btnDoneUpdate;

    //Add New
    public GridPane grdAddPopup;
    public ComboBox<Customer> cmbUsers;
    public DatePicker dtpAddStartDate;
    public DatePicker dtpAddEndDate;
    public CheckBox chcAddMon;
    public CheckBox chcAddTue;
    public CheckBox chcAddWed;
    public CheckBox chcAddThu;
    public CheckBox chcAddFri;
    public CheckBox chcAddSat;
    public CheckBox chcAddSun;
    public ComboBox<String> cmbAddTimeFrom;
    public ComboBox<String> cmbAddTimeTo;
    public ComboBox<Integer> cmbAddGuests;
    public Label lblAddTableId;
    public Button btnAddSelectTable;
    public TextArea txtAddComments;


    Customer customer = new Customer();
    Booking booking = new Booking();


    public void initialize(){
        loadTable(booking.getObsListBookings(this));
        String[] hours = {
                "08:00","09:00","10:00","11:00","12:00","13:00","14:00",
                "15:00","16:00","17:00","18:00","19:00","20:00"};
        cmbUpdateTimeFrom.getItems().addAll(hours);
        cmbUpdateTimeTo.getItems().addAll(hours);
        cmbAddTimeFrom.getItems().addAll(hours);
        cmbAddTimeTo.getItems().addAll(hours);
        Integer[] guests = {1,2,3,4,5,6,7,8};
        cmbAddGuests.getItems().addAll(guests);
        cmbUpdateGuests.getItems().addAll(guests);
    }


    //Table View

    public void loadTable(ObservableList<Booking> bookings) {

        //Reset all as it is
        tblBookings.getSelectionModel().clearSelection();
        vbxMain.setDisable(false);
        vbxMain.setStyle("");
        grdUpdatePopup.setVisible(false);
        grdAddPopup.setVisible(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        clearFields();
        tblBookings.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Booking>() {
            @Override
            public void changed(ObservableValue<? extends Booking> observableValue, Booking booking, Booking t1) {
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });

        //Add booking data to columns
        clmBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        clmBookingDate.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        clmCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        clmTableId.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        clmGuests.setCellValueFactory(new PropertyValueFactory<>("noOfGuests"));
        clmStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        clmEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        clmDaysOfWeek.setCellValueFactory(new PropertyValueFactory<>("weekDaysExpanded"));
        clmTimeFrom.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        clmTimeTo.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        clmComments.setCellValueFactory(new PropertyValueFactory<>("comments"));
        tblBookings.setItems(bookings);

        //Initial filterd list
        FilteredList<Booking> filteredData = new FilteredList<>(bookings, b -> true);
        txtKeyword.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredData.setPredicate(booking -> {
                //If no search value, then display all records or whatever records it current have. No changes.
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                if (booking.getBookingId().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (booking.getBookingDate().toString().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (booking.getCustomerId().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (booking.getTableId().toString().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (booking.getNoOfGuests().toString().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (booking.getStartDate().toString().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (booking.getEndDate()!=null && booking.getEndDate().toString().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (booking.getStartTime().toString().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (booking.getEndTime().toString().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (booking.getWeekDaysExpanded().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (booking.getComments().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else {
                    return false; //No matches found
                }
            });
        });

        //List with matches
        SortedList<Booking> sortedData = new SortedList<>(filteredData);

        //Bind the sorted result with Table View
        sortedData.comparatorProperty().bind(tblBookings.comparatorProperty());

        //Apply filtered data to the Table View
        tblBookings.setItems(sortedData);
    }

    public void btnAddOnAction(ActionEvent actionEvent) {
        vbxMain.setDisable(true);
        vbxMain.setStyle("-fx-background-color: rgba(0,0,0,0.2)");
        grdAddPopup.setVisible(true);
        populateCustomersComboBox();
        btnAddSelectTable.setOnAction(actionEvent1 -> {
            AddResult result = getAddResult();
            selectTablePopup(result.startDate,result.endDate,result.startTime,result.endTime,result.guests,result.days());
            lblAddTableId.setText("Table ID: "+RestaurantTable.getTableId());
        });
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        vbxMain.setDisable(true);
        vbxMain.setStyle("-fx-background-color: rgba(0,0,0,0.2)");
        grdUpdatePopup.setVisible(true);
        dtpUpdateStartDate.setValue(tblBookings.getSelectionModel().getSelectedItem().getStartDate());
        try {
            dtpUpdateEndDate.setValue(tblBookings.getSelectionModel().getSelectedItem().getEndDate());
        } catch (Exception e) {}
        cmbUpdateTimeFrom.setValue(tblBookings.getSelectionModel().getSelectedItem().getStartTime()+":00");
        cmbUpdateTimeTo.setValue(tblBookings.getSelectionModel().getSelectedItem().getEndTime()+":00");
        cmbUpdateGuests.setValue(tblBookings.getSelectionModel().getSelectedItem().getNoOfGuests());
        lblUpdateTableId.setText("Table* : "+tblBookings.getSelectionModel().getSelectedItem().getTableId());
        txtUpdateComments.setText(tblBookings.getSelectionModel().getSelectedItem().getComments());
        showOnCheckBoxes(chcUpdateMon,chcUpdateTue,chcUpdateWed,chcUpdateThu,chcUpdateFri,chcUpdateSat,chcUpdateSun);
        btnUpdateSelectTable.setOnAction(actionEvent1 -> {
            UpdateResult result = getUpdateResult();
            selectTablePopup(result.startDate,result.endDate,result.startTime,result.endTime,result.guests,result.days());
            lblUpdateTableId.setText("Table* : "+RestaurantTable.getTableId());
        });
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        tblBookings.getSelectionModel().getSelectedItem().deleteBooking();
        loadTable(booking.getObsListBookings(this));
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        clearFields();
    }


    //Update Booking

    private UpdateResult getUpdateResult() {
        int guests = cmbUpdateGuests.getValue();
        LocalDate startDate = dtpUpdateStartDate.getValue();
        LocalDate endDate = null;
        int startTime = Integer.parseInt(cmbUpdateTimeFrom.getValue().substring(0,2));
        int endTime = Integer.parseInt(cmbUpdateTimeTo.getValue().substring(0,2));
        try {
            endDate = dtpUpdateEndDate.getValue();
        } catch (NullPointerException ignored) {}
        String comments = txtUpdateComments.getText();
        String days = "";
        try {
            if (chcUpdateMon.isSelected())
                days += "A";
            if (chcUpdateTue.isSelected())
                days += "B";
            if (chcUpdateWed.isSelected())
                days += "C";
            if (chcUpdateThu.isSelected())
                days += "D";
            if (chcUpdateFri.isSelected())
                days += "E";
            if (chcUpdateSat.isSelected())
                days += "F";
            if (endDate!=null && days.isEmpty())
                days = "ABCEDFG";
        } catch (Exception ignored) {}
        return new UpdateResult(guests, startDate, endDate, startTime, endTime, comments, days);
    }

    private record UpdateResult(int guests, LocalDate startDate, LocalDate endDate, int startTime, int endTime, String comments, String days) {
    }

    public void btnDoneUpdateOnAction(ActionEvent actionEvent) {
        UpdateResult result = getUpdateResult();
        tblBookings.getSelectionModel().selectedItemProperty().getValue().updateBooking(RestaurantTable.getTableId(), result.guests(), result.startDate(), result.endDate(), result.startTime(), result.endTime(), result.days(), result.comments());
        loadTable(booking.getObsListBookings(this));
    }

    public void btnUpdateCancelOnAction(ActionEvent actionEvent) {
        loadTable(booking.getObsListBookings(this));
    }


    //Add Booking

    public void btnDoneAddOnAction(ActionEvent actionEvent) {
        AddResult result = getAddResult();
        System.out.println(cmbUsers);
        String customerId = cmbUsers.getValue().userId();
        int tableId = RestaurantTable.getTableId();
        String bookingId = booking.autoGenerateBookingID();
        LocalDate bookingDate = LocalDate.now();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into bookings values(?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setObject(1,bookingId);
            preparedStatement.setObject(2,customerId);
            preparedStatement.setObject(3,tableId);
            preparedStatement.setObject(4,bookingDate);
            preparedStatement.setObject(5, result.startDate());
            preparedStatement.setObject(6, result.endDate());
            preparedStatement.setObject(7, result.startTime());
            preparedStatement.setObject(8, result.endTime());
            preparedStatement.setObject(9, result.guests());
            preparedStatement.setObject(10, result.comments());
            preparedStatement.setObject(11, result.days());
            int i = preparedStatement.executeUpdate();

            if (i!=0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"New Booking Added successfully!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Something went wrong...");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.out.println("An error occurs when inserting to the database");
        }
        loadTable(booking.getObsListBookings(this));
    }

    public void btnAddCancelOnAction(ActionEvent actionEvent) {
        loadTable(booking.getObsListBookings(this));
        clearFields();
    }

    public void populateCustomersComboBox(){
        cmbUsers.setItems(Customer.getObsListCustomers());
//        // Initial filtered list
//        FilteredList<Customer> filteredData = new FilteredList<>(Customer.getObsListCustomers(), b -> true);
//
//        cmbUsers.setEditable(true);
//        cmbUsers.setItems(filteredData);
//        cmbUsers.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
//            filteredData.setPredicate(customer -> {
//                // If no search value, then display all records or whatever records it currently has. No changes.
//                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
//                    return true;
//                }
//
//                String searchKeyword = newValue.toLowerCase();
//
//                // Check if any matches found
//                if (customer.userId().toLowerCase().contains(searchKeyword) ||
//                        customer.firstName().toLowerCase().contains(searchKeyword) ||
//                        (customer.lastName() != null && customer.lastName().toLowerCase().contains(searchKeyword)) ||
//                        customer.username().toLowerCase().contains(searchKeyword) ||
//                        customer.phoneNo().toString().contains(searchKeyword)) {
//                    return true;
//                } else {
//                    return false; // No matches found
//                }
//            });
//            if (!filteredData.isEmpty()) {
//                cmbUsers.show();
//            } else {
//                cmbUsers.hide();
//            }
//        });
//
//        // List with matches
//
//        SortedList<Customer> sortedData = new SortedList<>(filteredData);
//
//
//
//        // Apply filtered data to the ComboBox
//        try {
//            cmbUsers.setItems(sortedData);
//        } catch (Exception e) {
//
//        }
//
//        cmbUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue != null) {
//                System.out.println("Selected Customer: " + newValue);
//                // Add any additional handling for the selected customer here
//            }
//        });
    }

    private AddResult getAddResult() {
        int guests = cmbAddGuests.getValue();
        LocalDate startDate = dtpAddStartDate.getValue();
        LocalDate endDate = null;
        int startTime = Integer.parseInt(cmbAddTimeFrom.getValue().substring(0,2));
        int endTime = Integer.parseInt(cmbAddTimeTo.getValue().substring(0,2));
        try {
            endDate = dtpAddEndDate.getValue();
        } catch (NullPointerException ignored) {}
        String comments = txtAddComments.getText();
        String days = "";
        try {
            if (chcAddMon.isSelected())
                days += "A";
            if (chcAddTue.isSelected())
                days += "B";
            if (chcAddWed.isSelected())
                days += "C";
            if (chcAddThu.isSelected())
                days += "D";
            if (chcAddFri.isSelected())
                days += "E";
            if (chcAddSat.isSelected())
                days += "F";
            if ((endDate!=null) && (days.isEmpty()))
                days = "ABCEDFG";
        } catch (Exception ignored) {}
        return new AddResult(guests, startDate, endDate, startTime, endTime, comments, days);
    }

    private record AddResult(int guests, LocalDate startDate, LocalDate endDate, int startTime, int endTime, String comments, String days) {
    }




    //Common ones

    public void selectTablePopup(LocalDate startDate, LocalDate endDate, int startTime, int endTime,int guests, String weekDays){
        RestaurantTable restaurantTable = new RestaurantTable();
        restaurantTable.checkTableAvailability(startDate,endDate,startTime,endTime,guests,weekDays);
        try {
            restaurantTable.showTableView();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showOnCheckBoxes(CheckBox chcMon,CheckBox chcTue,CheckBox chcWed,CheckBox chcThu,CheckBox chcFri,CheckBox chcSat,CheckBox chcSun) {
        try {
            chcMon.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("A"));
            chcTue.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("B"));
            chcWed.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("C"));
            chcThu.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("D"));
            chcFri.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("E"));
            chcSat.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("F"));
            chcSun.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("G"));
        } catch (Exception e) {}
    }

    private void clearFields() {
        dtpAddStartDate.cancelEdit();
        dtpAddEndDate.cancelEdit();
        cmbAddTimeTo.cancelEdit();
        cmbAddTimeFrom.cancelEdit();
        cmbAddGuests.cancelEdit();
        lblAddTableId.setText("Table* :");
        cmbUsers.getSelectionModel().clearSelection();

    }
}

