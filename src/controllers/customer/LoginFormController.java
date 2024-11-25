package controllers.customer;

import OtherClasses.Customer;
import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class LoginFormController{
    public AnchorPane root;
    public TextField txtUsername;
    public PasswordField pssPassword;
    public Label txtIncorrectLoginDetails;


    public void txtUsernameOnAction(ActionEvent actionEvent) {
        pssPassword.requestFocus();
    }

    public void pssPasswordOnAction(ActionEvent actionEvent) {
        login();
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        login();
    }

    public void login(){

        String username = txtUsername.getText();
        String email = txtUsername.getText();
        String password = pssPassword.getText();
        ResultSet resultSet = null;

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement("select * from user where username = ? and password = ?");
            preparedStatement1.setObject(1,username);
            preparedStatement1.setObject(2,password);
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            boolean correctWithUsername = resultSet1.next();
            PreparedStatement preparedStatement2 = connection.prepareStatement("select * from user where email = ? and password = ?");
            preparedStatement2.setObject(1,email);
            preparedStatement2.setObject(2,password);
            ResultSet resultSet2 = preparedStatement2.executeQuery();
            boolean correctWithEmail = resultSet2.next();

            if (correctWithUsername||correctWithEmail){
                if (correctWithUsername){
                    resultSet = resultSet1;
                } else {
                    resultSet = resultSet2;
                }
                Customer.user = new Customer(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        Integer.parseInt(resultSet.getString(6)));
                Customer.getCustomerDataById();
                Parent parent = FXMLLoader.load(this.getClass().getResource("../../view/customer/HomeForm.fxml"));
                Scene scene = new Scene(parent);
                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("XYZ Restaurent");
                primaryStage.centerOnScreen();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Invalid login details...");
                alert.showAndWait();
                txtUsername.clear();
                pssPassword.clear();
                txtUsername.requestFocus();
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void lblCreateNewAccountOnMouseClick(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../../view/customer/CreateNewAccountForm.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Create New Account");
        primaryStage.centerOnScreen();
    }

}
