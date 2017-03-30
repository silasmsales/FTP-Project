package ftp.client.gui;

import ftp.client.net.FTPClientConnection;
import ftp.client.tool.FTPLogger;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import sun.awt.WindowClosingListener;
import sun.awt.WindowClosingSupport;

/**
 *
 * @author silasmsales
 */
public class MainWindow extends JFrame implements ActionListener {

    private FTPClientConnection clientConnection;
    private final FTPLogger logger;
    private Socket clientSocketConnection;
    private Socket clientSocketData;

    private BorderLayout layout;
    private JLabel labelUser, labelPassword;
    private JTextField textUser, textIPaddress, textPortConnection, textPortData;
    private JPasswordField textPassword;
    private JTextArea textStatus;
    private JFlipButton buttonLogin;
    private JPanel panelLogin, panelFiles, panelButtons;
    private JList listClient, listServer;
    private JButton buttonUpload, buttonDownload, buttonDelete;

    private String[] clientFiles;
    private String[] serverFiles = {"Sem arquivos para mostrar"};

    public MainWindow() {

        logger = new FTPLogger();

        clientFiles = this.getLocalFiles();
        addGUIComponents();

    }

    public void addGUIComponents() {
        layout = new BorderLayout(50, 50);

        textUser = new JTextField("silas", 25);
        textPassword = new JPasswordField("abcd", 15);
        textIPaddress = new JTextField("127.0.0.1", 10);
        textPortConnection = new JTextField("2021", 5);
        textPortData = new JTextField("2020", 5);
        buttonLogin = new JFlipButton(JFlipButton.CONNECT);
        panelLogin = new JPanel();
        panelFiles = new JPanel();
        labelUser = new JLabel("Usuário");
        labelPassword = new JLabel("Senha");
        textStatus = new JTextArea();
        listClient = new JList(clientFiles);
        listServer = new JList(serverFiles);
        buttonUpload = new JButton("Enviar >>");
        buttonDownload = new JButton("<< Receber");
        buttonDelete = new JButton("Deletar");
        panelButtons = new JPanel(new BorderLayout());

        buttonLogin.addActionListener(this);
        buttonDelete.addActionListener(this);
        buttonUpload.addActionListener(this);
        buttonDownload.addActionListener(this);

        panelLogin.setLayout(new FlowLayout());
        panelLogin.setBackground(Color.lightGray);
        panelLogin.add(labelUser);
        panelLogin.add(textUser);
        panelLogin.add(labelPassword);
        panelLogin.add(textPassword);
        panelLogin.add(textIPaddress);
        panelLogin.add(textPortConnection);
        panelLogin.add(textPortData);
        panelLogin.add(buttonLogin);

        listClient.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listClient.setVisibleRowCount(25);
        listClient.setFixedCellWidth(430);

        listServer.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listServer.setVisibleRowCount(25);
        listServer.setFixedCellWidth(430);

        textStatus.setRows(8);
        
        panelFiles.setLayout(new FlowLayout());
        panelFiles.add(new JScrollPane(listClient));
        panelButtons.add(buttonUpload, BorderLayout.NORTH);
        panelButtons.add(buttonDownload, BorderLayout.CENTER);
        panelButtons.add(buttonDelete, BorderLayout.SOUTH);
        panelFiles.add(panelButtons);
        panelFiles.add(new JScrollPane(listServer));

        add(panelLogin, BorderLayout.NORTH);
        add(panelFiles, BorderLayout.CENTER);
        add(new JScrollPane(textStatus), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                if (clientConnection != null) {
                    clientConnection.commandDISCONNECT();
                }
            }

        });

    }

    private String[] getLocalFiles() {

        File directory = new File("./");
        File[] fileList = directory.listFiles();
        String files[] = new String[fileList.length];
        int count = 0;

        for (File file : fileList) {
            if (file.isFile()) {
                files[count++] = file.getName();
            }
        }
        files = Arrays.copyOf(files, fileList.length - (fileList.length - count));
        return files;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        String username = textUser.getText();
        String password = textPassword.getText();
        String ipAddress = textIPaddress.getText();
        String portConection = textPortConnection.getText();
        String portData = textPortData.getText();

        if (ae.getSource().equals(buttonLogin)) {
            try {

                switch (buttonLogin.getStatus()) {
                    case JFlipButton.CONNECT:
                        clientSocketConnection = new Socket(ipAddress, Integer.valueOf(portConection));
                        clientSocketData = new Socket(ipAddress, Integer.valueOf(portData));
                        clientConnection = new FTPClientConnection(clientSocketConnection, clientSocketData, username, password);
                        if (clientConnection.isConnected()) {
                            buttonLogin.flipStatus();
                            listServer.setListData(clientConnection.commandLIST());
                        } else {
                            clientConnection = null;
                        }
                        break;
                    case JFlipButton.DISCONNECT:
                        listServer.setListData(serverFiles);
                        clientConnection.commandDISCONNECT();
                        clientConnection = null;
                        buttonLogin.flipStatus();
                        break;
                }
            } catch (IOException e) {
                logger.writeLog("Não foi possível se conectar ao servidor!", FTPLogger.ERR);
            }
        } else if (ae.getSource().equals(buttonDelete)) {
            List filesFromServerToBeDeleted = listServer.getSelectedValuesList();
            List filesFromClientToBeDeleted = listClient.getSelectedValuesList();
            if (filesFromClientToBeDeleted.isEmpty() && filesFromServerToBeDeleted.isEmpty()) {
                return;
            }
            String message = "Excluir os arquivos abaixo?\n";
            int option;

            if (!filesFromServerToBeDeleted.isEmpty()) {
                message += "Remoto\n";
                for (Object filename : filesFromServerToBeDeleted) {
                    message += "-" + (String) filename + "\n";
                }
            }
            if (!filesFromClientToBeDeleted.isEmpty()) {
                message += "\n-Local\n";
                for (Object filename : filesFromClientToBeDeleted) {
                    message += "-" + (String) filename + "\n";
                }
            }

            JTextArea area = new JTextArea(message);
            area.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
            area.setEditable(false);
            option = JOptionPane.showConfirmDialog(null, new JScrollPane(area), "Atenção!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                for (Object filename : filesFromServerToBeDeleted) {
                    clientConnection.commandDELETE((String) filename);
                }
                for (Object filename : filesFromClientToBeDeleted) {
                    File file = new File((String) filename);
                    file.delete();
                }
                listClient.setListData(this.getLocalFiles());
                if (!filesFromServerToBeDeleted.isEmpty()) {
                    listServer.setListData(clientConnection.commandLIST());
                }
            }
        } else if (ae.getSource().equals(buttonUpload)) {
            List filesToBeUploaded = listClient.getSelectedValuesList();
            if (!filesToBeUploaded.isEmpty()) {
                for (Object filename : filesToBeUploaded) {
                    clientConnection.commandSTOR((String)filename);
                }
                listServer.setListData(clientConnection.commandLIST());
            }
        }else if(ae.getSource().equals(buttonDownload)){
            List filesToBeDownloaded = listServer.getSelectedValuesList();
            if (!filesToBeDownloaded.isEmpty()) {
                for (Object filename : filesToBeDownloaded) {
                    clientConnection.commandRETR((String)filename);
                }
                listClient.setListData(this.getLocalFiles());
            }
        }

    }
    private void updateStatus(){
        
    }

}
