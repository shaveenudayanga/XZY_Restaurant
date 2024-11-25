package controllers.admin;

import OtherClasses.Customer;
import db.DBOperations;
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
import javafx.scene.layout.VBox;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class ManageCustomersFormController {
    public AnchorPane paneManageCustomers;
    public VBox vbxMain;
    public TextField txtKeyword;
    public TableView<Customer> tblCustomers;
    public TableColumn<Customer,String> clmCustomerId;
    public TableColumn<Customer,String> clmFirstName;
    public TableColumn<Customer,String> clmLastName;
    public TableColumn<Customer,String> clmUserName;
    public TableColumn<Customer,String> clmEmail;
    public TableColumn<Customer,Integer> clmContactNo;
    public Button btnUpdate;
    public Button btnDelete;

    //Update PopUp
    public GridPane grdUpdatePopup;
    public TextField txtFirstName;
    public TextField txtLastName;
    public TextField txtUserName;
    public TextField txtEmail;
    public TextField txtPhone;

    //Add Customer Popup
    public GridPane grdAddPopup;
    public TextField txtNewFirstName;
    public TextField txtNewLastName;
    public TextField txtNewUserName;
    public TextField txtNewEmail;
    public TextField txtNewPhone;
    public TextField txtNewPassword;

    Customer customer = new Customer();


    public void initialize(){
        loadTable(Customer.getObsListCustomers());
    }

    //Customer Table

    public void loadTable(ObservableList<Customer> customers) {
        //Reset all as it is
        tblCustomers.getSelectionModel().clearSelection();
        vbxMain.setDisable(false);
        vbxMain.setStyle("");
        grdUpdatePopup.setVisible(false);
        grdAddPopup.setVisible(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tblCustomers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {
            @Override
            public void changed(ObservableValue<? extends Customer> observableValue, Customer customer, Customer t1) {
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });

        //Add user data to columns
        clmCustomerId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        clmFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        clmLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        clmUserName.setCellValueFactory(new PropertyValueFactory<>("username"));
        clmEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        clmContactNo.setCellValueFactory(new PropertyValueFactory<>("phoneNo"));
        tblCustomers.setItems(customers);

        //Initial filterd list
        FilteredList<Customer> filteredData = new FilteredList<>(customers, b -> true);
        txtKeyword.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                //If no search value, then display all records or whatever records it current have. No changes.
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();
                //Check if any matches found
                if (customer.userId().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (customer.firstName().toString().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if ((customer.lastName()!=null) && (customer.lastName().toLowerCase().indexOf(searchKeyword) > -1)){
                    return true;
                } else if (customer.username().toString().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else if (customer.phoneNo().toString().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                } else {
                    return false; //No matches found
                }
            });
        });

        //List with matches
        SortedList<Customer> sortedData = new SortedList<>(filteredData);

        //Bind the sorted result with Table View
        sortedData.comparatorProperty().bind(tblCustomers.comparatorProperty());

        //Apply filtered data to the Table View
        tblCustomers.setItems(sortedData);
    }

    public void btnAddOnAction(ActionEvent actionEvent) {
        vbxMain.setDisable(true);
        vbxMain.setStyle("-fx-background-color: rgba(0,0,0,0.2)");
        grdAddPopup.setVisible(true);
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        vbxMain.setDisable(true);
        vbxMain.setStyle("-fx-background-color: rgba(0,0,0,0.2)");
        grdUpdatePopup.setVisible(true);
        txtFirstName.setText(tblCustomers.getSelectionModel().getSelectedItem().firstName());
        txtLastName.setText(tblCustomers.getSelectionModel().getSelectedItem().lastName());
        txtUserName.setText(tblCustomers.getSelectionModel().getSelectedItem().username());
        txtEmail.setText(tblCustomers.getSelectionModel().getSelectedItem().email());
        txtPhone.setText(tblCustomers.getSelectionModel().getSelectedItem().phoneNo().toString());
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Customer.deleteCustomer(tblCustomers.getSelectionModel().getSelectedItem().userId());
        loadTable(Customer.getObsListCustomers());
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }

    //Update Customer

    private void update(){
        String customerId = tblCustomers.getSelectionModel().getSelectedItem().userId();
        String firstName = txtFirstName.getText();
        String lastName = "";
        lastName = txtLastName.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        int phoneNo = Integer.parseInt(txtPhone.getText());
        //Send data to update method in Customer class
        tblCustomers.getSelectionModel().getSelectedItem().updateCustomer(customerId, firstName, lastName, userName, email, phoneNo);
        loadTable(Customer.getObsListCustomers());
    }

    public void btnDoneUpdateOnAction(ActionEvent actionEvent) {
        update();
    }

    public void txtPhoneOnAction(ActionEvent actionEvent) {
        update();
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        loadTable(Customer.getObsListCustomers());
    }

    //Add Customer

    private void addCustomer(){
        String firstName = txtNewFirstName.getText();
        String lastName = "";
        lastName = txtNewLastName.getText();
        String userName = txtNewUserName.getText();
        String email = txtNewEmail.getText();
        int phoneNo = Integer.parseInt(txtNewPhone.getText());
        String password = txtNewPassword.getText();
        //Send data to update method in Customer class
        boolean added = DBOperations.addNewCustomerOnDB(firstName, lastName, userName, email, phoneNo, password);
        if (added) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"New Customer Added Successfully!");
            alert.showAndWait();
        } else {
            Alert alert1 = new Alert(Alert.AlertType.ERROR,"Something went Wrong...");
            alert1.showAndWait();
        }
        loadTable(Customer.getObsListCustomers());
        txtNewFirstName.clear();
        txtNewLastName.clear();
        txtNewUserName.clear();
        txtNewEmail.clear();
        txtNewPhone.clear();
        txtNewPassword.clear();

    }

    public void btnDoneAddOnAction(ActionEvent actionEvent) {
        addCustomer();
    }

    public void txtNewPasswordOnAction(ActionEvent actionEvent) {
        addCustomer();
    }

    public void btnCancelAddOnAction(ActionEvent actionEvent) {
        loadTable(Customer.getObsListCustomers());
        txtNewFirstName.clear();
        txtNewLastName.clear();
        txtNewUserName.clear();
        txtNewEmail.clear();
        txtNewPhone.clear();
        txtNewPassword.clear();
    }

}
