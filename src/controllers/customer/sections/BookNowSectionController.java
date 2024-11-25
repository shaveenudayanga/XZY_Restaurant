package controllers.customer.sections;

import OtherClasses.Booking;
import OtherClasses.RestaurantTable;
import controllers.customer.HomeFormController;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class BookNowSectionController extends HomeFormController implements Runnable{
    public AnchorPane paneBookTable;
    //Book Now --> One-Time
    public AnchorPane paneOneTimeReservations;
    public Button btnOneTimeReservations;
    public DatePicker dtpDateSelectOneTimeReservation;
    public ComboBox<Integer> cmbGuestsOneTimeReservation;
    public ComboBox<String> cmbTimeFromOneTimeReservation;
    public ComboBox<String> cmbTimeToOneTimeReservation;
    public TextArea txtCommentsOneTimeReservation;
    public Label lblTableIdOneTimeReservations;
    public Button btnSelectTableOneTimeReservation;
    //Book Now --> Recurring
    public AnchorPane paneRecurringReservations;
    public Button btnRecurringReservations;
    public Label lblTableIdRecurringReservations;
    public Button btnSelectTableRecurringReservation;
    public DatePicker dtpDateFromRecurringReserve;
    public DatePicker dtpDateToRecurringReserve;
    public ComboBox<String> cmbTimeFromRecurringReserve;
    public ComboBox<String> cmbTimeToRecurringReserve;
    public ComboBox<Integer> cmbGuestsRecurringReserve;
    public TextArea txtCommentsRecurring;
    public CheckBox chcMonday;
    public CheckBox chcTuesday;
    public CheckBox chcWednesday;
    public CheckBox chcThursday;
    public CheckBox chcFriday;
    public CheckBox chcSaturday;
    public CheckBox chcSunday;


    @Override
    public void run() {
        initialize();
    }
    public void initialize(){
        paneRecurringReservations.setVisible(false);
        paneOneTimeReservations.setVisible(true);
        btnOneTimeReservations.setOpacity(1);
        btnRecurringReservations.setOpacity(0.5);
        RestaurantTable.setOpenedPane(paneOneTimeReservations);
        addToComboBoxes(cmbTimeFromOneTimeReservation,cmbTimeToOneTimeReservation,cmbGuestsOneTimeReservation);
    }

    public void btnOneTimeReservationsOnAction(ActionEvent actionEvent) {
        RestaurantTable.setOpenedPane(paneOneTimeReservations);
        paneRecurringReservations.setVisible(false);
        paneOneTimeReservations.setVisible(true);
        btnOneTimeReservations.setOpacity(1);
        btnRecurringReservations.setOpacity(0.5);
    }

    public void btnRecurringReservationsOnAction(ActionEvent actionEvent) {
        RestaurantTable.setOpenedPane(paneRecurringReservations);
        paneOneTimeReservations.setVisible(false);
        paneRecurringReservations.setVisible(true);
        btnRecurringReservations.setOpacity(1);
        btnOneTimeReservations.setOpacity(0.5);
        dateInputValidation(dtpDateFromRecurringReserve,dtpDateToRecurringReserve);
        addToComboBoxes(cmbTimeFromRecurringReserve,cmbTimeToRecurringReserve,cmbGuestsRecurringReserve);
    }

    public void btnSelectTableOneTimeReservationOnAction(ActionEvent actionEvent) throws IOException {
        LocalDate reservationDate = null;
        int startTime = 0;
        int endTime = 0;
        int guests = 0;
        try {
            reservationDate = dtpDateSelectOneTimeReservation.getValue();
            startTime = Integer.parseInt(cmbTimeFromOneTimeReservation.getValue().substring(0,2));
            endTime = Integer.parseInt(cmbTimeToOneTimeReservation.getValue().substring(0,2));
            guests = cmbGuestsOneTimeReservation.getValue();
            RestaurantTable restaurantTable = new RestaurantTable();
            restaurantTable.checkTableAvailability(reservationDate,null,startTime,endTime,guests,null);
            restaurantTable.showTableView();
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Fill the Dates, Times and Guest count before selecting a Table!");
            alert.showAndWait();
        }
        lblTableIdOneTimeReservations.setText("Table ID: "+RestaurantTable.getTableId());
    }

    public void btnSelectTableRecurringReservationOnAction(ActionEvent actionEvent) throws IOException {
        try {
            LocalDate startDate = dtpDateFromRecurringReserve.getValue();
            LocalDate endDate = dtpDateToRecurringReserve.getValue();
            int startTime = Integer.parseInt(cmbTimeFromRecurringReserve.getValue().substring(0,2));
            int endTime = Integer.parseInt(cmbTimeToRecurringReserve.getValue().substring(0,2));
            int guests = cmbGuestsRecurringReserve.getValue();
            String weekDaysAsCharacters = getWeekDaysAsCharacters(chcMonday, chcTuesday, chcWednesday, chcThursday, chcFriday, chcSaturday, chcSunday);
            RestaurantTable restaurantTable = new RestaurantTable();
            restaurantTable.checkTableAvailability(startDate,endDate,startTime,endTime,guests,weekDaysAsCharacters);
            restaurantTable.showTableView();
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Fill the Dates, Times and Guest count before selecting a Table!");
            alert.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        lblTableIdRecurringReservations.setText("Table ID: "+RestaurantTable.getTableId());
    }

    public void btnConfirmBookingOneTimeReservationsOnAction(ActionEvent actionEvent) {
        if (RestaurantTable.getTableId()==0){
            Alert alert = new Alert(Alert.AlertType.ERROR,"Select a Table to Confirm...");
            alert.showAndWait();
        }
        LocalDate reservationDate = dtpDateSelectOneTimeReservation.getValue();
        int startTime = Integer.parseInt(cmbTimeFromOneTimeReservation.getValue().substring(0,2));
        int endTime = Integer.parseInt(cmbTimeToOneTimeReservation.getValue().substring(0,2));
        int guests = cmbGuestsOneTimeReservation.getValue();
        String comments = txtCommentsOneTimeReservation.getText();
        Booking booking = new Booking(reservationDate,startTime,endTime,guests,comments);
        booking.createBooking();
    }

    public void btnConfirmBookingRecurringOnAction(ActionEvent actionEvent) {
        if (RestaurantTable.getTableId()==0){
            Alert alert = new Alert(Alert.AlertType.ERROR,"Select a Table to Confirm...");
            alert.showAndWait();
        }
        LocalDate startDate = dtpDateFromRecurringReserve.getValue();
        LocalDate endDate = dtpDateToRecurringReserve.getValue();
        int startTime = Integer.parseInt(cmbTimeFromRecurringReserve.getValue().substring(0,2));
        int endTime = Integer.parseInt(cmbTimeToRecurringReserve.getValue().substring(0,2));
        int guests = cmbGuestsRecurringReserve.getValue();
        String comments = txtCommentsRecurring.getText();
        String days = getWeekDaysAsCharacters(chcMonday, chcTuesday, chcWednesday, chcThursday, chcFriday, chcSaturday, chcSunday);


        Booking booking = new Booking(startDate,endDate,startTime,endTime,guests,comments,days);
        booking.createBooking();
    }


}
