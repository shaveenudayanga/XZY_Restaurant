package controllers.customer.sections;

import OtherClasses.Customer;
import controllers.customer.HomeFormController;
import db.DBConnection;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.net.HttpURLConnection;
import java.net.URL;

import static OtherClasses.Customer.user;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class UserProfileSectionController extends HomeFormController{
    public GridPane paneUserInfo;

    //Profile photo(0,0)
    public AnchorPane paneDp;
    public ImageView imgDp;
    public StackPane paneEditDp;
    public ImageView btnEditDp;
    public Label lblUserID;

    //User Info(1,0)
        //Name
    public HBox hbxShowName;
    public Label lblShowName;
    public ImageView imgEditName;
    public HBox hbxEditName;
    public TextField txtFirstName;
    public TextField txtLastName;
    public Button btnDoneName;
        //UserName
    public HBox hbxShowUserName;
    public Label lblShowUserName;
    public ImageView imgEditUserName;
    public HBox hbxEditUserName;
    public TextField txtUserName;
    public Button btnDoneUserName;
        //Email
    public HBox hbxShowEmail;
    public Label lblShowEmail;
    public ImageView imgEditEmail;
    public HBox hbxEditEmail;
    public TextField txtEmail;
    public Button btnDoneEmail;
        //Phone
    public HBox hbxShowPhone;
    public Label lblShowPhone;
    public ImageView imgEditPhone;
    public HBox hbxEditPhone;
    public TextField txtPhone;
    public Button btnDonePhone;

    //ChangePassword(1,1)
    public VBox vbxChangePw;
    public PasswordField pssOldPassword;
    public PasswordField pssNewPassword;
    public PasswordField pssConfirmNewPassword;
    public Button btnChangePassword;

    //Delete Account(1,0)
    public Button btnDeleteAccount;
    public VBox vbxHideDeleteAcc;
    public PasswordField pssPasswordForAccDelete;
    public Button btnDeleteAccountConfirm;

    //Constants
    private final String serverUrl = "https://pethometown.com/upload.php";
    private final File defaultImageFile = new File("images/profile-user.256x256.png");


    public void initialize(){
        shiftToShow(hbxShowName,hbxEditName);
        shiftToShow(hbxShowUserName,hbxEditUserName);
        shiftToShow(hbxShowEmail,hbxEditEmail);
        shiftToShow(hbxShowPhone,hbxEditPhone);
        enableOnActions();
        btnDeleteAccount.setVisible(true);
        vbxHideDeleteAcc.setVisible(false);
        startLoadProfilePictureThread();
        showHideEditDpButton();
        btnEditDp.setOnMouseClicked(e -> {
            try {
                uploadProfilePicture();
            } catch (Exception ex) {
                showErrorAlert("Error uploading profile picture");
            }
        });
    }

    public void enableOnActions(){
        imgEditName.setOnMouseClicked(e -> {
            shiftToEdit(hbxShowName,hbxEditName,txtFirstName,btnDoneName,"first_name");
        });
        imgEditUserName.setOnMouseClicked(e -> {
            shiftToEdit(hbxShowUserName,hbxEditUserName,txtUserName,btnDoneUserName,"username");
        });
        imgEditEmail.setOnMouseClicked(e -> {
            shiftToEdit(hbxShowEmail,hbxEditEmail,txtEmail,btnDoneEmail,"email");
        });
        imgEditPhone.setOnMouseClicked(e -> {
            shiftToEdit(hbxShowPhone,hbxEditPhone,txtPhone,btnDonePhone,"contact_no");
        });
        btnDeleteAccount.setOnMouseClicked(e -> {
            showDeleteAccVBox();
        });
        btnChangePassword.setOnAction(e -> {
            updatePassword();
        });
        pssConfirmNewPassword.setOnAction(e -> {
            updatePassword();
        });
    }


    //Profile photo

    public void showHideEditDpButton(){
        paneEditDp.setVisible(false);
        paneDp.setOnMouseEntered(e->{
            paneEditDp.setVisible(true);
        });
        paneDp.setOnMouseExited(e->{
            paneEditDp.setVisible(false);
        });
    }

    private void startLoadProfilePictureThread() {
        Thread thread = new Thread(() -> {
            Platform.runLater(() -> {

                Circle clip = new Circle(imgDp.getFitWidth() / 2, imgDp.getFitHeight() / 2, imgDp.getFitWidth() / 2);
                imgDp.setClip(clip);
            });
            try {
                loadProfilePicture();
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Error loading profile picture");
            }
        });
        thread.start();
    }

    private void uploadProfilePicture() {
        File selectedFile = chooseImageFile();
        if (selectedFile != null) {
            try {
                File renamedFile = renameImageFile(selectedFile);
                updateDatabaseWithDpUrl(renamedFile.getName());
                String response = uploadFileToServer(renamedFile);
//                String imageUrl = parseResponse(response);
//                System.out.println("Img url from the method: "+imageUrl);
//                displayProfilePicture(imageUrl);
                startLoadProfilePictureThread();
                renamedFile.delete(); // Optionally delete the temporary file after uploading
            } catch (Exception e) {
                showErrorAlert("Error uploading file");
            }
        } else {
            System.out.println("File selection cancelled.");
        }
    }

    private File chooseImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        return fileChooser.showOpenDialog(new Stage());
    }

    private File renameImageFile(File selectedFile) {
        String extension = getFileExtension(selectedFile);
        File renamedFile = new File(selectedFile.getParent(), user.userId() + "." + extension);
        try {
            Files.copy(selectedFile.toPath(), renamedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            showErrorAlert("Error renaming file");
        }
        return renamedFile;
    }

    private void updateDatabaseWithDpUrl(String dpUrl) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE user SET dp_url = ? WHERE id = ?");
        preparedStatement.setObject(1, dpUrl);
        preparedStatement.setObject(2, user.userId());
        int i = preparedStatement.executeUpdate();
        if (i != 0) {
            System.out.println("User dp url updated on database");
        }
    }

    private void displayProfilePicture(String imageUrl) {
        Image image;
        if (imageUrl != null) {
            try {
                String completeUrl = "https://pethometown.com/" + imageUrl;
                if (doesProfilePictureExist(completeUrl)) {
                    File imageFile = downloadImage(completeUrl);
                    image = new Image(imageFile.toURI().toString());
                    System.out.println("Image loaded");
                } else {
                    System.out.println("Image does not exist at the specified URL.");
                    image = new Image(defaultImageFile.toURI().toString());
                }
            } catch (Exception e) {
                image = new Image(defaultImageFile.toURI().toString());
            }
        } else {
            image = new Image(defaultImageFile.toURI().toString());
        }
        imgDp.setImage(image);
//        super.loadDp(image);
    }

    public void loadProfilePicture() throws SQLException {
        String imageUrl = "uploads/";
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT dp_url FROM user WHERE id = ?");
        preparedStatement.setObject(1, user.userId());
        ResultSet result = preparedStatement.executeQuery();
        if (result.next()) {
            imageUrl += result.getString(1);
            System.out.println(imageUrl);
        }
        displayProfilePicture(imageUrl);
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    private String uploadFileToServer(File file) throws IOException {
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";
        URL url = new URL(serverUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                FileInputStream inputStream = new FileInputStream(file)
        ) {
            // Send file
            output.writeBytes("--" + boundary + CRLF);
            output.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + CRLF);
            output.writeBytes("Content-Type: application/octet-stream" + CRLF);
            output.writeBytes(CRLF);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.writeBytes(CRLF);

            // End of multipart/form-data.
            output.writeBytes("--" + boundary + "--" + CRLF);
            output.flush();

            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } else {
                throw new IOException("Failed to upload file: " + responseCode);
            }
        } finally {
            connection.disconnect();
        }
    }

    private File downloadImage(String completeImgUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(completeImgUrl).openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = new BufferedInputStream(connection.getInputStream())) {
            // Extract file extension from the URL
            String fileExtension = completeImgUrl.substring(completeImgUrl.lastIndexOf('.') + 1);

            // Create a temporary file with the original file extension
            File tempFile = File.createTempFile("profile_picture", "." + fileExtension);

            // Copy input stream to the output file
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Image downloaded to: " + tempFile.getAbsolutePath());

            return tempFile;
        } finally {
            // Disconnect the connection
            connection.disconnect();
        }
    }

//    private String parseResponse(String response) {
//        // Implement JSON parsing here to extract the URL from the response
//        // Assuming the response is in JSON format
//        // Example response: {"status":"success","url":"uploads/filename.jpg"}
//        JSONObject jsonResponse = new JSONObject(response);
//        if (jsonResponse.getString("status").equals("success")) {
//            return jsonResponse.getString("url");
//        }
//        return null;
//    }

    private boolean doesProfilePictureExist(String imageUrl) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
        connection.setRequestMethod("HEAD");
        int responseCode = connection.getResponseCode();
        return (responseCode == HttpURLConnection.HTTP_OK);
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    //User Info(1,0)

    private void shiftToShow(HBox showHBox, HBox editHBox) {
        editHBox.setVisible(false);
        showHBox.setVisible(true);
        try {
            lblUserID.setText("User ID: "+ user.userId());
            lblShowUserName.setText(user.username());
            lblShowEmail.setText(user.email());
            lblShowPhone.setText(String.valueOf(user.phoneNo()));
            try {
                lblShowName.setText(user.firstName()+" "+user.lastName());
            } catch (NullPointerException e) {
            }
        } catch (Exception e) {
            System.out.println("Can't load user data");
        }
        FadeTransition showShowNameHbx = new FadeTransition(Duration.seconds(0.4), showHBox);
        showShowNameHbx.setFromValue(0);
        showShowNameHbx.setToValue(1);
        showShowNameHbx.play();
        FadeTransition hideEditNameHbx = new FadeTransition(Duration.seconds(0.4), editHBox);
        hideEditNameHbx.setFromValue(1);
        hideEditNameHbx.setToValue(0);
        hideEditNameHbx.play();
    }

    private void shiftToEdit(HBox showHBox, HBox editHBox, TextField focusTextField, Button doneButton, String columnNameONDb) {
        showHBox.setVisible(false);
        editHBox.setVisible(true);
        FadeTransition hideNameHbx = new FadeTransition(Duration.seconds(0.4),showHBox);
        hideNameHbx.setFromValue(1);
        hideNameHbx.setToValue(0);
        hideNameHbx.play();
        FadeTransition showNameHbx = new FadeTransition(Duration.seconds(0.4),editHBox);
        showNameHbx.setFromValue(0);
        showNameHbx.setToValue(1);
        showNameHbx.play();
        focusTextField.requestFocus();
        doneButton.setOnAction(e -> {
            updateInfo(showHBox, editHBox, focusTextField, doneButton, columnNameONDb);
        });
        focusTextField.setOnAction(e -> {
            updateInfo(showHBox, editHBox, focusTextField, doneButton, columnNameONDb);
        });
        editHBox.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Node target = event.getPickResult().getIntersectedNode();
            if (!isDescendant(editHBox, target) && editHBox.isVisible()) {
                System.out.println("Mouse click detected outside HBox");
                shiftToShow(showHBox, editHBox);
            }
        });
    }

    private void updateInfo(HBox showHBox, HBox editHBox, TextField focusTextField, Button doneButton, String columnNameONDb) {
        String query = null;
        if(!focusTextField.getText().isEmpty()){
            if (focusTextField.equals(txtFirstName) && !txtLastName.getText().isEmpty()) {
                query = "UPDATE user SET "+ columnNameONDb +" = ?, last_name = "+ txtLastName.getText() +"  WHERE id = ?";
            } else {
                query = "UPDATE user SET "+ columnNameONDb +" = ? WHERE id = ?";
            }
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                if (doneButton.equals(btnDonePhone)) {
                    try {
                        preparedStatement.setObject(1,Integer.parseInt(focusTextField.getText()));
                    } catch (NumberFormatException | SQLException exception) {
                        Alert alert = new Alert(Alert.AlertType.ERROR,"Contact number should contain only numbers, no letters.");
                        alert.showAndWait();
                        txtPhone.requestFocus();
                    }
                } else {
                    preparedStatement.setObject(1, focusTextField.getText());
                }
                preparedStatement.setObject(2,user.userId());
                System.out.println(preparedStatement);
                int i = preparedStatement.executeUpdate();
                if (i!=0){
                    Customer.getCustomerDataById();
                    shiftToShow(showHBox, editHBox);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR,"Something went wrong...");
                    alert.showAndWait();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println(query);
        }
    }

    private boolean isDescendant(Parent parent, Node node) {
        if (node == null) return false;
        if (node == parent) return true;
        return isDescendant(parent, node.getParent());
    }


    //ChangePassword(1,1)

    private boolean checkPassword(PasswordField passwordField){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE id = ? AND password = ?");
            preparedStatement.setObject(1,user.userId());
            preparedStatement.setObject(2,passwordField.getText());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updatePassword(){
        if (pssNewPassword.getText().equals(pssConfirmNewPassword.getText())) {
            if (checkPassword(pssOldPassword)) {
                pssOldPassword.setStyle("-fx-border-color: transparent; -fx-background-color: rgba(0,0,0,0.4); -fx-text-fill: white;");
                //Update in dataabase
                Connection connection = DBConnection.getInstance().getConnection();
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("UPDATE user SET password = ? WHERE id = ?");
                    preparedStatement.setObject(1,pssConfirmNewPassword.getText());
                    preparedStatement.setObject(2,user.userId());
                    int i = preparedStatement.executeUpdate();
                    if (i!=0){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Password Updated!");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR,"Password not Updated. Something went wrong...");
                        alert.showAndWait();
                    }
                    pssOldPassword.clear();
                    pssNewPassword.clear();
                    pssConfirmNewPassword.clear();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } else {
                pssOldPassword.requestFocus();
                pssOldPassword.setStyle("-fx-border-color: rgba(120,0,0,0.6); -fx-border-radius: 3; -fx-background-color: rgba(0,0,0,0.4); -fx-text-fill: white;");
            }
        } else {
            pssNewPassword.requestFocus();
            pssNewPassword.setStyle("-fx-border-color: rgba(120,0,0,0.6); -fx-border-radius: 3; -fx-background-color: rgba(0,0,0,0.4); -fx-text-fill: white;");
            pssConfirmNewPassword.setStyle("-fx-border-color: rgba(120,0,0,0.6); -fx-border-radius: 3; -fx-background-color: rgba(0,0,0,0.4); -fx-text-fill: white;");
        }
        vbxChangePw.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Node target = event.getPickResult().getIntersectedNode();
            if (!isDescendant(vbxChangePw, target) && vbxChangePw.isVisible()) {
                System.out.println("Mouse click detected outside HBox");
                pssOldPassword.clear();
                pssNewPassword.clear();
                pssConfirmNewPassword.clear();
                pssOldPassword.setStyle("-fx-border-color: transparent; -fx-background-color: rgba(0,0,0,0.4); -fx-text-fill: white;");
                pssNewPassword.setStyle("-fx-border-color: transparent; -fx-background-color: rgba(0,0,0,0.4); -fx-text-fill: white;");
                pssConfirmNewPassword.setStyle("-fx-border-color: transparent; -fx-background-color: rgba(0,0,0,0.4); -fx-text-fill: white;");
            }
        });
    }


    //Delete Account(1,0)

    private void showDeleteAccVBox(){
        btnDeleteAccount.setVisible(false);
        vbxHideDeleteAcc.setVisible(true);
        btnDeleteAccountConfirm.setOnMouseClicked(e -> {
            deleteFromDatabase();
        });
        pssPasswordForAccDelete.setOnAction(e -> {
            deleteFromDatabase();
        });
        vbxHideDeleteAcc.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Node target = event.getPickResult().getIntersectedNode();
            if (!isDescendant(vbxHideDeleteAcc, target) && vbxHideDeleteAcc.isVisible()) {
                System.out.println("Mouse click detected outside HBox");
                btnDeleteAccount.setVisible(true);
                vbxHideDeleteAcc.setVisible(false);
            }
        });
    }

    private void deleteFromDatabase() {
        if (checkPassword(pssPasswordForAccDelete)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Your bookings and user data will be permanently deleted.\nAre you sure you want to delete?",
                    ButtonType.YES,ButtonType.NO);
            alert.showAndWait();
            //Perform delete acc
            if (alert.getButtonTypes().equals(ButtonType.YES)) {
                Connection connection = DBConnection.getInstance().getConnection();
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM user WHERE id = ?");
                    preparedStatement.setObject(1,user.userId());
                    int i = preparedStatement.executeUpdate();
                    if (i!=0){
                        Parent parent = FXMLLoader.load(this.getClass().getResource("../../view/StartPageForm.fxml"));
                        Scene scene = new Scene(parent);
                        Stage primaryStage = (Stage) paneUserInfo.getScene().getWindow();
                        primaryStage.setScene(scene);
                        primaryStage.setTitle("Create New Account");
                        primaryStage.centerOnScreen();
                    } else {
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION,"Something went wrong...");
                        alert1.showAndWait();
                    }

                } catch (SQLException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Incorrect Password!");
            alert.showAndWait();
            pssPasswordForAccDelete.requestFocus();
        }
    }
}
