package ftp.server.net;

import ftp.server.tool.FTPLogger;
import ftp.server.tool.FTPUser;
import ftp.server.tool.FTPUsersList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silasmsales
 */
public class FTPServerConnection extends Thread {

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

    private DataOutputStream connectionOutputStream;
    private DataInputStream connectionInputStream;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private FileStream fileStream;
    private FTPUser userClient;
    private FTPLogger log;
    private Inet4Address myIPAddress;
    private Socket serverSocketConnection;
    private Socket serverSocketData;
    
    public FTPServerConnection(Socket serverSocketConnection, Socket serverSocketData) {
        try {
            
            this.serverSocketConnection = serverSocketConnection;
            this.serverSocketData = serverSocketData;
            
            connectionOutputStream = new DataOutputStream(this.serverSocketConnection.getOutputStream());
            connectionInputStream = new DataInputStream(this.serverSocketConnection.getInputStream());
            dataOutputStream = new DataOutputStream(this.serverSocketData.getOutputStream());
            dataInputStream = new DataInputStream(this.serverSocketData.getInputStream());

            myIPAddress = (Inet4Address) serverSocketConnection.getInetAddress();

            log = new FTPLogger();
            userClient = new FTPUser();
            fileStream = new FileStream(dataOutputStream, dataInputStream, userClient);

            start();
        } catch (IOException e) {
            log.writeLog("Não foi possível estabelecer uma conexão! Tente novamente.", FTPLogger.ERR);
        }
    }

    @Override
    public void run() {
        boolean stop = false;
        while (!stop) {
            try {
                log.writeLog("Esperando solicitação do cliente...", FTPLogger.OUT);
                String command = connectionInputStream.readUTF();
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
                        stop = commandDISCONNECT();
                        break;
                    case DELE:
                        commandDELE();
                        break;
                    default:
                        log.writeLog("Esperando solicitação do cliente...", FTPLogger.OUT);
                }
            } catch (IOException iOException) {
                log.writeLog("Erro ao processar o comando!", FTPLogger.ERR);
            }
            if (stop) {
                return;
            }
        }
    }

    private void commandDELE() {
        try {
            log.writeLog(userClient, "Esperando pelo nome do arquivo...", FTPLogger.OUT);
            String filename = connectionInputStream.readUTF();
            File file = new File(DIRECTORIES + userClient.getUsername() + "/" + filename);
            if (!file.exists()) {
                connectionOutputStream.writeUTF(FILE_NOT_FOUND);
                log.writeLog(userClient, "Arquivo não existe", FTPLogger.OUT);
            } else {
                file.delete();
                connectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
                log.writeLog(userClient, "Arquivo deletado com sucesso.", FTPLogger.OUT);
            }
        } catch (IOException ioe) {
            log.writeLog(userClient, "Falha ao deletar arquivo.", FTPLogger.ERR);
        }
    }

    private void commandLIST() {
        try {
            log.writeLog(userClient, "Comando LIST recebido.", FTPLogger.OUT);

            File directory = new File(DIRECTORIES.concat(userClient.getUsername()));
            File[] fileList = directory.listFiles();
            for (File file : fileList) {
                if (file.isFile()) {
                    connectionOutputStream.writeUTF(file.getName());
                }else if(file.isDirectory()){
                    connectionOutputStream.writeUTF(file.getName()+"/");
                }
            }
            connectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
            log.writeLog(userClient, "Arquivos listados com sucesso!", FTPLogger.OUT);
        } catch (IOException ex) {
            Logger.getLogger(FTPServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void commandRETR() throws FileNotFoundException {
        try {
            log.writeLog(userClient, "Comando RETR recebido.", FTPLogger.OUT);
            log.writeLog(userClient, "Esperando pelo arquivo...", FTPLogger.OUT);
            String filename = connectionInputStream.readUTF();
            File file = new File(DIRECTORIES + userClient.getUsername() + "/" + filename);
            if (!file.exists()) {
                connectionOutputStream.writeUTF(FILE_NOT_FOUND);
            } else {
                connectionOutputStream.writeUTF(FILE_STATUS_OK);

                fileStream.sendFile(file);

                connectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
                log.writeLog(userClient, "Arquivo enviado com sucesso!", FTPLogger.OUT);
            }
        } catch (IOException iOException) {
            log.writeLog(userClient, "Erro ao receber o arquivo!", FTPLogger.ERR);
        }
    }

    private void commandSTOR() {
        try {
            log.writeLog(userClient, "Comando STOR recebido.", FTPLogger.OUT);
            log.writeLog(userClient, "Esperando pelo nome do arquivo...", FTPLogger.OUT);
            String filename = connectionInputStream.readUTF();
            String action;

            if (filename.equals(FILE_NOT_FOUND)) {
                log.writeLog(userClient, "Operação cancelada pelo cliente", FTPLogger.OUT);
                return;
            }
            File file = new File(DIRECTORIES + userClient.getUsername() + "/" + filename);
            if (file.exists()) {
                connectionOutputStream.writeUTF(FILE_EXIST);
            } else {
                connectionOutputStream.writeUTF(FILE_NOT_EXIST);
            }

            String reply = connectionInputStream.readUTF();
            switch (reply) {
                case FILE_STATUS_OK:
                    fileStream.getFile(file);
                    connectionOutputStream.writeUTF(SUCCESSFUL_ACTION);
                    log.writeLog(userClient, "Arquivo recebido com sucesso!", FTPLogger.OUT);
                    break;
                case ACTION_ABORTED:
                    log.writeLog(userClient, "Ação cancelada pelo usuário.", FTPLogger.OUT);
            }

        } catch (IOException iOException) {
            log.writeLog(userClient, "Erro ao executar commando STOR!", FTPLogger.ERR);
        }
    }

    private boolean authenticate() {
        FTPUsersList usersList = new FTPUsersList();
        List<FTPUser> users = usersList.getUsersList();
        String status = CONNECTION_CLOSE;

        try {

            String username = connectionInputStream.readUTF();
            String command = connectionInputStream.readUTF();
            String password = connectionInputStream.readUTF();

            userClient.setUsername(username);
            userClient.setPassword(password);
            userClient.setIPAddress(myIPAddress);

            for (FTPUser user : users) {
                if (userClient.getUsername().equals(user.getUsername())
                 && userClient.getPassword().equals(user.getPassword())) {
                    status = LOGGED_IN;
                }
            }

            if (status.equals(LOGGED_IN)) {
                log.writeLog(userClient, "Autenticado com sucesso", FTPLogger.OUT);
                connectionOutputStream.writeUTF(LOGGED_IN);
            } else {
                log.writeLog(userClient, "Usuário e/ou senha incorreto(s)", FTPLogger.ERR);
                connectionOutputStream.writeUTF(CONNECTION_CLOSE);
                return commandDISCONNECT();
            }

        } catch (IOException iOException) {
            log.writeLog("Erro ao autenticar o usuário!", FTPLogger.ERR);
        }
        return false;
    }

    private boolean commandDISCONNECT() throws IOException {

        connectionOutputStream.writeUTF(DISCONNECT);
        dataInputStream.close();
        dataOutputStream.close();
        connectionInputStream.close();
        connectionOutputStream.close();
        serverSocketConnection.close();
        serverSocketData.close();
        
        log.writeLog(userClient, "Cliente desconectado com sucesso!", FTPLogger.OUT);

        return true;
    }

}
