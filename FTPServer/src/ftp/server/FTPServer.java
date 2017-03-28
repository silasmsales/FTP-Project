package ftp.server;

import java.io.IOException;
import java.net.ServerSocket;

public class FTPServer {

    public static void main(String[] args) {
        FTPLogger log = new FTPLogger();
        try {
            int port = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(port);
            log.writeLog("FTP Server iniciou na porta " + port, FTPLogger.OUT);
            while (true) {
                log.writeLog("Esperando conexão ...", FTPLogger.OUT);
                FTPServerConnection fTPServerConection = new FTPServerConnection(serverSocket.accept());
            }
        } catch (IOException | NumberFormatException e) {
            log.writeLog("Não foi possível iniciar o servidor!", FTPLogger.ERR);
        }
    }
}