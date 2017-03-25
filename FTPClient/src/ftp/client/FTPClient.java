package ftp.client;

import java.net.*;

class FTPClient {

    public static void main(String args[]) {

        try {
            String IPAddress = args[0];
            int port = Integer.parseInt(args[1]);

            Socket soc = new Socket(IPAddress, port);
            ClientConection t = new ClientConection(soc);
            t.displayMenu();

        } catch (Exception e) {
            System.err.println("Não foi possível iniciar uma conexão! " + e.getMessage());
        }

    }
}
