package ftp.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Socket;

/**
 *
 * @author silasmsales
 */
class FTPClientConnection {

    private static final String CONNECTION_CLOSE = "426";
    private static final String LOGGED_IN = "230";
    private static final String ACTION_ABORTED = "451";
    private static final String FILE_NOT_FOUND = "450";
    private static final String FILE_EXIST = "350";
    private static final String FILE_NOT_EXIST = "351";
    private static final String FILE_STATUS_OK = "150";
    private static final String SUCCESSFUL_ACTION = "226";

    private static final String USER = "USER";
    private static final String PASS = "PASS";
    private static final String STOR = "STOR";
    private static final String RETR = "RETR";
    private static final String LIST = "LIST";
    private static final String DELE = "DELE";
    private static final String DISCONNECT = "DISCONNECT";

    private static final String YES = "S";
    private static final String NO = "N";

    private DataInputStream dataConnectionInputStream;
    private DataOutputStream dataConnectionOutputStream;
    private BufferedReader bufferedReader;
    private FTPUser userClient;
    private FTPLogger log;

    public FTPClientConnection(Socket clientSocketConection, String username, String password) {
        log = new FTPLogger();
        try {
            dataConnectionInputStream = new DataInputStream(clientSocketConection.getInputStream());
            dataConnectionOutputStream = new DataOutputStream(clientSocketConection.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            userClient = new FTPUser(username, password, (Inet4Address) clientSocketConection.getInetAddress());

            authenticate(userClient.getUsername(), userClient.getPassword());

        } catch (IOException iOException) {
            log.writeLog("Não foi possível estabelecer uma conexão " + iOException.getMessage(), FTPLogger.ERR);
        }
    }

    private void authenticate(String username, String password) throws IOException {
        try {
            dataConnectionOutputStream.writeUTF(USER);
            dataConnectionOutputStream.writeUTF(username);
            dataConnectionOutputStream.writeUTF(PASS);
            dataConnectionOutputStream.writeUTF(password);

            String reply = dataConnectionInputStream.readUTF();

            if (reply.equals(LOGGED_IN)) {
                log.writeLog("Conexão estabelecida!", FTPLogger.OUT);
            } else {
                log.writeLog("Usuário e/ou senha incorreto(s)", FTPLogger.ERR);
                System.exit(1);
            }

        } catch (IOException iOException) {
            log.writeLog("Erro ao autenticar o usuário!", FTPLogger.ERR);
            dataConnectionOutputStream.writeUTF(DISCONNECT);
        }
    }

    public void commandMenu() {
        while (true) {
            log.writeLog("Digite o comando : ", FTPLogger.OUT);
            String choice;

            try {
                choice = bufferedReader.readLine();

                switch (choice) {
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
                        commandDISCONNECT();
                        break;
                    case DELE:
                        commandDELETE();
                        break;
                    default:
                        log.writeLog("Comando não reconhecido!", FTPLogger.OUT);
                }

            } catch (IOException iOException) {
                log.writeLog("Opção inválida! Tente outra vez.", FTPLogger.ERR);
            }

        }
    }

    private void commandDELETE() {
        try {
            dataConnectionOutputStream.writeUTF(DELE);
            String filename;
            log.writeLog("Entre com o nome do arquivo a ser deletado :", FTPLogger.OUT);
            filename = bufferedReader.readLine();
            dataConnectionOutputStream.writeUTF(filename);
            String reply = dataConnectionInputStream.readUTF();
            if (reply.equals(FILE_NOT_FOUND)) {
                log.writeLog("Arquivo não encontrado no servidor.", FTPLogger.OUT);
            } else if (reply.equals(SUCCESSFUL_ACTION)) {
                log.writeLog("Arquivo deletado com sucesso.", FTPLogger.OUT);
            }

        } catch (IOException ex) {
            log.writeLog("Não foi possível deletar o arquivo.", FTPLogger.ERR);
        }
    }

    private void commandLIST() {
        try {
            dataConnectionOutputStream.writeUTF(LIST);
            String filename = dataConnectionInputStream.readUTF();

            while (!filename.equals(SUCCESSFUL_ACTION)) {
                System.out.println(filename);
                filename = dataConnectionInputStream.readUTF();
            }
            log.writeLog("Arquivos listados com sucesso!", FTPLogger.OUT);
        } catch (IOException ex) {
            log.writeLog("Erro ao listar os arquivos!", FTPLogger.ERR);
        }
    }

    private void commandDISCONNECT() {
        try {
            dataConnectionOutputStream.writeUTF(DISCONNECT);
            String reply;
            reply = dataConnectionInputStream.readUTF();
            if (reply.equals(DISCONNECT)) {
                log.writeLog("Disconectado do servidor.", FTPLogger.OUT);
            }
            System.exit(1);
        } catch (IOException ex) {
            log.writeLog("Erro ao disconectar do servidor!", FTPLogger.ERR);
        }
    }

    private void commandRETR() {
        try {
            dataConnectionOutputStream.writeUTF(RETR);
            String filename;
            log.writeLog("Entre com o nome do arquivo : ", FTPLogger.OUT);
            filename = bufferedReader.readLine();
            dataConnectionOutputStream.writeUTF(filename);
            String reply = dataConnectionInputStream.readUTF();
            if (reply.equals(FILE_NOT_FOUND)) {
                log.writeLog("Arquivo não encontrado no servidor.", FTPLogger.OUT);
            } else if (reply.equals(FILE_STATUS_OK)) {
                log.writeLog("Recebendo arquivo...", FTPLogger.OUT);
                File file = new File(filename);
                if (file.exists()) {
                    String option;
                    log.writeLog("Arquivo já existe localmente, deseja sobrescrever?(S/N)", FTPLogger.OUT);
                    option = bufferedReader.readLine();
                    if (option.equals(NO)) {
                        dataConnectionOutputStream.flush();
                        return;
                    }
                }
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
                if (dataConnectionInputStream.readUTF().equals(SUCCESSFUL_ACTION)) {
                    log.writeLog("Arquivo recebido com sucesso!", FTPLogger.OUT);
                }

            }
        } catch (IOException ex) {
            log.writeLog("Não foi possível receber o arquivo.", FTPLogger.ERR);
        }
    }

    private void commandSTOR() {
        try {
            dataConnectionOutputStream.writeUTF(STOR);
            log.writeLog("Nome do arquivo : ", FTPLogger.OUT);
            String filename = bufferedReader.readLine();

            File file = new File(filename);
            if (!file.exists()) {
                log.writeLog("Arquivo não existe!", FTPLogger.OUT);
                dataConnectionOutputStream.writeUTF(FILE_NOT_FOUND);
                return;
            }

            dataConnectionOutputStream.writeUTF(filename);

            String msmFromServer = dataConnectionInputStream.readUTF();
            switch (msmFromServer) {
                case FILE_EXIST:
                    log.writeLog("Arquivo já existe, deseja sobescrever?(S/N):", FTPLogger.OUT);
                    String option = bufferedReader.readLine();
                    if (option.equals(YES)) {
                        dataConnectionOutputStream.writeUTF(FILE_STATUS_OK);
                    } else {
                        dataConnectionOutputStream.writeUTF(ACTION_ABORTED);
                        return;
                    }
                    break;
                case FILE_NOT_EXIST:
                    dataConnectionOutputStream.writeUTF(FILE_STATUS_OK);
                    break;
            }
            log.writeLog("Enviando arquivo...", FTPLogger.OUT);
            FileInputStream fileInputStream = new FileInputStream(file);
            int piece;
            do {
                piece = fileInputStream.read();
                dataConnectionOutputStream.writeUTF(String.valueOf(piece));
            } while (piece != -1);
            fileInputStream.close();
            if (dataConnectionInputStream.readUTF().equals(SUCCESSFUL_ACTION)) {
                log.writeLog("Enviado com sucesso!", FTPLogger.OUT);
            } else {
                log.writeLog("Não foi possível completar a transação!", FTPLogger.OUT);
            }

        } catch (IOException iOException) {
            log.writeLog("Erro ao enviar arquivo!", FTPLogger.ERR);
        }
    }

}
