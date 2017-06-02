package codeu.desktop.gui;

/**
 * Project Group #34 
 * CodeU Spring '17 | Google
 * Skeleton for 'LoginScreen.fxml' Controller Class
 */

import com.jfoenix.controls.JFXButton;  //actionButton for login screen
import com.jfoenix.controls.JFXTextField;   //textField for login screen -- to be validated
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LoginScreenController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="loginTextField"
    protected JFXTextField loginTextField; // Value injected by FXMLLoader
 
    @FXML // fx:id="loginActionButton"
    public JFXButton loginActionButton; // Value injected by FXMLLoader
      //if field validation returns false
    
    @FXML // fx:id="welcomeText"
    private Text welcomeText; // Value injected by FXMLLoader

    @FXML
    void IfEnterValidateText(KeyEvent event) {
           //enter key pressed, validate text input + update buttons
    }

    @FXML
    void changeTextEasterEgg(MouseEvent event) {    //if 'Welcome' title clicked
        if (event == true) {
            welcomeText.setText("Bienvenidos"); 
        }
        if (event == true) {
            welcomeText.setText("Willkommen");
        }
        if (event == true) {
            welcomeText.setText("Welcome");
        }    
    }

    @FXML
    void checkLanguageToMatch(InputMethodEvent event) { 
        //matches 'welcome' greeting to virtual keyboard language input language

    }

    @FXML
    void ifEnterCheckInput(KeyEvent event) {    //on 'Enter'
        loginActionButton.setOnKeyPressed((event) -> {      //lambda notation
            if(event.getCode() == KeyCode.ENTER) {
                //do validation + button update actions here
            }
        });

    }

    @FXML
    void validateInput(ActionEvent event) {
        //if 'Proceed' pressed when auto text validation fails (button not changed to 'login')

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert textInputValidated != null : "fx:id=\"textInputValidated\" was not injected: check your FXML file 'LoginScreen.fxml'.";
        assert loginActionButton != null : "fx:id=\"loginActionButton\" was not injected: check your FXML file 'LoginScreen.fxml'.";
        assert welcomeText != null : "fx:id=\"welcomeText\" was not injected: check your FXML file 'LoginScreen.fxml'.";

    }
}
