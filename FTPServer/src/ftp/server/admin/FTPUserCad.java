package ftp.server.admin;

import ftp.server.tool.FTPLogger;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.util.Scanner;

/**
 *
 * @author Rafa
 */
public class FTPUserCad {

    private final String DIRECTORIES = "./directories";
    private final String USERS_FILE = "./logins.txt";
    private FTPLogger logger;

    public FTPUserCad() {
        logger = new FTPLogger();
    }

    public void removeUser(String username, String password) {
        String newFile = "";
        
        try {
            File file = new File(USERS_FILE);
            Scanner scanner = new Scanner(file);
            while(scanner.hasNext()){
                String tmp = scanner.nextLine();
                if (!tmp.equals(username +" "+password)) {
                    newFile += tmp+"\n";
                }
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(newFile);
            fileWriter.close();
            
            Runtime.getRuntime().exec("rm -rf "+DIRECTORIES+"/"+username);
            
            logger.writeLog("Usuário "+username+" deletado com sucesso.", FTPLogger.OUT);
            
        } catch (Exception iOException) {
            logger.writeLog("Erro ao deleter o usuário "+username, 0);
        }
    } 
    
    public void addUser(String username, String password) {

        try {

            File file = new File(USERS_FILE);
            FileWriter fw = new FileWriter(file, true);
            try ( //
                    BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(username + " " + password + "\n");

                boolean success = (new File(DIRECTORIES + "/" + username)).mkdir();
                if (success) {
                    logger.writeLog("Usuário " + username + " cadastrado com sucesso", FTPLogger.OUT);
                }

            }

        } catch (IOException ioe) {
            logger.writeLog("Erro ao cadastrar novo usuário", FTPLogger.ERR);
        }
    }

}
