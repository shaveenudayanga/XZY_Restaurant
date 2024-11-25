package controllers;

import OtherClasses.RestaurantTable;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Map;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class TableViewFormController extends RestaurantTable {
    public Button b1;
    public Button b2;
    public Button b3;
    public Button b4;
    public Button b5;
    public Button b6;
    public Button b7;
    public Button b8;
    public Button b9;
    public Button b10;
    public Button b11;
    public Button b12;
    public Button b13;
    public Button btnClearSelection;
    public Button btnConfirmSelection;
    public AnchorPane root;
    int tableID = 0;


    public void initialize(){
        setBtnDisable(true);
        checkAndDisplayTableAvailability();
    }

    public void checkAndDisplayTableAvailability(){
        Map<Integer, Boolean> tableAvailability = getTableAvailability();
        Button[] btn = {b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13};
        for (Map.Entry<Integer, Boolean> entry : tableAvailability.entrySet()) {
            if (entry.getValue()) {
                btn[entry.getKey()-1].setDisable(false);
                btn[entry.getKey()-1].setStyle("-fx-background-color: transparent ; -fx-border-color: transparent ; -fx-border-radius: 5");
            } else {

                btn[entry.getKey()-1].setDisable(true);
                btn[entry.getKey()-1].setStyle("-fx-background-color: rgba(255, 0, 0, 0.3); ; -fx-border-color: red ; -fx-border-radius: 5");
            }
        }
    }

    public void setBtnDisable(boolean isDisable){
        btnClearSelection.setDisable(isDisable);
        btnConfirmSelection.setDisable(isDisable);
    }

    public void onButtonPressed(Button b){
        setBtnDisable(false);
        checkAndDisplayTableAvailability();
        b.setStyle("-fx-background-color: rgba(0, 255, 0, 0.3) ; -fx-border-color: blue ; -fx-border-radius: 5");
        tableID = Integer.parseInt(b.getText().substring(1));
    }


    public void btnTable1OnAction(ActionEvent actionEvent) {
        onButtonPressed(b1);
    }

    public void btnTable2OnAction(ActionEvent actionEvent) {
        onButtonPressed(b2);
    }

    public void btnTable3OnAction(ActionEvent actionEvent) {
        onButtonPressed(b3);
    }

    public void btnTable4OnAction(ActionEvent actionEvent) {
        onButtonPressed(b4);
    }

    public void btnTable5OnAction(ActionEvent actionEvent) {
        onButtonPressed(b5);
    }

    public void btnTable6OnAction(ActionEvent actionEvent) {
        onButtonPressed(b6);
    }

    public void btnTable7OnAction(ActionEvent actionEvent) {
        onButtonPressed(b7);
    }

    public void btnTable8OnAction(ActionEvent actionEvent) {
        onButtonPressed(b8);
    }

    public void btnTable9OnAction(ActionEvent actionEvent) {
        onButtonPressed(b9);
    }

    public void btnTable10OnAction(ActionEvent actionEvent) {
        onButtonPressed(b10);
    }

    public void btnTable11OnAction(ActionEvent actionEvent) {
        onButtonPressed(b11);
    }

    public void btnTable12OnAction(ActionEvent actionEvent) {
        onButtonPressed(b12);
    }

    public void btnTable13OnAction(ActionEvent actionEvent) {
        onButtonPressed(b13);
    }


    public void btnClearSelectionOnAction(ActionEvent actionEvent) {
        tableID = 0;
        setBtnDisable(true);
        initialize();
    }

    public void btnConfirmSelectionOnAction(ActionEvent actionEvent) {
        setTableId(tableID);

        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
        System.out.println(RestaurantTable.getTableId());
    }



}
