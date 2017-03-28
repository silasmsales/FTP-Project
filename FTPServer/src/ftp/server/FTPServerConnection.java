<<<<<<< HEAD
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ftp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silasmsales
 */
public class FTPServerConnection extends Thread {

    private static final String USERNAME = "silas";
    private static final String PASSWORD = "abcd";

    private static final String FILE_NOT_FOUND = "450";
    private static final String CONNECTION_CLOSE = "426";
    private static final String FILE_EXIST = "350";
    private static final String FILE_NOT_EXIST = "351";
    private static final String LOGGED_IN = "230";
    private static final String ACTION_ABORTED = "451";
    private static final String FILE_STATUS_OK = "150";
    private static final String SUCCESSFUL_ACTION = "226";

    private static final String USER = "USER";
    private static final String PASS = "PASS";
    private static final String STOR = "STOR";
    private static final String RETR = "RETR";
    private static final String LIST = "LIST";
    private static final String DELE = "DELE";
    private static final String DISCONNECT = "DISCONNECT";

    private static final String DIRECTORIES = "./directories/";

    private Socket serverSocketConection;
    private DataOutputStream dataConnectionOutputStream;
    private DataInputStream dataConnectionInputStream;
    private FTPUser userClient;
    private FTPLogger log;
            
    public FTPServerConnection(Socket serverSocketConection) {
        try {
            this.serverSocketConection = serverSocketConection;
            dataConnectionOutputStream = new DataOutputStream(this.serverSocketConection.getOutputStream());
            dataConnectionInputStream = new DataInputStream(this.serverSocketConection.getInputStream());
            userClient = new FTPUser();
            
            log = new FTPLogger();
                        
            start();
        } catch (IOException e) {
            System.err.println("Não foi possível estabelecer uma conexão! Tente novamente.");
        }
    }

    @Override
    public void run() {
        boolean stop = false;
        while (!stop) {
            try {
                String command = dataConnectionInputStream.readUTF();
                switch (command) {
                    case USER:
                        stop = authenticate();
                        break;
                    case STOR:
                        commandSTOR();
                        break;
                    case RETR:
                        commandRETR();
                        break;
                    case LIST:
                        commandLIST();
                        break;
                    case DISCONNECT:
                        stop = closeConnection();
                        break;
                    case DELE:
                        commandDELE();
                        break;
                    default:
                        System.out.println("Comando não reconhecido!");
                }
            } catch (IOException iOException) {
                System.err.println("Erro ao processar o comando!");
            }
            if (stop) {
                return;
            }
        }
    }

    private void commandDELE(){
        try {
            log.writeLog(userClient, "Esperando pelo nome do arquivo...", FTPLogger.OUT);
            String filename = dataConnectionInputStream.readUTF();
            File file = new File(DIRECTORIES+userClient.getUsername()+"/"+filename);
            System.err.println(file);
            if (!file.exists()) {
                dataConnectionOutputStream.writeUTF(FILE_NOT_FOUND);
                log.writeLog(userClient, "Arquivo não existe", FTPLogger.OUT);
            }else{
                file.delete();
                dataConnectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
                log.writeLog(userClient, "Arquivo deletado com sucesso.", FTPLogger.OUT);
            }
        } catch (IOException ioe) {
            log.writeLog(userClient, "Falha ao deletar arquivo.", FTPLogger.ERR);
        }
    }
    
    private void commandLIST() {
        try {
            File directory = new File(DIRECTORIES.concat(userClient.getUsername()));
            File[] fileList = directory.listFiles();
            for (File file : fileList) {
                if (file.isFile()) {
                    dataConnectionOutputStream.writeUTF(file.getName());
                }
            }
            dataConnectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
            System.out.println("Arquivos listados com sucesso!");
        } catch (IOException ex) {
            Logger.getLogger(FTPServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void commandRETR() throws FileNotFoundException {
        try {
            System.out.println("Esperando pelo arquivo...");
            String filename = dataConnectionInputStream.readUTF();
            File file = new File(DIRECTORIES +  userClient.getUsername() + "/" +filename);
            if (!file.exists()) {
                dataConnectionOutputStream.writeUTF(FILE_NOT_FOUND);
            } else {
                dataConnectionOutputStream.writeUTF(FILE_STATUS_OK);
                FileInputStream fileInputStream = new FileInputStream(file);
                int piece;
                do {
                    piece = fileInputStream.read();
                    dataConnectionOutputStream.writeUTF(String.valueOf(piece));
                } while (piece != -1);
                fileInputStream.close();
                dataConnectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
                System.out.println("Arquivo enviado com sucesso!");
            }
        } catch (IOException iOException) {
            System.err.println("Erro ao receber o aquivo!");
        }
    }

    private void commandSTOR() {
        try {
            System.out.println("Esperando pelo nome do arquivo...");
            String filename = dataConnectionInputStream.readUTF();
            String action;

            if (filename.equals(FILE_NOT_FOUND)) {
                System.out.println("Operação cancelada pelo cliente");
                return;
            }
            File file = new File(DIRECTORIES + userClient.getUsername() + "/" + filename);
            if (file.exists()) {
                dataConnectionOutputStream.writeUTF(FILE_EXIST);
            } else {
                dataConnectionOutputStream.writeUTF(FILE_NOT_EXIST);
            }

            String proceed = dataConnectionInputStream.readUTF();
            switch (proceed) {
                case FILE_STATUS_OK:
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    int piece;
                    String trans;
                    do {
                        trans = dataConnectionInputStream.readUTF();
                        piece = Integer.parseInt(trans);
                        if (piece != -1) {
                            fileOutputStream.write(piece);
                        }
                    } while (piece != -1);
                    fileOutputStream.close();
                    dataConnectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
                    System.out.println("Arquivo recebido com sucesso!");
                    break;
                case ACTION_ABORTED:
                    System.out.println("Ação cancelada pelo usuário.");
            }

        } catch (IOException iOException) {
            System.err.println("Erro ao executar commando STOR!");
        }
    }

    private boolean authenticate() {
        FTPUsersList usersList = new FTPUsersList();
        List<FTPUser> users = usersList.getUsersList();
        String status = CONNECTION_CLOSE;

        try {

            String username = dataConnectionInputStream.readUTF();
            String command = dataConnectionInputStream.readUTF();
            String password = dataConnectionInputStream.readUTF();

            userClient.setUsername(username);
            userClient.setPassword(password);
            userClient.setIPAddress((Inet4Address) serverSocketConection.getInetAddress());

            for (FTPUser user : users) {
                if (userClient.getUsername().equals(user.getUsername())
                        && userClient.getPassword().equals(user.getPassword())) {
                    status = LOGGED_IN;
                }
            }

            if (status.equals(LOGGED_IN)) {
                log.writeLog(userClient, "Autenticado com sucesso", FTPLogger.OUT);
                dataConnectionOutputStream.writeUTF(LOGGED_IN);
            } else {
                System.err.println("Usuário e/ou senha incorreto(s)");
                dataConnectionOutputStream.writeUTF(CONNECTION_CLOSE);
                return closeConnection();
            }

        } catch (IOException iOException) {
            System.err.println("Erro ao autenticar o usuário!");
        }
        return false;
    }

    private boolean closeConnection() throws IOException {
        System.out.println("Cliente disconectado com sucesso!");
        dataConnectionOutputStream.writeUTF(DISCONNECT);
        dataConnectionInputStream.close();
        dataConnectionOutputStream.close();
        return true;
    }

}
=======
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ftp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silasmsales
 */
public class FTPServerConnection extends Thread {

    private static final String USERNAME = "silas";
    private static final String PASSWORD = "abcd";

    private static final String FILE_NOT_FOUND = "450";
    private static final String CONNECTION_CLOSE = "426";
    private static final String FILE_EXIST = "350";
    private static final String FILE_NOT_EXIST = "351";
    private static final String LOGGED_IN = "230";
    private static final String ACTION_ABORTED = "451";
    private static final String FILE_STATUS_OK = "150";
    private static final String SUCCESSFUL_ACTION = "226";

    private static final String USER = "USER";
    private static final String PASS = "PASS";
    private static final String STOR = "STOR";
    private static final String RETR = "RETR";
    private static final String LIST = "LIST";
    private static final String DELE = "DELE";
    private static final String DISCONNECT = "DISCONNECT";

    private static final String DIRECTORIES = "./directories/";

    private Socket serverSocketConection;
    private DataOutputStream dataConnectionOutputStream;
    private DataInputStream dataConnectionInputStream;
    private FTPUser userClient;
    private FTPLogger log;
            
    public FTPServerConnection(Socket serverSocketConection) {
        try {
            this.serverSocketConection = serverSocketConection;
            dataConnectionOutputStream = new DataOutputStream(this.serverSocketConection.getOutputStream());
            dataConnectionInputStream = new DataInputStream(this.serverSocketConection.getInputStream());
            userClient = new FTPUser();
            
            log = new FTPLogger();
                        
            start();
        } catch (IOException e) {
            System.err.println("Não foi possível estabelecer uma conexão! Tente novamente.");
        }
    }

    @Override
    public void run() {
        boolean stop = false;
        while (!stop) {
            try {
                System.out.println("Esperando solicitação do cliente...");
                String command = dataConnectionInputStream.readUTF();
                switch (command) {
                    case USER:
                        stop = authenticate();
                        break;
                    case STOR:
                        commandSTOR();
                        break;
                    case RETR:
                        commandRETR();
                        break;
                    case LIST:
                        commandLIST();
                        break;
                    case DISCONNECT:
                        stop = closeConnection();
                        break;
                    case DELE:
                        commandDELE();
                        break;
                    default:
                        System.out.println("Comando não reconhecido!");
                }
            } catch (IOException iOException) {
                System.err.println("Erro ao processar o comando!");
            }
            if (stop) {
                return;
            }
        }
    }

    private void commandDELE(){
        try {
            log.writeLog(userClient, "Esperando pelo nome do arquivo...", FTPLogger.OUT);
            log.writeLog(userClient, "Esperando pelo nome do arquivo...", FTPLogger.OUT);
            String filename = dataConnectionInputStream.readUTF();
            File file = new File(DIRECTORIES+userClient.getUsername()+"/"+filename);
            System.err.println(file);
            if (!file.exists()) {
                dataConnectionOutputStream.writeUTF(FILE_NOT_FOUND);
                log.writeLog(userClient, "Arquivo não existe", FTPLogger.OUT);
            }else{
                file.delete();
                dataConnectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
                log.writeLog(userClient, "Arquivo deletado com sucesso.", FTPLogger.OUT);
            }
        } catch (IOException ioe) {
            log.writeLog(userClient, "Falha ao deletar arquivo.", FTPLogger.ERR);
        }
    }
    
    private void commandLIST() {
        try {
            File directory = new File(DIRECTORIES.concat(userClient.getUsername()));
            File[] fileList = directory.listFiles();
            for (File file : fileList) {
                if (file.isFile()) {
                    dataConnectionOutputStream.writeUTF(file.getName());
                }
            }
            dataConnectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
            System.out.println("Arquivos listados com sucesso!");
        } catch (IOException ex) {
            Logger.getLogger(FTPServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void commandRETR() throws FileNotFoundException {
        try {
            System.out.println("Esperando pelo arquivo...");
            String filename = dataConnectionInputStream.readUTF();
            File file = new File(DIRECTORIES +  userClient.getUsername() + "/" +filename);
            if (!file.exists()) {
                dataConnectionOutputStream.writeUTF(FILE_NOT_FOUND);
            } else {
                dataConnectionOutputStream.writeUTF(FILE_STATUS_OK);
                FileInputStream fileInputStream = new FileInputStream(file);
                int piece;
                do {
                    piece = fileInputStream.read();
                    dataConnectionOutputStream.writeUTF(String.valueOf(piece));
                } while (piece != -1);
                fileInputStream.close();
                dataConnectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
                System.out.println("Arquivo enviado com sucesso!");
            }
        } catch (IOException iOException) {
            System.err.println("Erro ao receber o aquivo!");
        }
    }

    private void commandSTOR() {
        try {
            System.out.println("Esperando pelo nome do arquivo...");
            String filename = dataConnectionInputStream.readUTF();
            String action;

            if (filename.equals(FILE_NOT_FOUND)) {
                System.out.println("Operação cancelada pelo cliente");
                return;
            }
            File file = new File(DIRECTORIES + userClient.getUsername() + "/" + filename);
            if (file.exists()) {
                dataConnectionOutputStream.writeUTF(FILE_EXIST);
            } else {
                dataConnectionOutputStream.writeUTF(FILE_NOT_EXIST);
            }

            String proceed = dataConnectionInputStream.readUTF();
            switch (proceed) {
                case FILE_STATUS_OK:
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    int piece;
                    String trans;
                    do {
                        trans = dataConnectionInputStream.readUTF();
                        piece = Integer.parseInt(trans);
                        if (piece != -1) {
                            fileOutputStream.write(piece);
                        }
                    } while (piece != -1);
                    fileOutputStream.close();
                    dataConnectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
                    System.out.println("Arquivo recebido com sucesso!");
                    break;
                case ACTION_ABORTED:
                    System.out.println("Ação cancelada pelo usuário.");
            }

        } catch (IOException iOException) {
            System.err.println("Erro ao executar commando STOR!");
        }
    }

    private boolean authenticate() {
        FTPUsersList usersList = new FTPUsersList();
        List<FTPUser> users = usersList.getUsersList();
        String status = CONNECTION_CLOSE;

        try {

            String username = dataConnectionInputStream.readUTF();
            String command = dataConnectionInputStream.readUTF();
            String password = dataConnectionInputStream.readUTF();

            userClient.setUsername(username);
            userClient.setPassword(password);
            userClient.setIPAddress((Inet4Address) serverSocketConection.getInetAddress());

            for (FTPUser user : users) {
                if (userClient.getUsername().equals(user.getUsername())
                        && userClient.getPassword().equals(user.getPassword())) {
                    status = LOGGED_IN;
                }
            }

            if (status.equals(LOGGED_IN)) {
                log.writeLog(userClient, "Autenticado com sucesso", FTPLogger.OUT);
                dataConnectionOutputStream.writeUTF(LOGGED_IN);
            } else {
                System.err.println("Usuário e/ou senha incorreto(s)");
                dataConnectionOutputStream.writeUTF(CONNECTION_CLOSE);
                return closeConnection();
            }

        } catch (IOException iOException) {
            System.err.println("Erro ao autenticar o usuário!");
        }
        return false;
    }

    private boolean closeConnection() throws IOException {
        System.out.println("Cliente disconectado com sucesso!");
        dataConnectionOutputStream.writeUTF(DISCONNECT);
        dataConnectionInputStream.close();
        dataConnectionOutputStream.close();
        return true;
    }

}
>>>>>>> c077926b96f6fc8e3cfc85f4dc4a641dbe916aa2
