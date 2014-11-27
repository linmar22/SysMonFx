/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 *
 * @author Linas Martusevicius
 * 
 * A custom logger implementation.
 */
public class LogUtil {

    public final static String INFO = "[INFO] ";
    public final static String WARNING = "[WARN] ";
    public final static String SEVERE = "[SEVE] ";
    public final static String CRITICAL = "[CRIT] ";

    TextArea console;

    public LogUtil(TextArea console) {
        this.console = console;
    }

    /**
     * Appends a message to the Overview tab's Output segment in the following
     * format: [TIME] [LEVEL] message.
     * @param level the level of severity of the message (LogUtil.LEVEL)
     * @param message a String representation of the message.
     */
    public void log(String level, String message) {
        Platform.runLater(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("[kk:mm:ss] ");

            String now = sdf.format(new Date());
            console.appendText('\n' + now + level + message);
        });
    }

}
