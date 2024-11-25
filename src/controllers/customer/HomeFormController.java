package controllers.customer;

import OtherClasses.Customer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;


/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class HomeFormController{
    public AnchorPane root;
    private static AnchorPane openedPane = null;
    public AnchorPane paneCenter;

    //Menu Items
    public ImageView imgMenu;
    public AnchorPane menuPane;
    public AnchorPane paneFade;

    //Dashboard
    public GridPane paneDashboard;
    public ImageView imgDp;
    public Label lblHiUser;
    public VBox tilePane1;
    public ImageView tileImg1;
    public StackPane tilePane2;
    public ImageView tileImg2;
    public VBox tilePane3;
    public ImageView tileImg3;

    //Book Now
    public AnchorPane paneBookNow;

    //Your Bookings
    public AnchorPane paneYourBookings;

    //User Info
    public GridPane paneUserInfo;


    public void initialize(){
        openDashboardPane();
        closeMenu();
        tileAnimation(tilePane1,tileImg1);
        tileAnimation(tilePane2,tileImg2);
        tileAnimation(tilePane3,tileImg3);

        paneCenter.setStyle("-fx-background-image: url('@../../images/restaurant_cafe_design_39235_1920x1080.jpg');-fx-background-size: 100% 100%; -fx-background-position: center; ");

    }

    private void tileAnimation(Pane tilePane, ImageView tileImg) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(0.3), tileImg);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(0.3), tileImg);
        scaleDown.setToX(1);
        scaleDown.setToY(1);


        tilePane.setOnMouseEntered(event -> scaleUp.playFromStart());
        tilePane.setOnMouseExited(event -> scaleDown.playFromStart());
    }

    public void addToComboBoxes(ComboBox<String> timeFrom, ComboBox<String> timeTo, ComboBox<Integer> guestsCount){
        Integer[] guests = {1,2,3,4,5,6,7,8};
        guestsCount.getItems().addAll(guests);
        String[] hours = {
                "08:00","09:00","10:00","11:00","12:00","13:00","14:00",
                "15:00","16:00","17:00","18:00","19:00","20:00"};
        timeFrom.getItems().addAll(hours);
        timeInputValidation(hours,timeFrom,timeTo);
    }

    public void timeInputValidation(String[] hours, ComboBox<String> cmbTimeFrom, ComboBox<String> cmbTimeTo){
        cmbTimeFrom.setOnAction(e -> {
            cmbTimeTo.setValue(null);
        });
        cmbTimeTo.setOnMouseClicked(e -> {
            String startTime = cmbTimeFrom.getValue();
            if (startTime != null) {
                cmbTimeTo.getItems().setAll(getAvailableEndTimes(startTime, hours));
            } else {
                cmbTimeTo.getItems().setAll(hours);
            }
        });
    }

    private String[] getAvailableEndTimes(String startTime, String[] hours) {
        int startIndex = 0;
        for (int i = 0; i < hours.length; i++) {
            if (hours[i].equals(startTime)) {
                startIndex = i+1;
                break;
            }
        }
        String[] endTimes = new String[hours.length - startIndex];
        System.arraycopy(hours, startIndex, endTimes, 0, endTimes.length);
        return endTimes;
    }

    public void dateInputValidation(DatePicker dtpDateFrom, DatePicker dtpDateTo) {
        dtpDateFrom.setOnAction(e ->{
            dtpDateTo.setValue(null);
        });
        dtpDateTo.setOnMouseEntered(e ->{
            LocalDate startDate = dtpDateFrom.getValue();
            if (startDate != null) {
                dtpDateTo.setDayCellFactory(getDayCellFactory(startDate));
            } else {
                dtpDateTo.setDayCellFactory(null);
            }
        });
    }

    private Callback<DatePicker, DateCell> getDayCellFactory(LocalDate startDate) {
        return datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(startDate.plusDays(1))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Optional: change the color to indicate disabled dates
                }
            }
        };
    }

    protected String getWeekDaysAsCharacters(CheckBox chcMonday, CheckBox chcTuesday, CheckBox chcWednesday, CheckBox chcThursday, CheckBox chcFriday, CheckBox chcSaturday, CheckBox chcSunday) {
        String days = "";
        if (chcMonday.isSelected())
            days += "A";
        if (chcTuesday.isSelected())
            days += "B";
        if (chcWednesday.isSelected())
            days += "C";
        if (chcThursday.isSelected())
            days += "D";
        if (chcFriday.isSelected())
            days += "E";
        if (chcSaturday.isSelected())
            days += "F";
        if (chcSunday.isSelected())
            days += "G";
        if (days.isEmpty())
            days = "ABCDEFG";
        return days;
    }


    //Menu

    public void imgMenuOnMouseClicked(MouseEvent mouseEvent) {

        if (menuPane.getTranslateX()==(-179-44)) {
            openMenu();
        } else {
            closeMenu();
        }
    }

    public void openMenu(){
        paneFade.setDisable(false);
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(menuPane);
        slide.setToX(0);
        slide.play();
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.seconds(0.6));
        fadeTransition.setNode(menuPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(0.8);
        fadeTransition.play();
        FadeTransition fadeTransition1 = new FadeTransition();
        fadeTransition1.setDuration(Duration.seconds(0.6));
        fadeTransition1.setNode(paneFade);
        fadeTransition1.setFromValue(0);
        fadeTransition1.setToValue(0.8);
        fadeTransition1.play();
        paneFade.setOnMouseClicked(e ->{
            closeMenu();
        });
    }

    public void closeMenu(){
        paneFade.setDisable(true);
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(menuPane);
        slide.setToX(-179-44);
        slide.play();
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.seconds(0.2));
        fadeTransition.setNode(menuPane);
        fadeTransition.setFromValue(0.8);
        fadeTransition.setToValue(0.2);
        fadeTransition.play();
        FadeTransition fadeTransition1 = new FadeTransition();
        fadeTransition1.setDuration(Duration.seconds(0.6));
        fadeTransition1.setNode(paneFade);
        fadeTransition1.setFromValue(0.8);
        fadeTransition1.setToValue(0);
        fadeTransition1.play();
    }

    public void btnDashboardOnAction(ActionEvent actionEvent) {
        openDashboardPane();
    }

    public void btnBookNowOnAction(ActionEvent actionEvent) {
        openBookNowPane();
    }

    public void btnYourBookingsOnAction(ActionEvent actionEvent) {
        openYourBookingsPane();
    }

    public void btnUserOnAction(ActionEvent actionEvent) {
        openUserPane();
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) {
        logOut();
    }

    public void imgDashboardOnMouseClicked(MouseEvent mouseEvent) {
        openDashboardPane();
    }

    public void imgBookNowOnMouseClicked(MouseEvent mouseEvent) {
        openBookNowPane();
    }

    public void imgYourBookingsOnMouseClicked(MouseEvent mouseEvent) {
        openYourBookingsPane();
    }

    public void imgUserOnMouseClicked(MouseEvent mouseEvent) {
        openUserPane();
    }

    public void imgLogOutOnMouseClicked(MouseEvent mouseEvent) {
        logOut();
    }

    public void sectionsOpener(int selectedSection){
        paneDashboard.setVisible(false);
        paneBookNow.setVisible(false);
        paneYourBookings.setVisible(false);
        paneUserInfo.setVisible(false);
        switch (selectedSection){
            case 1:
                paneDashboard.setVisible(true);
                break;
            case 2:
                paneBookNow.setVisible(true);
                break;
            case 3:
                paneYourBookings.setVisible(true);
                break;
            case 4:
                paneUserInfo.setVisible(true);
                break;
        }
    }


    //Dashboard

    public void openDashboardPane(){
        sectionsOpener(1);
        closeMenu();
        try {
            lblHiUser.setText("Hello "+Customer.user.firstName()+"!");
        } catch (Exception e) {
        }
        Circle clip = new Circle(imgDp.getFitWidth() / 2, imgDp.getFitHeight() / 2, imgDp.getFitWidth() / 2);
        imgDp.setClip(clip);
    }

    public void loadDp(Image imgDp){
        Circle clip = new Circle(this.imgDp.getFitWidth() / 2, this.imgDp.getFitHeight() / 2, this.imgDp.getFitWidth() / 2);
        this.imgDp.setClip(clip);
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(new File(imgDp.getUrl().substring(5)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Desktop is not supported.");
        }
        this.imgDp.setImage(imgDp);
    }


    //Book Now

    public void openBookNowPane(){
        sectionsOpener(2);
        closeMenu();
    }


//    Your Bookings

    public void openYourBookingsPane(){
        sectionsOpener(3);
        closeMenu();

    }


    //User Info

    public void openUserPane(){
        sectionsOpener(4);
        closeMenu();
    }


    //Log out
    public void logOut(){
        Window parentStage = root.getScene().getWindow();
        // Check if popup is already created and showing
        if (parentStage.getUserData() instanceof Popup && ((Popup) parentStage.getUserData()).isShowing()) {
            return;
        }

        // Create the popup
        Popup popup = new Popup();

        // Save popup to user data to check later
        parentStage.setUserData(popup);

        // Title
        Label titleLabel = new Label("Log out");
        titleLabel.setFont(new Font("Arial Rounded MT Bold", 20));

        // Confirmation text
        Label confirmationText = new Label("You will return to the login screen");
        confirmationText.setFont(new Font("Varela Round", 14));

        // Logout and Cancel buttons
        Button yesButton = new Button("Log out");
        yesButton.setOnAction(e -> {
            // Perform logout action here
            System.out.println("User logged out.");
            popup.hide();
            try {
                Parent parent = FXMLLoader.load(this.getClass().getResource("../../view/customer/LoginForm.fxml"));
                Scene scene = new Scene(parent);
                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Login");
                primaryStage.centerOnScreen();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button noButton = new Button("Cancel");
        noButton.setOnAction(e -> {
            popup.hide();
            parentStage.setUserData(null); // Clear the popup reference
        });

        HBox buttonBox = new HBox(10, yesButton, noButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Layout for the popup
        VBox popupLayout = new VBox(20, titleLabel, confirmationText, buttonBox);
        popupLayout.setPadding(new Insets(20));
        popupLayout.setAlignment(Pos.CENTER);
        popupLayout.setStyle("-fx-background-color: rgba(200,200,200,0.9) ; -fx-border-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10;");

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

    //getter and setter

    public static void setOpenedPane(AnchorPane openedPane) {
        HomeFormController.openedPane = openedPane;
    }
}
