/**
 * Google CodeU Project Group 34, 05/14/17
 * Skeleton connector .java class
 */

import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;

public class MessagesPaneController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="messagesPaneActive"
    private Pane messagesPaneActive; // Value injected by FXMLLoader

    @FXML // fx:id="messagesPane"
    private JFXListView<?> messagesPane; // Value injected by FXMLLoader

    @FXML
    void DetermineMessageAction(SwipeEvent event) {

    }

    @FXML
    void None(SwipeEvent event) {

    }

    @FXML
    void aWildStaticContextMenuAppears(TouchEvent event) {

    }

    @FXML
    void activeContactBar(SwipeEvent event) {

    }

    @FXML
    void activeContextCopyPasteMore(TouchEvent event) {

    }

    @FXML
    void contextMenuCopyPasteMore(MouseEvent event) {

    }

    @FXML
    void contextMenuListener1halfSec(MouseEvent event) {

    }

    @FXML
    void contextMenuMore(TouchEvent event) {

    }

    @FXML
    void disabilityFeatureset(ZoomEvent event) {

    }

    @FXML
    void disable(ZoomEvent event) {

    }

    @FXML
    void disableForNow(MouseEvent event) {

    }

    @FXML
    void fuckThatsGoing2beHardcosIWantIndivActions(SwipeEvent event) {

    }

    @FXML
    void hoverContextMenuMaybeAddSumGlow(TouchEvent event) {

    }

    @FXML
    void ifActiveonSelectMode(TouchEvent event) {

    }

    @FXML
    void indivMessageOptions(TouchEvent event) {

    }

    @FXML
    void limitToVerticalScroll(MouseEvent event) {

    }

    @FXML
    void lockMessageElementstoPane(RotateEvent event) {

    }

    @FXML
    void messagesFollowScrollWithParent(ScrollEvent event) {

    }

    @FXML
    void messagesLockedonRotation(RotateEvent event) {

    }

    @FXML
    void messagesOrientWithRotation(RotateEvent event) {

    }

    @FXML
    void moveElementsVertical(ScrollEvent event) {

    }

    @FXML
    void nothing(MouseEvent event) {

    }

    @FXML
    void queEsEso(ContextMenuEvent event) {

    }

    @FXML
    void returnToOriginal(ZoomEvent event) {

    }

    @FXML
    void rotateElements(RotateEvent event) {

    }

    @FXML
    void scrollMessagesVerticalDown(SwipeEvent event) {

    }

    @FXML
    void scrollMessagesVerticalUp(SwipeEvent event) {

    }

    @FXML
    void setElementsStatic(ScrollEvent event) {

    }

    @FXML
    void setMessageElements(DragEvent event) {

    }

    @FXML
    void showMessageTimes(SwipeEvent event) {

    }

    @FXML
    void wrapMessages(ZoomEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert messagesPaneActive != null : "fx:id=\"messagesPaneActive\" was not injected: check your FXML file 'messagespane.fxml'.";
        assert messagesPane != null : "fx:id=\"messagesPane\" was not injected: check your FXML file 'messagespane.fxml'.";

    }
}