/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ftp.client.gui;

import javax.swing.JButton;

/**
 *
 * @author silasmsales
 */
public class JFlipButton extends JButton{
    
    public static final int CONNECT = 1;
    public static final int DISCONNECT = 0;
    private final String CONNECTED_D = "Conectar";
    private final String NOT_CONNECTED_D = "Desconectar";
    
    private int status;

    public JFlipButton(int status) {
        this.status = status;
        if(this.status == CONNECT)
            this.setText(CONNECTED_D);
        else
            this.setText(NOT_CONNECTED_D);
    }
    
    public void setStatus(int status){
        this.status = status;
    }
    
    public void flipStatus(){
        if (status == CONNECT)
        {
            status = DISCONNECT;
            this.setText("Desconectar");
        }
        else
        {
            status = CONNECT;
            this.setText("Conectar");
        }
    }
    
    public int getStatus(){
        return this.status;
    }
}
