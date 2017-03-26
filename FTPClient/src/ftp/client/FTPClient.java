package ftp.client;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author silasmsales
 */
public class FTPClient {

    public static void main(String[] args) {
        try {
            String IPAddress = args[0];
            int port = Integer.parseInt(args[1]);
            String username = args[2];
            String password = args[3];

            Socket clientSocketConnection = new Socket(IPAddress, port);

            FTPClientConnection clientConection = new FTPClientConnection(clientSocketConnection, username, password);

            clientConection.commandMenu();

        } catch (IOException | NumberFormatException exception) {
            System.err.println("Não foi possível se conectar ao servidor!");
        }
    }

}
