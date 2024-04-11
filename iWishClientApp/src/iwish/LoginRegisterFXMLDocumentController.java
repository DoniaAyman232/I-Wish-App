package iwish;

import client.ClientSocketManager;
import com.google.gson.Gson;
import dto.UserDTO;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author ahmed
 */
public class LoginRegisterFXMLDocumentController implements Initializable {

    @FXML
    private AnchorPane login_form;
    @FXML
    private Button si_createAccountBtn;
    private TextField si_username;
    @FXML
    private PasswordField si_password;
    @FXML
    private Button si_loginBtn;
    @FXML
    private Button su_loginAccountBtn;
    @FXML
    private TextField su_username;
    @FXML
    private PasswordField su_password;
    @FXML
    private Button signup_btn;
    private Alert alert;
    @FXML
    private TextField su_email;
    @FXML
    private TextField su_firstName;
    @FXML
    private TextField su_lastName;
    @FXML
    private AnchorPane signup_form;
    @FXML
    private TextField si_userEmail;
    private boolean switchToLoginForm;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    public void loginAccount() {
        try {
            ClientSocketManager.initializeSocket();

            if (si_userEmail == null || si_userEmail.getText().isEmpty() || si_password.getText().isEmpty()) {
                showAlert(JOptionPane.ERROR_MESSAGE, "Error Message", "Please fill in your username and password");
                return;
            }

            // Create a UserDTO object with the entered username and password
            UserDTO userDTO = new UserDTO(si_userEmail.getText(), si_password.getText());

            // Convert UserDTO to JSON string using Gson
            Gson gson = new Gson();
            String jsonUser = gson.toJson(userDTO);

            // Send the login request and UserDTO JSON string to the server
            ClientSocketManager.getOutputStream().println("loginRequest");// 1
            ClientSocketManager.getOutputStream().println(jsonUser);

            // Wait for the response from the server
            String response = ClientSocketManager.getInputStream().readLine();

            if (response.equals("succeed")) {
                //showAlert(JOptionPane.INFORMATION_MESSAGE, "Information Message", "Login Success!");
                si_userEmail.getScene().getWindow().hide();
                // Move to the main screen
                FXMLLoader mainPageLoader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
                Parent mainPageRoot = mainPageLoader.load();
                MainPageController mainPageController = mainPageLoader.getController();
                mainPageController.email = si_userEmail.getText();
                mainPageController.setUserEmail(si_userEmail.getText());
                Stage stage = new Stage();
                Scene scene = new Scene(mainPageRoot);
                stage.setScene(scene);
                stage.setTitle("I-Wish App");
                stage.show();
                stage.setOnCloseRequest(event -> {
                    Platform.exit();
                    System.exit(0);
                });
            } else if (response.equals("failed")) {
                // Log in failed
                showAlert(JOptionPane.ERROR_MESSAGE, "Error Message", "Incorrect email or password");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ClientSocketManager.closeSocket();
        }
    }

    private void showAlert(int type, String title, String content) {
        Platform.runLater(() -> {
            JOptionPane optionPane = new JOptionPane(content, type);
            JDialog dialog = optionPane.createDialog(title);
            dialog.setAlwaysOnTop(true);
            dialog.setVisible(true);
        });
    }

    @FXML
    public void registerAccount() {
        try {
            ClientSocketManager.initializeSocket();

            // client side validation
            if (su_username.getText().isEmpty()
                    || su_password.getText().isEmpty()
                    || su_email.getText().isEmpty()
                    || su_firstName.getText().isEmpty()
                    || su_lastName.getText().isEmpty()) {
                showAlert(JOptionPane.ERROR_MESSAGE, "Error Message", "Please fill in all the fields");
                return;
            }

            if (su_password.getText().length() < 3) {
                showAlert(JOptionPane.ERROR_MESSAGE, "Error Message", "Invalid Password, must be at least 3 characters");
            } else {
                UserDTO newUser = new UserDTO(
                        su_username.getText(), su_password.getText(),
                        su_email.getText(), su_firstName.getText(),
                        su_lastName.getText(),
                        0);

                Gson gson = new Gson();
                String jsonUser = gson.toJson(newUser);

                ClientSocketManager.getOutputStream().println("registerRequest");// request type
                ClientSocketManager.getOutputStream().println(jsonUser);

                // Wait for the response from the server in a separate thread
                new Thread(() -> {
                    try {
                        // Read the server response
                        String response = ClientSocketManager.getInputStream().readLine();

                        Platform.runLater(() -> {
                            if (response.equals("succeed")) {
                                showAlert(JOptionPane.INFORMATION_MESSAGE, "Information Message", "Register Success!");
                                switchToLoginForm = true;
                                switchForm(new ActionEvent());
                            } else if (response.equals("failed")) {
                                showAlert(JOptionPane.ERROR_MESSAGE, "Error Message", "User Already Exists, Please Login");
                            }
                        });
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        ClientSocketManager.closeSocket();
                    }
                }).start();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void switchForm(ActionEvent event) {
        if (event.getSource() == su_loginAccountBtn || switchToLoginForm) {
            login_form.setVisible(true);
            signup_form.setVisible(false);
            switchToLoginForm = false;
        } else if (event.getSource() == si_createAccountBtn) {
            login_form.setVisible(false);
            signup_form.setVisible(true);
            switchToLoginForm = false;
        }
    }

}
