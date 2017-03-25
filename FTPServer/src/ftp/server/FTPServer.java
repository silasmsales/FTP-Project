package ftp.server;

import java.net.*;

public class FTPServer {

    public static void main(String args[]) {

        try {
            int port = Integer.parseInt(args[0]);

            ServerSocket soc = new ServerSocket(port);
            System.out.println("FTP Server iniciou na porta " + port);
            while (true) {
                System.out.println("Esperando conexão ...");
                ServerConection t = new ServerConection(soc.accept());

            }
        } catch (Exception e) {
            System.err.println("Não foi possível iniciar o servidor! " + e.getMessage());
        }

    }
}
