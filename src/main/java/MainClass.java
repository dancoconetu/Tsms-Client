import Common.SystemInfo;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;
import static java.lang.Thread.sleep;

/**
 * Created by dic on 15-10-2015.
 */
public class MainClass extends JFrame {
    private JPanel panel1;
    private JButton browseButton;
    private JTextField folderPathTextField;
    private JPanel westPanel;
    private JPanel eastPanel;
    private JPanel southPanel;
    private JPanel middlePanel;
    private JLabel masterIP;
    private JTextField ipTextField;
    private JButton connectButton;
    private JLabel connectStatusLabel;
    private JButton hideAppButton;
    private JTextField scriptsResults;
    private JFileChooser chooser;
    private String choosertitle;
    private Slave slave = null;
    private boolean isSlaveConnected = false;
    private JFrame ourJframe;
    private boolean isHidden = false;
    public SystemInfo systemInfo;
    private Preferences prefs;
    private String ipPreferences = "ipPreferences";
    private String scriptPreferences = "scriptPreferences";

    public MainClass()
    {
        super("TSMS Slave");
        init();
        connectSlave(false);
    }

    public void init()

    {
        prefs = Preferences.userRoot().node(this.getClass().getName());
        SystemTrayIcon systemTrayIcon = new SystemTrayIcon(this);
        setIconImage(systemTrayIcon.CreateIcon("icon.png","app icon"));

        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(prefs.get(scriptPreferences, chooser.getCurrentDirectory().toString())));
        systemInfo  = new SystemInfo(chooser.getCurrentDirectory());
        chooser.setDialogTitle(choosertitle);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        connectStatusLabel.setText("Disconnected");
        connectStatusLabel.setForeground(Color.RED);
        connectButton.setForeground(Color.GREEN);
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseButtonClicked();
            }
        });
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectButtonClicked();
            }
        });
        //folderPathTextField.setText(chooser.getCurrentDirectory().toString());
        folderPathTextField.setText(prefs.get(scriptPreferences, chooser.getCurrentDirectory().toString()));
        systemInfo.setPathForHome(new File(prefs.get(scriptPreferences, chooser.getCurrentDirectory().toString())));
        ipTextField.setText(prefs.get(ipPreferences, "172.16.4.6"));
        ipTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                prefs.put(ipPreferences, ipTextField.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                prefs.put(ipPreferences, ipTextField.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                prefs.put(ipPreferences, ipTextField.getText());
            }
        });
        hideAppButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hideButtonPressed();
            }
        });



    }

    private void browseButtonClicked() {

        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            prefs.put(scriptPreferences, chooser.getSelectedFile().toString());
            folderPathTextField.setText(chooser.getSelectedFile().toString());
            System.out.println("getCurrentDirectory(): "
                    + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    + chooser.getSelectedFile());
            systemInfo.setPathForHome(chooser.getSelectedFile());
            System.out.println("new path for home: " + systemInfo.getPathForHome());
            disconnectSlave();
            connectSlave(true);

        }
        else {
            System.out.println("No Selection ");
        }
    }

    private void connectButtonClicked() {
        if (!isSlaveConnected)
        {
            connectSlave(false);
            System.out.println("Connected");
        }
        else
        {
            System.out.println("Disconnected");
            disconnectSlave();
        }

    }

    public void hideButtonPressed()
    {

        setVisible(false);
        System.out.println("hiding");
    }

    public  void unhideButtonPressed()
    {
        setVisible(true);
        System.out.println("unhiding");
    }

    private void connectSlave(boolean t)
    {
        try {
            slave = new Slave(ipTextField.getText(), 7777, systemInfo, this);
            connectStatusLabel.setText("Connected");
            connectStatusLabel.setForeground(Color.GREEN);
            isSlaveConnected = true;
            connectButton.setText("Disconnect");
            connectButton.setForeground(Color.RED);
            Thread t1 = new Thread(new Runnable() {
                public void run() {



                    while(true)
                    {
                        try {
                            sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        slave.sendMessage("Rain Check");
                        //System.out.println("rain check sent");
                       
                        //System.out.println(slave.isAlive());

                        //if (!slave.isAlive())

                        //  disconnectSlave();
                    }

                }
            });
            t1.start();

        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
            System.out.println("wtf?");
            disconnectSlave();
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
            if(t)
            JOptionPane.showMessageDialog(this, "Connection was refused.", "Connection error",
                    JOptionPane.ERROR_MESSAGE);
            disconnectSlave();
        }
    }


    protected void disconnectSlave()
    {   boolean previousStatusForSlave = isSlaveConnected;
        isSlaveConnected = false;
        if (previousStatusForSlave)
        {
            slave.stop();


        }
        connectStatusLabel.setText("Disconnected");
        connectStatusLabel.setForeground(Color.RED);

        connectButton.setText("Connect");
        connectButton.setForeground(Color.GREEN);
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("Sleeping ?");
                    sleep(60000);
                    System.out.println("waking");
                    if (!isSlaveConnected)
                    connectSlave(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
        });
        t1.start();



    }

    protected void showDisconnectedInformation()
    { Thread t1 = new Thread(new Runnable() {
        public void run() {
            JOptionPane.showMessageDialog(getContentPane(),"The connection was terminated. Try again to connect","Connection error " + slave.isAlive(),
                    JOptionPane.ERROR_MESSAGE);
        }
    });
        t1.start();

    }

    public boolean IsSlaveConnected()
    {
        return isSlaveConnected;
    }

    public boolean IsHidden()
    {
        return !this.isShowing();
    }

    public static void main(String[] args)
    {
//        try {
//            new ServerSocket(11000);
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.exit(0);
//        }
//        JFrame jFrame = new JFrame("TSMS Slave");
//
//        jFrame.setContentPane(new MainClass().panel1);
//        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
//        jFrame.pack();
//        jFrame.setVisible(true);

        MainClass mainClass = new MainClass();
        mainClass.setContentPane(mainClass.panel1);
        mainClass.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        mainClass.pack();
        mainClass.setVisible(true);



    }

}
