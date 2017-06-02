package codeu.desktop.gui;



import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
public class CodeUDesktopGUI extends Application {

    @FXML
    private AnchorPane rootPane;
    private Object loginActionButton;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        //loads LoginScreen.fxml on start
        AnchorPane pane = FXMLLoader.load(getClass().getResource("LoginScreen.fxml"));
        rootPane.getChildren().setAll(pane);
    }
    
    @FXML
    public void onValidatedEnter(ActionEvent event) throws IOException {               
        /**loads MainClient upon entry of validated input
         * @param event triggered from LoginScreenController.java
         */
        
        loginActionButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent t){
                AnchorPane pane;
                try {
                    pane = FXMLLoader.load(getClass().getResource("MainClient.fxml"));
                } catch (IOException ex) {
                    Logger.getLogger(CodeUDesktopGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                rootPane.getChildren().setAll(pane);
            }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
