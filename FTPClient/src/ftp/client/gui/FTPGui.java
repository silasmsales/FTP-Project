package ftp.client.gui;

import javax.swing.JFrame;

/**
 *
 * @author silasmsales
 */
public class FTPGui {

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setTitle("SalesZilla");
        mainWindow.setSize(1024, 620);
        mainWindow.setResizable(false);
        mainWindow.setVisible(true);
    }
    
}
