package controllers.customer;

import db.DBOperations;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class CreateNewAccountFormController {
    public AnchorPane root;
    public TextField txtFirstName;
    public TextField txtLastName;
    public TextField txtUsername;
    public TextField txtEmail;
    public PasswordField pssPassword;
    public PasswordField pssConfirmPassword;
    public TextField txtContactNo;
    public Button btnRegister;
    public Label lblUsernameCannotBeEmpty;
    public Label lblEmailCannotBeEmpty;
    public Label lblWrongEmailFormat;
    public Label lblPasswordDoesNotMatch1;
    public Label lblPasswordDoesNotMatch2;
    public Label lblContactNoCannotBeEmpty;
    public Label lblFirstNameCannotBeEmpty;


    public void initialize(){
        txtFirstName.requestFocus();
        setBorderColorRequiredFields("transparent");
        allLblVisibility(false);
        passwordLblVisibility(false);
        lblWrongEmailFormat.setVisible(false);
    }

    public void pssConfirmPasswordOnAction(ActionEvent actionEvent) {
        register();
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        register();
    }

    public boolean requiredFieldIsBlank(){
        boolean fNameBlank = txtFirstName.getText().isBlank();
        boolean uNameBlank = txtUsername.getText().isBlank();
        boolean emailBlank = txtEmail.getText().isBlank();
        boolean contactNoBlank = txtContactNo.getText().isBlank();
        boolean pwBlank = pssPassword.getText().isBlank();
        return (fNameBlank||uNameBlank||emailBlank||contactNoBlank||pwBlank);
    }

    public void register(){
        //check if First name field is blank
        if (!txtFirstName.getText().isBlank()){
            txtFirstName.setStyle("-fx-border-color: transparent");
            lblFirstNameCannotBeEmpty.setVisible(false);
            String fName = txtFirstName.getText();
            String lName = "";
            lName = txtLastName.getText();
            //check if username field is blank
            if (!txtUsername.getText().isBlank()){
                txtUsername.setStyle("-fx-border-color: transparent");
                lblUsernameCannotBeEmpty.setVisible(false);
                String username = txtUsername.getText();
                //check if email field is blank
                if (!(txtEmail.getText().isBlank())){
                    txtEmail.setStyle("-fx-border-color: transparent");
                    lblEmailCannotBeEmpty.setVisible(false);
                    //check if email format is correct
                    if (txtEmail.getText().contains("@")){
                        txtEmail.setStyle("-fx-border-color: transparent");
                        lblWrongEmailFormat.setVisible(false);
                        String email = txtEmail.getText();
                        //check if contact no field is blank
                        if (!txtContactNo.getText().isBlank()){
                            txtContactNo.setStyle("-fx-border-color: transparent");
                            lblContactNoCannotBeEmpty.setVisible(false);
                            int contactNo = Integer.parseInt(txtContactNo.getText());
                            //check if password fields is blank
                            if (!(pssPassword.getText().isBlank()||pssConfirmPassword.getText().isBlank())){
                                setBorderColorPWFields("transparent");
                                //check if passwords are matched
                                String newPassword = pssPassword.getText();
                                String confirmPassword = pssConfirmPassword.getText();
                                if (newPassword.equals(confirmPassword)){
                                    setBorderColorPWFields("trnsparent");
                                    passwordLblVisibility(false);
                                    //genarate a id for the user
                                    //pass data to the method which is pass user data to database
                                    boolean customerAdded = DBOperations.addNewCustomerOnDB(fName, lName, username, email, contactNo, confirmPassword);
                                    if (customerAdded){
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Success!");
                                        alert.showAndWait();
                                        Parent parent = null;
                                        try {
                                            parent = FXMLLoader.load(this.getClass().getResource("../../view/customer/LoginForm.fxml"));
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        Scene scene = new Scene(parent);
                                        Stage primaryStage = (Stage) root.getScene().getWindow();
                                        primaryStage.setScene(scene);
                                        primaryStage.setTitle("Login");
                                        primaryStage.centerOnScreen();
                                    }
                                } else {
                                    pssPassword.requestFocus();
                                    passwordLblVisibility(true);
                                    setBorderColorPWFields("red");
                                }
                            } else {
                                setBorderColorPWFields("red");
                            }
                        } else {
                            txtContactNo.requestFocus();
                            txtContactNo.setStyle("-fx-border-color: red ; -fx-border-radius: 3");
                            lblContactNoCannotBeEmpty.setVisible(true);
                        }
                    }else {
                        txtEmail.requestFocus();
                        txtEmail.setStyle("-fx-border-color: red ; -fx-border-radius: 3");
                        lblWrongEmailFormat.setVisible(true);
                    }
                }else {
                    txtEmail.requestFocus();
                    txtEmail.setStyle("-fx-border-color: red ; -fx-border-radius: 3");
                    lblEmailCannotBeEmpty.setVisible(true);
                }
            }else {
                txtUsername.requestFocus();
                txtUsername.setStyle("-fx-border-color: red ; -fx-border-radius: 3");
                lblUsernameCannotBeEmpty.setVisible(true);
            }

        } else {
            txtFirstName.requestFocus();
            txtFirstName.setStyle("-fx-border-color: red ; -fx-border-radius: 3");
            lblFirstNameCannotBeEmpty.setVisible(true);
        }
    }


    public void lblLoginToExistingAccountOnMouseClick(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../../view/customer/LoginForm.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.centerOnScreen();
    }

    public void setBorderColorPWFields(String color){
        pssPassword.setStyle("-fx-border-color: " + color + " ; -fx-border-radius: 3");
        pssConfirmPassword.setStyle("-fx-border-color: " + color + " ; -fx-border-radius: 3");
    }

    public void allLblVisibility(boolean isVisible){
        lblFirstNameCannotBeEmpty.setVisible(isVisible);
        lblUsernameCannotBeEmpty.setVisible(isVisible);
        lblEmailCannotBeEmpty.setVisible(isVisible);
        lblContactNoCannotBeEmpty.setVisible(isVisible);
    }

    public void passwordLblVisibility(boolean isVisible){
        lblPasswordDoesNotMatch1.setVisible(isVisible);
        lblPasswordDoesNotMatch2.setVisible(isVisible);
    }

    public void setBorderColorRequiredFields(String color){
        txtFirstName.setStyle("-fx-border-color: " + color + "; -fx-border-radius: 5");
        txtUsername.setStyle("-fx-border-color: " + color + "; -fx-border-radius: 5");
        txtEmail.setStyle("-fx-border-color: " + color + "; -fx-border-radius: 5");
        txtContactNo.setStyle("-fx-border-color: " + color + "; -fx-border-radius: 5");
        pssPassword.setStyle("-fx-border-color: " + color + "; -fx-border-radius: 5");
        pssConfirmPassword.setStyle("-fx-border-color: " + color + "; -fx-border-radius: 5");
    }
}
