package ftp.client;

import ftp.client.tool.FTPLogger;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author silasmsales
 */
public class FTPClient {

/*      public static void main(String[] args) {
    FTPLogger log = new FTPLogger();
    try {
    String IPAddress = args[0];
    int portConnection = Integer.parseInt(args[1]);
    int portDataTranfer = Integer.parseInt(args[2]);
    String username = args[3];
    String password = args[4];
    
    Socket clientSocketConnection = new Socket(IPAddress, portConnection);
    Socket clientSocketData = new Socket(IPAddress, portDataTranfer);
    
    FTPClientConnection clientConection = new FTPClientConnection(clientSocketConnection, clientSocketData, username, password);
    
    clientConection.commandMenu();
    
    } catch (IOException | NumberFormatException exception) {
    log.writeLog("Não foi possível se conectar ao servidor!", FTPLogger.ERR);
    }
    }
*/
}