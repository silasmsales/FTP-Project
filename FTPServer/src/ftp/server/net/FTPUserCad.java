package ftp.server.net;

import ftp.server.tool.FTPUser;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.util.Scanner;

/**
 *
 * @author Rafa
 */
public class FTPUserCad {

    public static void addUser() {

        try {
            Scanner in = new Scanner(System.in);
            System.out.println("Entre com usuário e senha: ");
            String user = in.nextLine();

            File file = new File("logins.txt");
            //adiciona a entrada ao arquivo txt
            FileWriter fw = new FileWriter(file, true);
            try ( //
                    BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(user);
                //fechando stream de bufferedWriter

                /*File diretorio = new File("/directories");
             diretorio.mkdir();*/
                String strDiretorio = user;
                boolean success = (new File(strDiretorio)).mkdir();
                if (success) {
                    System.out.println("Diretorio: " + strDiretorio + " criado");
                }

            }

            System.out.println("User cadastrado com sucesso!");

        } catch (IOException ioe) {
            System.out.println("Erro! ");
        }
    }

    public static boolean stopServ(FTPUser user) {

        boolean success = false;
        if (user != null) {
            try {
                System.out.println("Desconectando do servidor...");
                //user.closeServer();
                System.out.println("Desconectado do servidor.");
                success = true;
            } catch (Exception e) {
                System.out.println("Falha ao desconectar do servidor. ");
            }
        }
        return success;
    }

    /**
     *
     */
    public static void Menu() {

        System.out.println("1 - Adicionar user ");
        System.out.println("2 - Parar server ");
        System.out.println(" Digite a opção : ");

        int escolha = 0;
        Scanner entrada = new Scanner(System.in);

        switch (escolha) {
            case 1:
                addUser();
                break;
            case 2:
                stopServ();
                break;

            default:
                System.out.println("Comando não reconhecido!");
        }
    }

    private static void stopServ() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
