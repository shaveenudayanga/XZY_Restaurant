package OtherClasses;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfWriter;
import controllers.admin.AdminFormController;
import controllers.admin.ManageBookingsFormController;
import controllers.customer.HomeFormController;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class Booking extends HomeFormController {
    private String bookingId;
    private String customerId;
    private int tableId;
    private LocalDate bookingDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private int startTime;
    private int endTime;
    private int noOfGuests;
    private String comments;
    private String weekDaysInLetters;
    private String weekDaysExpanded = "";

    //Constructors

    public Booking(String bookingId, String customerId, int tableId, LocalDate bookingDate, LocalDate startDate, LocalDate endDate, int startTime, int endTime, int noOfGuests, String comments, String weekDaysInLetters) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.tableId = tableId;
        this.bookingDate = bookingDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.noOfGuests = noOfGuests;
        this.comments = comments;
        this.weekDaysInLetters = weekDaysInLetters;
    }

    public Booking(String bookingId, String customerId, int tableId, LocalDate bookingDate, LocalDate startDate, int startTime, int endTime, int noOfGuests, String comments) {
        this(bookingId, customerId, tableId, bookingDate, startDate,null, startTime, endTime, noOfGuests, comments, null);
    }

    public Booking(String bookingId, String customerId, int tableId, LocalDate bookingDate, LocalDate startDate, int startTime, int noOfGuests, int endTime) {
        this(bookingId, customerId, tableId,bookingDate, startDate,null, startTime, endTime, noOfGuests, null, null);
    }

    public Booking(LocalDate startDate, LocalDate endDate, int startTime, int endTime, int noOfGuests, String comments, String weekDaysInLetters) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.noOfGuests = noOfGuests;
        this.comments = comments;
        this.weekDaysInLetters = weekDaysInLetters;
    }

    public Booking(LocalDate date, int startTime, int endTime, int noOfGuests, String comments) {
        this(date,null,startTime,endTime,noOfGuests,comments,null);
    }

    public Booking() {
    }



    //Methods

    public void createBooking(){
        customerId = Customer.user.userId();
        tableId = RestaurantTable.getTableId();
        bookingId = autoGenerateBookingID();
        bookingDate = LocalDate.now();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into bookings values(?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setObject(1,bookingId);
            preparedStatement.setObject(2,customerId);
            preparedStatement.setObject(3,tableId);
            preparedStatement.setObject(4,bookingDate);
            preparedStatement.setObject(5,startDate);
            preparedStatement.setObject(6,endDate);
            preparedStatement.setObject(7,startTime);
            preparedStatement.setObject(8,endTime);
            preparedStatement.setObject(9,noOfGuests);
            preparedStatement.setObject(10,comments);
            preparedStatement.setObject(11,weekDaysInLetters);


            int i = preparedStatement.executeUpdate();

            if (i!=0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Booking successful!");
                alert.setContentText("A confirmation email with the booking details and a QR code has been sent to your email address.");
                alert.showAndWait();
                String subject = "Your Booking Confirmation";
                createPDF(bookingId,customerId,tableId,bookingDate,startDate,endDate,startTime,endTime,noOfGuests,comments,subject);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Something went wrong...");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.out.println("An error occurs when inserting to the database");
        }
        System.out.println(bookingId);
        System.out.println(customerId);
        System.out.println(tableId);
        System.out.println(bookingDate);
        System.out.println(startDate);
        System.out.println(endDate);
        System.out.println(startTime);
        System.out.println(endTime);
        System.out.println(noOfGuests);
        System.out.println(comments);
        System.out.println(weekDaysExpanded);

    }

    public void updateBooking(int tableId, int guests, LocalDate startDate, LocalDate endDate, int startTime, int endTime, String days, String comments){
        if (endDate==null ){
            days = "";
        }
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE bookings SET tableID = ?, number_of_guests = ?, start_date = ?, end_date = ?, start_time = ?, end_time = ?, weekDays = ?, Comments = ? WHERE bookingID = ?");
            preparedStatement.setObject(1,tableId);
            preparedStatement.setObject(2,guests);
            preparedStatement.setObject(3,startDate);
            preparedStatement.setObject(4,endDate);
            preparedStatement.setObject(5,startTime);
            preparedStatement.setObject(6,endTime);
            preparedStatement.setObject(7,days);
            preparedStatement.setObject(8,comments);
            preparedStatement.setObject(9,bookingId);
            int i = preparedStatement.executeUpdate();

            if (i!=0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Successfully Updated!");
                alert.setContentText("A confirmation email with the booking details and a QR code has been sent to your email address.");
                alert.showAndWait();
                String subject = "Booking Update Confirmation";
                createPDF(bookingId,customerId,tableId,bookingDate,startDate,endDate,startTime,endTime,noOfGuests,comments,subject);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Something went wrong...");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.out.println("An error occurs when updating data of the database");
        }
        System.out.println(bookingId);
        System.out.println(tableId);
        System.out.println(guests);
        System.out.println(startDate);
        System.out.println(endDate);
        System.out.println(startTime);
        System.out.println(endTime);
        System.out.println(days);
        System.out.println(comments);
    }

    public void deleteBooking(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to delete?",ButtonType.YES,ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult().equals(ButtonType.YES)) {
            System.out.println("Delete");
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from bookings where bookingID = ?");
                preparedStatement.setObject(1,bookingId);
                int i = preparedStatement.executeUpdate();
                if (i == 0){
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION,"Something went wrong...");
                    alert1.showAndWait();
                }
            } catch (SQLException e) {
                System.out.println("An error occurs when deleting from the database");
            }
        }
    }

    private void createPDF(String bookingId, String customerId, int tableId, LocalDate bookingDate, LocalDate startDate, LocalDate endDate, int startTime, int endTime, int noOfGuests, String comments, String subject) {
        try {
            // Create the PDF document
            Rectangle smallPage = new Rectangle(400, 700);
            Document document = new Document(smallPage);
            document.setMargins(20, 20, 20, 20);
            String filePath = "booking_details.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Add booking details to the PDF
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
            Paragraph title = new Paragraph("Booking Details", boldFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add space after the title
            document.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 25)));

            String weekDays = null;
            if (endDate!=null && weekDaysInLetters.equals("ABCDEFG")) {
                weekDays = "All";
            } else if (endDate!=null){
                weekDaysDecrypt();
                weekDays = weekDaysExpanded;
            }
            String bookingDetails = null;
            if (endDate!=null) {
                bookingDetails =
                        "Customer ID    : "+customerId+
                                "\nBooking ID       : "+bookingId+
                                "\nBooking Date   : "+bookingDate+
                                "\nTable ID           : "+tableId+
                                "\nStart Date         : "+startDate+
                                "\nEnd Date          : "+endDate+
                                "\nWeekdays        : "+weekDays+
                                "\nCheck in time   : "+startTime+
                                "\nCheck out time : "+endTime;
            } else {
                bookingDetails =
                        "Customer ID    : "+customerId+
                                "\nBooking ID       : "+bookingId+
                                "\nBooking Date   : "+bookingDate+
                                "\nTable ID           : "+tableId+
                                "\nReserved Date : "+startDate+
                                "\nCheck in time   : "+startTime+
                                "\nCheck out time : "+endTime;
            }
            document.add(new Paragraph(bookingDetails, new Font(Font.FontFamily.HELVETICA, 12)));

            // Add space before the QR
            document.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 25)));

            // Generate QR code
            String qrCodeData = bookingDetails;

            // Add QR code image to PDF
            Image qrCode = generateQRCodeImage(qrCodeData);
            qrCode.setAlignment(Element.ALIGN_CENTER);
            document.add(qrCode);

            document.close();

            // Open the PDF file
            File pdfFile = new File(filePath);
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("Desktop is not supported.");
                }
            } else {
                System.out.println("The file does not exist.");
            }

            String bodyText = "Dear Customer,\n\nPlease find attached your booking details.\n\nBest Regards,\nXYZ Restaurant";
            AdminFormController adminFormController = new AdminFormController();
            if (adminFormController.toggleMailing()) {
                Thread thread = new Thread(()->{
                    sendEmail(subject,bodyText,filePath);
                });
                thread.start();
            }

        } catch (Exception e) {
            System.out.println("Error creating PDF");
        }
    }

    private Image generateQRCodeImage(String qrCodeData) throws DocumentException, IOException {
        BarcodeQRCode barcodeQRCode = new BarcodeQRCode(qrCodeData, 200, 200, null);
        java.awt.Image awtImage = barcodeQRCode.createAwtImage(Color.BLACK, Color.WHITE);
        return Image.getInstance(awtImage, null);
    }

    private void sendEmail(String subject, String bodyText, String filePath) {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT mailer_email, mailer_password FROM program_data WHERE mail_no = 1");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String fromEmail = resultSet.getString(1);
                String password = resultSet.getString(2);

                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com"); // Set to your SMTP server
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(fromEmail));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(Customer.user.email()));
                    message.setSubject(subject);

                    // Create the message part
                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setText(bodyText);

                    // Create a multipart message for attachment
                    Multipart multipart = new MimeMultipart();
                    multipart.addBodyPart(messageBodyPart);

                    // Second part is the attachment
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(filePath);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName("booking_details.pdf");
                    multipart.addBodyPart(messageBodyPart);

                    // Send the complete message parts
                    message.setContent(multipart);

                    // Send message
                    Transport.send(message);
                    System.out.println("Email sent successfully...");
                } catch (MessagingException e) {
                    System.out.println("Failed to send email...");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to send email...");
        }

    }

    public String autoGenerateBookingID() {
        Connection connection =DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select bookingID from bookings order by bookingID desc limit 1");
            boolean isExist = resultSet.next();
            if (isExist){
                String oldId = resultSet.getString(1);
                oldId = oldId.substring(1);
                int intID = Integer.parseInt(oldId);
                intID += 1;
                if (intID < 10)
                    bookingId = "B000" + intID;
                else if (intID < 100)
                    bookingId = "B00" + intID;
                else if (intID < 1000)
                    bookingId = "B0" + intID;
                else
                    bookingId = "B" + intID;
            }
            else {
                bookingId = "B0001";
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bookingId;
    }

    public void weekDaysDecrypt(){
        try {
            if (weekDaysInLetters.contains("A")){
                weekDaysExpanded += "Monday ";
            }
            if (weekDaysInLetters.contains("B")){
                weekDaysExpanded += "Tuesday ";
            }
            if (weekDaysInLetters.contains("C")){
                weekDaysExpanded += "Wednesday ";
            }
            if (weekDaysInLetters.contains("D")){
                weekDaysExpanded += "Thursday ";
            }
            if (weekDaysInLetters.contains("E")){
                weekDaysExpanded += "Friday ";
            }
            if (weekDaysInLetters.contains("F")){
                weekDaysExpanded += "Saturday ";
            }
            if (weekDaysInLetters.contains("G")){
                weekDaysExpanded += "Sunday";
            }
        } catch (NullPointerException e){
            System.out.println("No Weekdays");
        }


    }

    public ObservableList<Booking> getObsListBookings(Object callerClass){
        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        bookings.clear();
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = null;
            if (callerClass instanceof HomeFormController) {
                preparedStatement = connection.prepareStatement("select * from bookings where customerID = ?");
                preparedStatement.setObject(1, Customer.user.userId());
            }else if (callerClass instanceof ManageBookingsFormController) {
                preparedStatement = connection.prepareStatement("select * from bookings");
            } else {
                System.out.println("Unknown caller class");
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String bookingId = resultSet.getString(1);
                String customerId = resultSet.getString(2);
                int tableId = resultSet.getInt(3);
                LocalDate bookingDate = LocalDate.parse(resultSet.getString(4));
                LocalDate startDate = LocalDate.parse(resultSet.getString(5));
                LocalDate endDate = null;
                try {
                    endDate = LocalDate.parse(resultSet.getString(6));
                } catch(NullPointerException e){
                    System.out.println("End date is null while adding to Obs. List in Booking ID: "+bookingId);
                }
                int startTime = resultSet.getInt(7);
                int endTime = resultSet.getInt(8);
                int noOfGuests = resultSet.getInt(9);
                String comments = resultSet.getString(10);
                String weekDaysInLetters = resultSet.getString(11);
                Booking booking = new Booking(bookingId, customerId, tableId, bookingDate, startDate, endDate, startTime, endTime, noOfGuests, comments, weekDaysInLetters);
                bookings.add(booking);
                booking.weekDaysDecrypt();
            }
        } catch (NullPointerException | SQLException e) {
            throw new RuntimeException("Error while getting booking records from the database");
        }
        return bookings;
    }




    //Getters and Setters

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public Integer getNoOfGuests() {
        return noOfGuests;
    }

    public void setNoOfGuests(int noOfGuests) {
        this.noOfGuests = noOfGuests;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getWeekDaysInLetters() {
        return weekDaysInLetters;
    }

    public String getWeekDaysExpanded() {
        return weekDaysExpanded;
    }

    public void setWeekDaysInLetters(String weekDaysInLetters) {
        this.weekDaysInLetters = weekDaysInLetters;
    }

}
