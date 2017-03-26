package ftp.client;

import com.sun.xml.internal.fastinfoset.util.StringArray;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static final String DISCONNECT = "DISCONNECT";

    private static final String YES = "S";
    private static final String NO = "N";

    private DataInputStream dataConnectionInputStream;
    private DataOutputStream dataConnectionOutputStream;
    private BufferedReader bufferedReader;

    public FTPClientConnection(Socket clientSocketConection, String username, String password) {
        try {
            dataConnectionInputStream = new DataInputStream(clientSocketConection.getInputStream());
            dataConnectionOutputStream = new DataOutputStream(clientSocketConection.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            authenticate(username, password);

        } catch (IOException iOException) {
            System.err.println("Não foi possível estabelecer uma conexão " + iOException.getMessage());
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
                System.out.println("Conexão estabelecida!");
            } else {
                System.err.println("Usuário e/ou senha incorreto(s)");
                System.exit(1);
            }

        } catch (IOException iOException) {
            System.err.println("Erro ao autenticar o usuário!");
            dataConnectionOutputStream.writeUTF(DISCONNECT);
        }
    }

    public void commandMenu() {
        while (true) {
            System.out.println(STOR);
            System.out.println(RETR);
            System.out.println(LIST);
            System.out.println(DISCONNECT);
            System.out.print("Digite o comando : ");

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
                    default:
                        System.out.println("Comando não reconhecido!");

                }

            } catch (IOException iOException) {
                System.err.println("Opção inválida! Tente outra vez.");
            }

        }
    }

    private void commandLIST() {
        try {
            dataConnectionOutputStream.writeUTF(LIST);
            StringArray listFile = new StringArray();
            String filename = dataConnectionInputStream.readUTF();
            
            while (!filename.equals(SUCCESSFUL_ACTION)) {                
                System.out.println(filename);
                filename = dataConnectionInputStream.readUTF();
            }
            System.out.println("Arquivos listados com sucesso!");
            
        } catch (IOException ex) {
            System.err.println("Erro ao listar os arquivos!");
        }
    }

    private void commandDISCONNECT() {
        try {
            dataConnectionOutputStream.writeUTF(DISCONNECT);
            String reply;
            reply = dataConnectionInputStream.readUTF();
            if (reply.equals(DISCONNECT)) {
                System.out.println("Disconectado do servidor.");
            }
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Erro ao disconectar do servidor!");
        }
    }

    private void commandRETR() {
        try {
            dataConnectionOutputStream.writeUTF(RETR);
            String filename;
            System.out.println("Entre com o nome do arquivo : ");
            filename = bufferedReader.readLine();
            dataConnectionOutputStream.writeUTF(filename);
            String reply = dataConnectionInputStream.readUTF();
            if (reply.equals(FILE_NOT_FOUND)) {
                System.out.println("Arquivo não encontrado no servidor.");
            } else if (reply.equals(FILE_STATUS_OK)) {
                System.out.println("Recebendo arquivo...");
                File file = new File(filename);
                if (file.exists()) {
                    String option;
                    System.out.println("Arquivo já existe localmente, deseja sobrescrever?(S/N)");
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
                    System.out.println("Arquivo recebido com sucesso!");
                }

            }
        } catch (IOException ex) {
            System.err.println("Não foi possível enviar o arquivo.");
        }
    }

    private void commandSTOR() {
        try {
            dataConnectionOutputStream.writeUTF(STOR);
            System.out.print("Nome do arquivo : ");

            String filename = bufferedReader.readLine();

            File file = new File(filename);
            if (!file.exists()) {
                System.out.println("Arquivo não existe!");
                dataConnectionOutputStream.writeUTF(FILE_NOT_FOUND);
                return;
            }

            dataConnectionOutputStream.writeUTF(filename);

            String msmFromServer = dataConnectionInputStream.readUTF();
            switch (msmFromServer) {
                case FILE_EXIST:
                    System.out.print("Arquivo já existe, deseja sobescrever?(S/N):");
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
            System.out.println("Enviando arquivo...");
            FileInputStream fileInputStream = new FileInputStream(file);
            int piece;
            do {
                piece = fileInputStream.read();
                dataConnectionOutputStream.writeUTF(String.valueOf(piece));
            } while (piece != -1);
            fileInputStream.close();
            if (dataConnectionInputStream.readUTF().equals(SUCCESSFUL_ACTION)) {
                System.out.println("Enviado com sucesso!");
            } else {
                System.out.println("Não foi possível completar a transação!");
            }

        } catch (IOException iOException) {
            System.err.println("Erro ao enviar arquivo!");
        }
    }

}
