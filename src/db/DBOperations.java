package db;

import OtherClasses.Customer;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class DBOperations {

    public static boolean addNewCustomerOnDB(String fName, String lName, String username, String email, int contactNo, String password){
        String id = Customer.autoGenerateID();
        System.out.println(id);
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into user values(?,?,?,?,?,?,?)");

            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,fName);
            preparedStatement.setObject(3,lName);
            preparedStatement.setObject(4,username);
            preparedStatement.setObject(5,email);
            preparedStatement.setObject(6,contactNo);
            preparedStatement.setObject(7,password);

            int i = preparedStatement.executeUpdate();

            if (i!=0){
                return true;
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Something went wrong...");
                alert.showAndWait();
                return false;
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Something went wrong...");
            alert.showAndWait();
            return false;
        }

    }

    public static int getTableSeatingCapacity(int tableId) {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT No_of_chairs FROM tables WHERE table_ID = ?");
            preparedStatement.setObject(1, tableId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("No_of_chairs");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet getBookingsByTableID(int tableId) {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT tableID, start_date, end_date, start_time, end_time, weekDays FROM bookings WHERE tableID = ?");
            preparedStatement.setObject(1, tableId);
            return preparedStatement.executeQuery();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}