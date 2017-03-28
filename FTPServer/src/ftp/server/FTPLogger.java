package ftp.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FTPLogger {

    public static final int ERR = -1;
    public static final int OUT = 0;
    
    private String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("'['HH:mm:ss']' ");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void writeLog(FTPUser fTPUser, String message, int OUTPUT){
        switch (OUTPUT){
            case OUT:
                System.out.println(getTimestamp()+"["+fTPUser.getIPAddress()+"@"+fTPUser.getUsername()+"]" + message);
                break;
            case ERR:
                System.err.println(getTimestamp()+"["+fTPUser.getIPAddress()+"@"+fTPUser.getUsername()+"]" + message);
                break;
        }
    }
    
}
