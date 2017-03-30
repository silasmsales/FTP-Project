package ftp.server.net;

import ftp.server.tool.FTPLogger;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer {

    public static void main(String[] args) {
        FTPLogger log = new FTPLogger();
        try {
            int portConnection = Integer.parseInt(args[0]);
            int portDataTransfer = Integer.parseInt(args[1]);
            
            ServerSocket serverSocketConnection = new ServerSocket(portConnection);
            ServerSocket serverSocketDataTransfer = new ServerSocket(portDataTransfer);
            
            log.writeLog("FTP Server iniciado na porta " + portConnection, FTPLogger.OUT);
            while (true) {
                log.writeLog("Esperando conexão ...", FTPLogger.OUT);
                
                Socket socketConnection = serverSocketConnection.accept();
                Socket socketData = serverSocketDataTransfer.accept();
                
                FTPServerConnection fTPServerConection = new FTPServerConnection(socketConnection, socketData);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println(e.getCause());
            log.writeLog("Não foi possível iniciar o servidor!", FTPLogger.ERR);
        }
    }
}