package ftpgui;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 *
 * @author silasmsales
 */
public class MainWindow extends JFrame{

    private BorderLayout layout;

    private JTextField textUser;
    private JPasswordField textPassword;
    private JLabel labelUser, labelPassword;
    private JLabel labelStatus;
    private JButton buttonLogin;
    private JPanel panelLogin, panelFiles, panelButtons;
    private JList listClient, listServer;
    private JButton buttonUpload, buttonDownload, buttonDelete;

    private final String [] clientFiles = {"file1.txt","file2.txt","file.odt","main.c","urban.java"};
    private final String [] serverFiles = {"file1.txt","file2.txt","file.odt","main.c","urban.java"};
    
    
    public MainWindow() {
        addGUIComponents();
        
        
        
    }

    public void addGUIComponents(){
        layout = new BorderLayout(100, 100);

        textUser = new JTextField(40);
        textPassword = new JPasswordField(30);
        buttonLogin = new JButton("Conectar");
        panelLogin = new JPanel();
        panelFiles = new JPanel();
        labelUser = new JLabel("Usuário");
        labelPassword = new JLabel("Senha");
        labelStatus = new JLabel("Aguardando mudanças...");
        listClient = new JList(clientFiles);
        listServer = new JList(serverFiles);
        buttonUpload = new JButton("Enviar >>");
        buttonDownload = new JButton("<< Receber");
        buttonDelete = new JButton("Deletar");
        panelButtons = new JPanel(new BorderLayout());
        
        
        panelLogin.setLayout(new FlowLayout());
        panelLogin.setBackground(Color.lightGray);
        panelLogin.add(labelUser);
        panelLogin.add(textUser);
        panelLogin.add(labelPassword);
        panelLogin.add(textPassword);
        panelLogin.add(buttonLogin);
        
        listClient.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listClient.setVisibleRowCount(35);
        listClient.setFixedCellWidth(430);
        listClient.setFixedCellHeight(15);

        listServer.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listServer.setVisibleRowCount(35);
        listServer.setFixedCellWidth(430);
        listServer.setFixedCellHeight(15);
        
        panelFiles.setLayout(new FlowLayout());
        panelFiles.add(new JScrollPane(listClient));

        panelButtons.add(buttonUpload, BorderLayout.NORTH);
        panelButtons.add(buttonDownload, BorderLayout.CENTER);
        panelButtons.add(buttonDelete, BorderLayout.SOUTH);
        
        panelFiles.add(panelButtons);
        
        
        panelFiles.add(new JScrollPane(listServer));

        add(panelLogin, BorderLayout.NORTH);
        add(panelFiles, BorderLayout.CENTER);
        add(labelStatus, BorderLayout.SOUTH);
        
    }
    
}
