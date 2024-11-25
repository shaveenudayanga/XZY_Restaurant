package controllers.admin;

import OtherClasses.Admin;
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

public class AdminLoginFormController {
    public TextField txtUsername;
    public PasswordField pssPassword;
    public AnchorPane root;
    public Label txtIncorrectLoginDetails;

    public void txtUsernameOnAction(ActionEvent actionEvent) {
        if (txtUsername.getText().isEmpty()) {
            txtUsername.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
            txtUsername.requestFocus();
        } else {
            txtUsername.setStyle("-fx-border-color: transparent; -fx-border-radius: 3;");
            pssPassword.requestFocus();
        }
    }

    public void pssPasswordOnAction(ActionEvent actionEvent) {
        txtFieldConditions();
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        txtFieldConditions();
    }

    private void txtFieldConditions() {
        if (txtUsername.getText().isEmpty()&&pssPassword.getText().isEmpty()){
            txtUsername.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
            pssPassword.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
            txtUsername.requestFocus();
        } else {
            if (txtUsername.getText().isEmpty()){
                txtUsername.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
                txtUsername.requestFocus();
            }
            else {
                txtUsername.setStyle("-fx-border-color: transparent; -fx-border-radius: 3;");
                if (pssPassword.getText().isEmpty()){
                    pssPassword.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
                    pssPassword.requestFocus();
                }
                else {
                    pssPassword.setStyle("-fx-border-color: transparent; -fx-border-radius: 3;");
                    login();
                }
            }
        }
    }

    public void login(){
        String username = txtUsername.getText();
        String password = pssPassword.getText();
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from admin where username = ? and password = ?");
            preparedStatement.setObject(1,username);
            preparedStatement.setObject(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){

                Admin admn = new Admin(resultSet.getInt(1),resultSet.getString(2));
                System.out.println(resultSet.getString(2));
                Admin.admin = admn;
                Parent parent = FXMLLoader.load(this.getClass().getResource("../../view/admin/AdminForm.fxml"));
                Scene scene = new Scene(parent);
                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Admin management");
                primaryStage.centerOnScreen();
                System.out.println("Hi "+username);
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

    public void lblToUserLoginOnMouseClick(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../../view/customer/LoginForm.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.centerOnScreen();
    }
}
