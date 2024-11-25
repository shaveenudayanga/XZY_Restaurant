package controllers.customer.sections;

import OtherClasses.Booking;
import OtherClasses.RestaurantTable;
import controllers.customer.HomeFormController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Window;

import java.time.LocalDate;
import java.util.Map;

import static javafx.geometry.NodeOrientation.RIGHT_TO_LEFT;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class YourBookingsSectionController extends HomeFormController{
    public AnchorPane paneYourBookings;
    public TableView<Booking> tblBookings;
    public TableColumn<Booking,Integer> clmTableId;
    public TableColumn<Booking,Integer> clmGuests;
    public TableColumn<Booking, LocalDate> clmStartDate;
    public TableColumn<Booking,LocalDate> clmEndDate;
    public TableColumn<Booking, String> clmDaysOfWeek;
    public TableColumn<Booking,Integer> clmTimeFrom;
    public TableColumn<Booking,Integer> clmTimeTo;
    public HBox btnsUpdateDeleteBooking;
    public ComboBox<Integer> cmbTableIds = new ComboBox<Integer>();
    public ComboBox<Integer> cmbGusts = new ComboBox<Integer>();
    public ComboBox<String> cmbStartTime = new ComboBox<String>();
    public ComboBox<String> cmbEndTime = new ComboBox<String>();
    Booking booking = new Booking();


    public void initialize(){

        btnsUpdateDeleteBooking.setDisable(true);
        tblBookings.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Booking>() {
            @Override
            public void changed(ObservableValue<? extends Booking> observableValue, Booking booking, Booking t1) {
                btnsUpdateDeleteBooking.setDisable(false);
            }
        });
        Thread thread = new Thread(() -> {
            try {
                loadTable(booking.getObsListBookings(this));
            } catch (Exception e) {
                System.out.println("Can't Load Table");
            }
        });
        thread.start();
    }

    public void btnUpdateBookingOnAction(ActionEvent actionEvent) {
        Thread thread = new Thread(() -> {
            Platform.runLater(() -> {
                openUpdateDetailsPopup();
            });
        });
        thread.start();
        tblBookings.refresh();
    }

    public void btnDeleteBookingOnAction(ActionEvent actionEvent) {
        tblBookings.getSelectionModel().getSelectedItem().deleteBooking();
        loadTable(booking.getObsListBookings(this));
    }

    public void loadTable(ObservableList<Booking> bookings) {

        clmTableId.setCellValueFactory(new PropertyValueFactory<Booking, Integer>("tableId"));
        clmGuests.setCellValueFactory(new PropertyValueFactory<Booking, Integer>("noOfGuests"));
        clmStartDate.setCellValueFactory(new PropertyValueFactory<Booking,LocalDate>("startDate"));
        clmEndDate.setCellValueFactory(new PropertyValueFactory<Booking,LocalDate>("endDate"));
        clmDaysOfWeek.setCellValueFactory(new PropertyValueFactory<Booking, String>("weekDaysExpanded"));
        clmTimeFrom.setCellValueFactory(new PropertyValueFactory<Booking, Integer>("startTime"));
        clmTimeTo.setCellValueFactory(new PropertyValueFactory<Booking, Integer>("endTime"));

        tblBookings.setItems(bookings);
        tblBookings.refresh();
    }

    public void openUpdateDetailsPopup(){
        Window parentStage = paneYourBookings.getScene().getWindow();
        // Check if popup is already created and showing
        if (parentStage.getUserData() instanceof Popup && ((Popup) parentStage.getUserData()).isShowing()) {
            return;
        }

        // Create the popup

        Popup popup = new Popup();

        // Save popup to user data to check later
        parentStage.setUserData(popup);

        // Title(#1 in VBox)
        Label titleLabel = new Label("Update Booking Details");
        titleLabel.setFont(new Font("Arial Rounded MT Bold", 20));

        // text(#2 in VBox)
        Label bookingId = new Label("Booking ID: " + tblBookings.getSelectionModel().getSelectedItem().getBookingId());
        bookingId.setFont(new Font("Varela Round", 14));
        // text(#3 in VBox)
        Label bookingDate = new Label("Date of placed booking: " + tblBookings.getSelectionModel().getSelectedItem().getBookingDate());
        bookingDate.setFont(new Font("Varela Round", 14));

        //GridPane(#4 in VBox)
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10,15,10,10));
        gridPane.setVgap(8);
        gridPane.setHgap(20);

        //editGuests
        Label lblGuests = new Label("No of Guests: ");
        GridPane.setConstraints(lblGuests,0,0);
        cmbGusts.setValue(tblBookings.getSelectionModel().getSelectedItem().getNoOfGuests());
        GridPane.setConstraints(cmbGusts,1,0);
        //editStartDate
        Label lblStartDate = new Label("Date from: ");
        GridPane.setConstraints(lblStartDate,0,1);
        DatePicker dtpStartDate = new DatePicker(LocalDate.parse(""+tblBookings.getSelectionModel().getSelectedItem().getStartDate()));
        GridPane.setConstraints(dtpStartDate,1,1);

        //editEndDate
        Label lblEndDate = new Label("Date to: ");
        GridPane.setConstraints(lblEndDate,0,2);
        DatePicker dtpEndDate = new DatePicker();

        try {
            dtpEndDate.setValue(LocalDate.parse(""+tblBookings.getSelectionModel().getSelectedItem().getEndDate()));
        } catch (Exception e) {}

        GridPane.setConstraints(dtpEndDate,1,2);
        //Edit Start Time
        Label lblStartTime = new Label("Time from: ");
        GridPane.setConstraints(lblStartTime,0,3);
        if (tblBookings.getSelectionModel().getSelectedItem().getStartTime()>9) {
            cmbStartTime.setValue(tblBookings.getSelectionModel().getSelectedItem().getStartTime()+":00");
        } else {
            cmbStartTime.setValue("0"+tblBookings.getSelectionModel().getSelectedItem().getStartTime()+":00");
        }
        GridPane.setConstraints(cmbStartTime,1,3);


        //Edit End Time
        Label lblEndTime = new Label("Time to: ");
        GridPane.setConstraints(lblEndTime,0,4);
        if (tblBookings.getSelectionModel().getSelectedItem().getEndTime()>9) {
            cmbEndTime.setValue(tblBookings.getSelectionModel().getSelectedItem().getEndTime()+":00");
        } else {
            cmbEndTime.setValue("0"+tblBookings.getSelectionModel().getSelectedItem().getEndTime()+":00");
        }
        GridPane.setConstraints(cmbEndTime,1,4);


        //editTableID
        Label lblTable = new Label("Table ID: ");
        GridPane.setConstraints(lblTable,0,5);
        cmbTableIds.setValue(tblBookings.getSelectionModel().getSelectedItem().getTableId());
        GridPane.setConstraints(cmbTableIds,1,5);

        //Add to grid pane
        gridPane.getChildren().addAll(lblGuests,cmbGusts,lblStartDate,dtpStartDate,lblEndDate,dtpEndDate,lblStartTime,cmbStartTime,lblEndTime,cmbEndTime,lblTable,cmbTableIds);

        //Validate date selection
        dateInputValidation(dtpStartDate,dtpEndDate);
        addToComboBoxes(cmbStartTime,cmbEndTime,cmbGusts);

        //editWeekDays(#5 in VBox)
        CheckBox chcMonday = new CheckBox("Mon");
        chcMonday.setNodeOrientation(RIGHT_TO_LEFT);
        CheckBox chcTuesday = new CheckBox("| Tue");
        chcTuesday.setNodeOrientation(RIGHT_TO_LEFT);
        CheckBox chcWednesday = new CheckBox("| Wed");
        chcWednesday.setNodeOrientation(RIGHT_TO_LEFT);
        CheckBox chcThursday = new CheckBox("| Thu");
        chcThursday.setNodeOrientation(RIGHT_TO_LEFT);
        CheckBox chcFriday = new CheckBox("| Fri");
        chcFriday.setNodeOrientation(RIGHT_TO_LEFT);
        CheckBox chcSaturday = new CheckBox("| Sat");
        chcSaturday.setNodeOrientation(RIGHT_TO_LEFT);
        CheckBox chcSunday = new CheckBox("| Sun");
        chcSunday.setNodeOrientation(RIGHT_TO_LEFT);
        try {
            chcMonday.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("A"));
            chcTuesday.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("B"));
            chcWednesday.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("C"));
            chcThursday.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("D"));
            chcFriday.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("E"));
            chcSaturday.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("F"));
            chcSaturday.setSelected(tblBookings.getSelectionModel().getSelectedItem().getWeekDaysInLetters().contains("G"));
        } catch (Exception e) {}
        HBox hBox1 = new HBox(2,chcMonday,chcTuesday,chcWednesday,chcThursday,chcFriday,chcSaturday,chcSunday);
        if (dtpEndDate.getValue() == null) {
            hBox1.setDisable(true);
        }
        dtpEndDate.setOnAction(actionEvent -> {
            hBox1.setDisable(false);
        });

        cmbTableIds.setOnMouseClicked(e -> {
            RestaurantTable restaurantTable = new RestaurantTable();
            int sTime;
            int eTime;
            try {
                sTime = Integer.parseInt(cmbStartTime.getValue().substring(0,2));
                eTime = Integer.parseInt(cmbEndTime.getValue().substring(0,2));
            } catch (NumberFormatException ex) {
                sTime = Integer.parseInt(cmbStartTime.getValue().substring(0,1));
                eTime = Integer.parseInt(cmbEndTime.getValue().substring(0,1));
            }
            restaurantTable.checkTableAvailability(
                    dtpStartDate.getValue(),
                    dtpEndDate.getValue(),
                    sTime, eTime,
                    cmbGusts.getValue(),
                    getWeekDaysAsCharacters(chcMonday,chcTuesday,chcWednesday,chcThursday,chcFriday,chcSaturday,chcSunday));
            Map<Integer, Boolean> availability = RestaurantTable.getTableAvailability();
            cmbTableIds.getItems().clear();
            int i=0;
            for (Map.Entry<Integer, Boolean> entry : availability.entrySet()) {
                if (entry.getValue()) {
                    cmbTableIds.getItems().add(i,entry.getKey());
                    i++;
                }
            }
        });

        //Comments(#6 in VBox)
        TextArea txtComments = new TextArea(tblBookings.getSelectionModel().getSelectedItem().getComments());
        txtComments.setPrefSize(100,60);

        // Update and Cancel buttons
        Button btnUpdate = new Button("Update");
        btnUpdate.setStyle("-fx-background-color: green");
        btnUpdate.setOnAction(e -> {
            int tableId = cmbTableIds.getValue();
            int guests = cmbGusts.getValue();
            LocalDate startDate = dtpStartDate.getValue();
            LocalDate endDate = null;
            int startTime = 0;
            try {
                startTime = Integer.parseInt(cmbStartTime.getValue().substring(0,2));
            } catch (NumberFormatException ex) {
                startTime = Integer.parseInt(cmbStartTime.getValue().substring(0,1));
            }
            int endTime = 0;
            try {
                endTime = Integer.parseInt(cmbEndTime.getValue().substring(0,2));
            } catch (NumberFormatException ex) {
                endTime = Integer.parseInt(cmbEndTime.getValue().substring(0,1));
            }
            try {
                endDate = dtpEndDate.getValue();
            } catch (NullPointerException ex) {}
            String comments = txtComments.getText();
            String days = "";
            if (endDate==null) {
                days = getWeekDaysAsCharacters(chcMonday,chcTuesday,chcWednesday,chcThursday,chcFriday,chcSaturday,chcSunday);
            }
            System.out.println("Details Updated");
            popup.hide();
            tblBookings.getSelectionModel().selectedItemProperty().getValue().updateBooking(tableId, guests, startDate, endDate, startTime, endTime, days, comments);
            loadTable(tblBookings.getSelectionModel().getSelectedItem().getObsListBookings(this));
            btnsUpdateDeleteBooking.setDisable(true);
        });

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: aqua");
        btnCancel.setOnAction(e -> {
            popup.hide();
            parentStage.setUserData(null); // Clear the popup reference
        });

        //HBox(7 in VBox)
        HBox hBox2 = new HBox(10,btnUpdate,btnCancel);
        hBox2.setAlignment(Pos.CENTER);

        // Layout for the popup
        VBox popupLayout = new VBox(10,titleLabel,bookingId,bookingDate,gridPane,hBox1,txtComments,hBox2);
        popupLayout.setPadding(new Insets(10));
        popupLayout.setAlignment(Pos.CENTER);
        popupLayout.setStyle("-fx-background-color: rgba(200, 200, 200, 0.9); -fx-border-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Add layout to popup
        popup.getContent().add(popupLayout);

        // Disable the main window when popup is shown
        popup.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                parentStage.getScene().getRoot().setDisable(true);
            } else {
                parentStage.getScene().getRoot().setDisable(false);
                parentStage.setUserData(null); // Clear the popup reference
            }
        });

        // Show popup
        popup.show(parentStage);
    }
}
