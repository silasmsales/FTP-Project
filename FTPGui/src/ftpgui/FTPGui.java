/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ftpgui;

import javax.swing.JFrame;

/**
 *
 * @author silasmsales
 */
public class FTPGui {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setSize(1024, 620);
        mainWindow.setResizable(false);
        mainWindow.setVisible(true);
    }
    
}
