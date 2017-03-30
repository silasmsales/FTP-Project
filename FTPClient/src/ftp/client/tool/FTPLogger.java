/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ftp.client.tool;

import ftp.client.gui.JTextLogger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Tiago
 */
public class FTPLogger {

    public static final int ERR = -1;
    public static final int OUT = 0;
    public JTextLogger textLogger = JTextLogger.getInstance();

    private String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("'['HH:mm:ss']' ");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void writeLog(FTPUser fTPUser, String message, int OUTPUT) {
        String out = getTimestamp() + "[" + fTPUser.getUsername() + "@" + fTPUser.getIPAddress().getHostAddress() + "] " + message;
        switch (OUTPUT) {
            case OUT:
                System.out.println(out);
                break;
            case ERR:
                System.err.println(out);
                break;
        }
        writeToTextLoggerStatus(out);
    }

    public void writeLog(String message, int OUTPUT) {
        String out  = getTimestamp() + message;
        switch (OUTPUT) {
            case OUT:
                System.out.println(out);
                break;
            case ERR:
                System.err.println(out);
                break;
        }
        writeToTextLoggerStatus(out);
    }
    
    private void writeToTextLoggerStatus(String message){
        textLogger.setText(textLogger.getText() + message + "\n");
    }
    
}
