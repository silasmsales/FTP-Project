package ftp.client.tool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author silasmsales
 */
public class FileStream {

    private final DataOutputStream dataOutputStream;
    private final DataInputStream dataInputStream;
    private FTPUser user;
    private final FTPLogger log;

    public FileStream(DataOutputStream dataOutputStream, DataInputStream dataInputStream, FTPUser user) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.user = user;
        log = new FTPLogger();
    }

    public boolean sendFile(File file) {
        int piece;
        boolean status;
        try {

            FileInputStream fileInputStream = new FileInputStream(file);

            do {
                piece = fileInputStream.read();
                dataOutputStream.writeUTF(String.valueOf(piece));
            } while (piece != -1);

            fileInputStream.close();
            status = true;

        } catch (IOException ex) {
            log.writeLog(user, "Não foi possível transferir o arquivo!", FTPLogger.ERR);
            status = false;
        }
        return true;
    }

    public boolean getFile(File file) {
        boolean status;
        int piece;
        String temp;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            do {
                temp = dataInputStream.readUTF();
                piece = Integer.parseInt(temp);
                if (piece != -1) {
                    fileOutputStream.write(piece);
                }
            } while (piece != -1);
            fileOutputStream.close();
            status = true;
        } catch (IOException ex) {
            log.writeLog(user, "Não foi possível transferir o arquivo!", FTPLogger.ERR);
            status = false;
        }

        return status;
    }

}
