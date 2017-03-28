/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ftp.client;

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
    
    private String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("'['HH:mm:ss']' ");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void writeLog(String message, int OUTPUT){
        switch (OUTPUT){
            case OUT:
                System.out.println(getTimestamp() + message);
                break;
            case ERR:
                System.err.println(getTimestamp() + message);
                break;
        }
    }
}
