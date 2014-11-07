/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 *
 * @author root
 */
public class LogUtil {
    
    public final static String INFO     = "[INFO] ";
    public final static String WARNING  = "[WARN] ";
    public final static String SEVERE   = "[SEVE] ";
    public final static String CRITICAL = "[CRIT] ";
    
    TextArea console;
    
    public LogUtil(TextArea console){
        this.console = console;
    }
    
    public void log(String level, String message){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                console.appendText('\n'+"> "+level + message);
            }
        });
    }
    
}
