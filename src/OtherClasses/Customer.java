package OtherClasses;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.*;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public record Customer(
        String userId,
        String firstName,
        String lastName,
        String username,
        String email,
        Integer phoneNo) {
    private static ResultSet customerResultSet;
    public static Customer user;

    //Default Constructor

    public Customer()
    {
        this("", "", "", "", "", 0);
    }


    //Methods

    public static void getCustomerDataById(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where id = ?");
            preparedStatement.setObject(1,user.userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                customerResultSet = resultSet;
                Customer.user = new Customer(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        Integer.parseInt(resultSet.getString(6)));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user details from the database");
        }
    }

    public static ObservableList<Customer> getObsListCustomers(){
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        customers.clear();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String userId = resultSet.getString(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String username = resultSet.getString(4);
                String email = resultSet.getString(5);
                Integer phoneNo = resultSet.getInt(6);
                Customer customer = new Customer(userId, firstName, lastName, username, email, phoneNo);
                customers.add(customer);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }

    public static void deleteCustomer(String customerId){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to delete?", ButtonType.YES,ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult().equals(ButtonType.YES)) {
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from user where id = ?");
                preparedStatement.setObject(1,customerId);
                int i = preparedStatement.executeUpdate();
                if (i == 0){
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION,"Something went wrong...");
                    alert1.showAndWait();
                }
            } catch (SQLException e) {
                System.out.println("An error occurs when deleting from the database");
            }
        }else {
            return;
        }
    }

    public void updateCustomer(String customerId,String firstName, String lastName, String username, String email, int phoneNo){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE user SET first_name = ?, last_name = ?, username = ?, email = ?, contact_no = ? WHERE id = ?");
            preparedStatement.setObject(1,firstName);
            preparedStatement.setObject(2,lastName);
            preparedStatement.setObject(3,username);
            preparedStatement.setObject(4,email);
            preparedStatement.setObject(5,phoneNo);
            preparedStatement.setObject(6,customerId);
            int i = preparedStatement.executeUpdate();
            if (i!=0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Successfully Updated!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Something went wrong...");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.out.println("An error occurs when updating data of the database");
        }
        System.out.println(customerId);
        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(username);
        System.out.println(email);
        System.out.println(phoneNo);
    }

    public static String autoGenerateID() {
        String userId = "";
        Connection connection =DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");
            boolean isExist = resultSet.next();
            if (isExist){
                String oldId = resultSet.getString(1);
                oldId = oldId.substring(1);
                int intID = Integer.parseInt(oldId);
                intID += 1;
                if (intID < 10)
                    userId = "U000" + intID;
                else if (intID < 100)
                    userId = "U00" + intID;
                else if (intID < 1000)
                    userId = "U0" + intID;
                else
                    userId = "U" + intID;
            }
            else {
                userId = "U0001";
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userId;
    }


    /*Custom getters:
    Record accessors don't follow standard JavaBean property naming conventions, which is what the PropertyValueFactory expects.
    For example, a record uses userId() rather than getUserId() as an accessor,
    which makes it incompatible with the PropertyValueFactory.
     */

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getPhoneNo() {
        return phoneNo;
    }

    @Override
    public String toString() {
        return userId + ": " + firstName + " " + lastName;
    }
}