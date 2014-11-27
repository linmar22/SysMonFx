/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Target;
import Model.TargetList;
import Util.Looper;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * FXML Controller class for the TargetEditorFXML.fxml.
 *
 * @author Linas Martusevicius
 *
 */
public class TargetEditorFXMLController implements Initializable {

    TargetList tl;
    ArrayList<Looper> ll;

    @FXML
    TextField targetEditor_nameField;
    @FXML
    TextField targetEditor_ipField1;
    @FXML
    TextField targetEditor_ipField2;
    @FXML
    TextField targetEditor_ipField3;
    @FXML
    TextField targetEditor_ipField4;
    @FXML
    TextField targetEditor_domainField;
    @FXML
    Label targetEditor_statusDomainLabel;
    @FXML
    Label targetEditor_statusIPLabel;
    @FXML
    Label targetEditor_statusNameLabel;
    @FXML
    Button targetEditor_addButton;
    @FXML
    Button targetEditor_cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        targetEditor_statusNameLabel.setVisible(false);
        targetEditor_statusIPLabel.setVisible(false);
        targetEditor_statusDomainLabel.setVisible(false);
        tl = TargetList.getInstance();
        ll = tl.getLooperList();
        createListeners();
    }

    /**
     * Pauses the pingers.
     */
    public void pausePing() {
        for (Looper l : ll) {
            l.suspend();
        }

    }

    /**
     * Resumes the pingers.
     */
    public void resumePing() {
        for (Looper l : ll) {
            l.resume();
        }
    }

    /**
     * Creates and sets the click, key, and focus listeners for elements in the
     * TargetEditorFXML.fxml.
     */
    public void createListeners() {
        //BUTTON LISTENERS
        targetEditor_addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Target t = validator();
                if (t != null) {
                    //pausePing();
                    tl.getTargetList().add(t);
                    //resumePing();
                    closeEditor(e);
                }
            }
        });

        targetEditor_cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                closeEditor(e);
            }
        });

        //IP FIELD KEY LISTENERS
        final Pattern pattern = Pattern.compile("\\d", Pattern.CASE_INSENSITIVE);

        EventHandler<KeyEvent> eh = (KeyEvent event) -> {
            Matcher m = pattern.matcher(event.getCharacter());
            TextField tf = (TextField) event.getSource();
            if (!m.matches()) {
                event.consume();
            }
            if (tf.getText().length() > 2) {
                String typed = event.getCharacter();
                if (m.matches()) {
                    if (!tf.getId().contains("4")) {
                        focusNext(tf);
                        getNextFocusableTextField(tf).textProperty().set(typed);
                        getNextFocusableTextField(tf).positionCaret(tf.getText().length());
                    }
                }
                event.consume();
            }
        };

        EventHandler<KeyEvent> eb = (KeyEvent event) -> {
            TextField tf = (TextField) event.getSource();
            if (event.getCode() == KeyCode.BACK_SPACE && !tf.getId().contains("1") && tf.getText().length() == 1) {
                tf.textProperty().set(tf.textProperty().get().substring(0, tf.getText().length() - 1));
                focusPrevious(tf);
                getPreviousFocusableTextField(tf).positionCaret(getPreviousFocusableTextField(tf).getText().length());
            }
        };

        targetEditor_ipField1.addEventHandler(KeyEvent.KEY_TYPED, eh);
        targetEditor_ipField2.addEventHandler(KeyEvent.KEY_TYPED, eh);
        targetEditor_ipField3.addEventHandler(KeyEvent.KEY_TYPED, eh);
        targetEditor_ipField4.addEventHandler(KeyEvent.KEY_TYPED, eh);
        targetEditor_ipField1.addEventHandler(KeyEvent.KEY_PRESSED, eb);
        targetEditor_ipField2.addEventHandler(KeyEvent.KEY_PRESSED, eb);
        targetEditor_ipField3.addEventHandler(KeyEvent.KEY_PRESSED, eb);
        targetEditor_ipField4.addEventHandler(KeyEvent.KEY_PRESSED, eb);

        //FOCUS LISTENERS
        ChangeListener<Boolean> cl = (ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (targetEditor_nameField.focusedProperty().get()) {
                targetEditor_statusNameLabel.setVisible(false);
                targetEditor_nameField.setEffect(null);
            }
            if (targetEditor_ipField1.focusedProperty().get() ^ targetEditor_ipField2.focusedProperty().get() ^ targetEditor_ipField3.focusedProperty().get() ^ targetEditor_ipField4.focusedProperty().get()) {
                targetEditor_statusIPLabel.setVisible(false);
                targetEditor_ipField1.setEffect(null);
                targetEditor_ipField2.setEffect(null);
                targetEditor_ipField3.setEffect(null);
                targetEditor_ipField4.setEffect(null);
            }
            if (targetEditor_domainField.focusedProperty().get()) {
                targetEditor_statusDomainLabel.setVisible(false);
                targetEditor_domainField.setEffect(null);
            }
        };

        targetEditor_nameField.focusedProperty().addListener(cl);
        targetEditor_ipField1.focusedProperty().addListener(cl);
        targetEditor_ipField2.focusedProperty().addListener(cl);
        targetEditor_ipField3.focusedProperty().addListener(cl);
        targetEditor_ipField4.focusedProperty().addListener(cl);
        targetEditor_domainField.focusedProperty().addListener(cl);
    }

    /**
     * Field validator for the TargetEditor window.
     *
     * @return null if any of the fields are invalid, or a Target object if all
     * fields are valid.
     */
    public Target validator() {
        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.RED);
        borderGlow.setWidth(6);
        borderGlow.setHeight(6);

        boolean nameFormatCorrect = false;
        boolean ipFormatCorrect = false;
        boolean domainFormatCorrect = false;

        boolean nameIsEmpty = targetEditor_nameField.textProperty().get().isEmpty();
        boolean ipAllIsEmpty = false;
        boolean domainIsEmpty = targetEditor_domainField.textProperty().get().isEmpty();

        boolean ip1IsEmpty = targetEditor_ipField1.textProperty().get().isEmpty();
        boolean ip2IsEmpty = targetEditor_ipField2.textProperty().get().isEmpty();
        boolean ip3IsEmpty = targetEditor_ipField3.textProperty().get().isEmpty();
        boolean ip4IsEmpty = targetEditor_ipField4.textProperty().get().isEmpty();

        boolean domainORIpIsCorrect = false;

        Pattern namePattern = Pattern.compile("\\W+", Pattern.CASE_INSENSITIVE);
        Matcher mName = namePattern.matcher(targetEditor_nameField.getText().trim());
        Pattern domainPattern = Pattern.compile("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$");

        if (mName.find() | nameIsEmpty) {
            nameFormatCorrect = false;
        } else {
            nameFormatCorrect = true;
        }

        if (ip1IsEmpty | ip2IsEmpty | ip3IsEmpty | ip4IsEmpty) {
            ipFormatCorrect = false;
        } else {
            ipFormatCorrect = true;
        }

        if (domainPattern.matcher(targetEditor_domainField.textProperty().get()).find()) {
            domainFormatCorrect = true;
        } else {
            domainFormatCorrect = false;
        }

        if (ip1IsEmpty && ip2IsEmpty && ip3IsEmpty && ip4IsEmpty) {
            ipAllIsEmpty = true;
        } else {
            ipAllIsEmpty = false;
        }

        if (domainFormatCorrect || ipFormatCorrect) {
            domainORIpIsCorrect = true;
        } else {
            domainORIpIsCorrect = false;
        }

        if (nameIsEmpty) {
            setStatus(targetEditor_statusNameLabel, Color.RED, "Name is required!");
            targetEditor_nameField.setEffect(borderGlow);
        }
        if (!nameIsEmpty && !nameFormatCorrect) {
            setStatus(targetEditor_statusNameLabel, Color.RED, "Name contains non alpha-numeric characters!");
            targetEditor_nameField.setEffect(borderGlow);
        }

        if (!ipAllIsEmpty && !ipFormatCorrect) {
            setStatus(targetEditor_statusIPLabel, Color.RED, "IP Address is format is incorrect!");
            targetEditor_ipField1.setEffect(borderGlow);
            targetEditor_ipField2.setEffect(borderGlow);
            targetEditor_ipField3.setEffect(borderGlow);
            targetEditor_ipField4.setEffect(borderGlow);
        }

        if (!domainIsEmpty && !domainFormatCorrect) {
            setStatus(targetEditor_statusDomainLabel, Color.RED, "Domain format is incorrect! Must be" + '\n' + "\"www.domain.com\" or \"subdomain.domain.com\"");
            targetEditor_domainField.setEffect(borderGlow);
        }

        if (domainIsEmpty && ip1IsEmpty) {
            setStatus(targetEditor_statusDomainLabel, Color.RED, "A valid domain or IP is required!");
            targetEditor_domainField.setEffect(borderGlow);
            targetEditor_ipField1.setEffect(borderGlow);
            targetEditor_ipField2.setEffect(borderGlow);
            targetEditor_ipField3.setEffect(borderGlow);
            targetEditor_ipField4.setEffect(borderGlow);
        }

        if (domainORIpIsCorrect & nameFormatCorrect) {
            String name = null;
            String ip = null;
            String domain = null;

            if (nameFormatCorrect & !nameIsEmpty) {
                name = targetEditor_nameField.textProperty().get();
            }
            if (ipFormatCorrect & !ipAllIsEmpty) {
                ip = targetEditor_ipField1.textProperty().get() + "." + targetEditor_ipField2.textProperty().get() + "." + targetEditor_ipField3.textProperty().get() + "." + targetEditor_ipField4.textProperty().get();
            }
            if (domainFormatCorrect & !domainIsEmpty) {
                domain = targetEditor_domainField.textProperty().get();

            }
            Target t = new Target("PENDING", name, domain, ip, null, 0, "A", false);
            return t;
        } else {
            return null;
        }
    }

    /**
     * Sets a Label objects color and String value on the JavaFX UI thread.
     *
     * @param l the Label object
     * @param c the Color
     * @param s the String
     */
    public void setStatus(Label l, Color c, String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                l.setText(s);
                l.setTextFill(c);
                l.setVisible(true);
            }
        });
    }

    /**
     * Traverses focus to the next TextField of the four IP TextFields
     *
     * @param tf the currently focused TextField
     */
    public void focusNext(TextField tf) {
        Character tfID = tf.getId().charAt(tf.getId().length() - 1);
        switch (tfID) {
            case '1':
                targetEditor_ipField2.requestFocus();
                break;
            case '2':
                targetEditor_ipField3.requestFocus();
                break;
            case '3':
                targetEditor_ipField4.requestFocus();
                break;
            default:
                break;
        }
    }

    /**
     * Traverses focus to the previous TextField of the four IP TextFields.
     *
     * @param tf the currently focused TextField.
     */
    public void focusPrevious(TextField tf) {
        Character tfID = tf.getId().charAt(tf.getId().length() - 1);
        switch (tfID) {
            case '4':
                targetEditor_ipField3.requestFocus();
                break;
            case '3':
                targetEditor_ipField2.requestFocus();
                break;
            case '2':
                targetEditor_ipField1.requestFocus();
                break;
            default:
                break;
        }
    }

    /**
     * Returns the next TextField which should be focused when traversing focus
     * to the next TextField of the four IP TextFields
     *
     * @param tf the currently focused text field.
     * @return the TextField that should gain focus next.
     */
    public TextField getNextFocusableTextField(TextField tf) {
        Character tfID = tf.getId().charAt(tf.getId().length() - 1);
        TextField toReturn = null;
        switch (tfID) {
            case '1':
                toReturn = targetEditor_ipField2;
                break;
            case '2':
                toReturn = targetEditor_ipField3;
                break;
            case '3':
                toReturn = targetEditor_ipField4;
                break;
        }
        return toReturn;
    }

    /**
     * Returns the next TextField which should be focused when traversing focus
     * to the previous TextField of the four IP TextFields
     *
     * @param tf the currently focused text field.
     * @return the TextField that should gain focus next.
     */
    public TextField getPreviousFocusableTextField(TextField tf) {
        Character tfID = tf.getId().charAt(tf.getId().length() - 1);
        TextField toReturn = null;
        switch (tfID) {
            case '4':
                toReturn = targetEditor_ipField3;
                break;
            case '3':
                toReturn = targetEditor_ipField2;
                break;
            case '2':
                toReturn = targetEditor_ipField1;
                break;
        }
        return toReturn;
    }

    /**
     * Closes the Target editor according to the source of the MouseEvent.
     *
     * @param e the MouseEvent that indicates which Stage should close (Should
     * be the TargetEditorFXML.fxml).
     */
    public void closeEditor(MouseEvent e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
