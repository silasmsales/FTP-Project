/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ftp.client.gui;

import javax.swing.JTextArea;

/**
 *
 * @author silasmsales
 */
public class JTextLogger extends JTextArea{
    private static JTextLogger logger;

    private JTextLogger() {
        setRows(8);
    }
    
    public static  JTextLogger getInstance(){
        if (logger == null) {
            logger = new JTextLogger();
        }
        return logger;
    }
    
}
