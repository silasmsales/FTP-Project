package ftp.server.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author silasmsales
 */
public class FTPUsersList {

    private Scanner input;
    private List <FTPUser> usersList; 
    private FTPLogger logger;
    
    public FTPUsersList() {
        logger = new FTPLogger();
        try {
            input = new Scanner(new File("logins.txt"));
            usersList = new ArrayList<>();
        } catch (FileNotFoundException ex) {
            logger.writeLog("Erro ao abrir arquivo com lista de usuários!", FTPLogger.ERR);
        }
    }

    /**
     * @return the usersList
     */
    public List <FTPUser> getUsersList() {
        
        while (input.hasNext()) {            
            FTPUser user = new FTPUser();
            user.setUsername(input.next());
            user.setPassword(input.next());
            usersList.add(user);
        }
        
        return usersList;
    }

}
