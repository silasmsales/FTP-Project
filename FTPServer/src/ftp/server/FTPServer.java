package ftp.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 *
 * @author silasmsales
 */
public class FTPServer {

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("FTP Server iniciou na porta " + port);
            while (true) {
                System.out.println("Esperando conexão ...");
                FTPServerConnection fTPServerConection = new FTPServerConnection(serverSocket.accept());
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Não foi possível iniciar o servidor!");
        }
    }

}
