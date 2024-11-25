package controllers.admin;


import OtherClasses.Admin;
import OtherClasses.RestaurantTable;
import db.DBConnection;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class AdminFormController{
    public AnchorPane root;
    public Label lblHiAdmin;

    //Manage Customers
    public AnchorPane paneCustomers;

    //Manage Bookings
    public AnchorPane paneBookings;

    //Manage Tables
    public VBox vbTables;
    public TableView<RestaurantTable> tblTables;
    public TableColumn<RestaurantTable, Integer> clmTableIdTables;
    public TableColumn<RestaurantTable, Integer> clmNoOfChairsTables;

    //Control Panel
    public ToggleGroup toggleGroup;
    public ToggleButton toggleMailingOn;
    public ToggleButton toggleMailingOff;
    public Label lblCurrentMailerAddress;
    public TextField txtMailerEmail;
    public PasswordField pssMailerPassword;
    public Button btnChangeCredentials;
    public BorderPane paneControlPanel;


    public void initialize(){
        lblHiAdmin.setText("Hi " +Admin.admin.username());
        sectionsOpener(1);
    }


    //Menu

    public void sectionsOpener(int selectedSection){
        paneCustomers.setVisible(false);
        paneBookings.setVisible(false);
        vbTables.setVisible(false);
        paneControlPanel.setVisible(false);
        switch (selectedSection){
            case 1:
                paneCustomers.setVisible(true);
                break;
            case 2:
                paneBookings.setVisible(true);
                break;
            case 3:
                vbTables.setVisible(true);
                loadTable(RestaurantTable.getObsListTables());
                break;
            case 4:
                paneControlPanel.setVisible(true);
                loadMailerMail();
                break;
        }
    }

    public void btnCustomersOnAction(ActionEvent actionEvent) {
        sectionsOpener(1);
    }

    public void btnBookingsOnAction(ActionEvent actionEvent) {
        sectionsOpener(2);
    }

    public void btnTablesOnAction(ActionEvent actionEvent) {
        sectionsOpener(3);
    }

    public void btnControlsOnAction(ActionEvent actionEvent) {
        sectionsOpener(4);
    }

    public void btnLogOutOnAction(MouseEvent mouseEvent) {
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
        yesButton.setStyle("-fx-background-radius: 0");
        yesButton.setOnAction(e -> {
            // Perform logout action here
            System.out.println("Admin logged out.");
            popup.hide();
            Parent parent = null;
            try {
                parent = FXMLLoader.load(this.getClass().getResource("../../view/admin/AdminLoginForm.fxml"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Scene scene = new Scene(parent);
            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Admin Login");
            primaryStage.centerOnScreen();
        });

        Button noButton = new Button("Cancel");
        noButton.setStyle("-fx-background-radius: 0");
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
        popupLayout.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10;");

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


    //Manage Tables

    public void loadTable(ObservableList<RestaurantTable> restaurantTables) {
        //Reset all as it is
        tblTables.getSelectionModel().clearSelection();


        //Add table data to columns
        clmTableIdTables.setCellValueFactory(new PropertyValueFactory<>("tableIdInstanceVar"));
        clmNoOfChairsTables.setCellValueFactory(new PropertyValueFactory<>("capacityInstanceVar"));
        tblTables.setItems(restaurantTables);
    }

    public void btnReqChangeTblsOnAction(ActionEvent actionEvent) {
        // Create a label with the instruction message
        Label instructionLabel = new Label(
                "Copy the following email address and send us\nan email with an attached table map.");

        // Create a non-editable TextField with the email address
        TextField emailTextField = new TextField("snr.broz@gmail.com");
        emailTextField.setEditable(false);

        // Create a Button to copy text
        Button copyButton = new Button("Copy to Clipboard");

        // Set the action for the button
        copyButton.setOnAction(event -> {
            copyTextToClipboard(emailTextField.getText());
        });

        // Layout to hold the Label, TextField, and Button
        HBox hBox = new HBox(5,emailTextField,copyButton);
        VBox vbox = new VBox(20, instructionLabel, hBox);
        vbox.setPadding(new Insets(20,20,20,20));

        // Create the scene
        Scene scene = new Scene(vbox, 350, 120);

        // Set up the stage
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Table map update request");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void copyTextToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        content.putString(text);
        clipboard.setContent(content);
    }


    //Control Panel

    private String email;

    private void loadMailerMail() {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT mailer_email FROM program_data");
            ResultSet emails = preparedStatement.executeQuery();
            if (emails.next()){
                email = emails.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            lblCurrentMailerAddress.setText(email);
        }
    }

    public Boolean toggleMailing(){
        boolean mailingOn = true;
        try {
            if (toggleGroup.getSelectedToggle().equals(toggleMailingOff)){
                return false;
            }
        } catch (Exception e) {
            return mailingOn;
        }
        return mailingOn;
    }

    public void btnChangeCredentialsOnAction(ActionEvent actionEvent) {
        email = txtMailerEmail.getText();
        String password = pssMailerPassword.getText();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE program_data SET mailer_email = ?, mailer_password = ? WHERE mail_no = 1");
            preparedStatement.setObject(1,email);
            preparedStatement.setObject(2,password);
            int i = preparedStatement.executeUpdate();
            if (i!=0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Update Successful");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            loadMailerMail();
            txtMailerEmail.clear();
            pssMailerPassword.clear();
        }
    }

}
