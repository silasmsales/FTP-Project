package ftp.server.admin;

import java.util.Scanner;

/**
 *
 * @author Rafa
 */
public class FTPApp {

    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String EXIT = "exit";
    private static FTPUserCad cadastro;

    public static void main(String[] args) {
        cadastro = new FTPUserCad();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String line = scanner.nextLine();
            String userInfo[] = line.split("\\s+");

            if (userInfo[0].equals(ADD)) {
                cadastro.addUser(userInfo[1], userInfo[2]);
            } else if (userInfo[0].equals(REMOVE)) {
                cadastro.removeUser(userInfo[1], userInfo[2]);
            }else if(userInfo[0].equals(EXIT)){
                return;
            }
        }

    }

}
