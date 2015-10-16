import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

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
    private JLabel connectionStatus;
    private JTextField ipTextField;
    private JButton connectButton;
    private JLabel connectStatusLabel;
    private JButton hideAppButton;
    private JFileChooser chooser;
    private String choosertitle;
    private Client slave = null;
    private boolean isSlaveConnected = false;
    private JFrame ourJframe;
    public SystemInfo systemInfo;
    public MainClass()
{
    super("TSMS Slave");
    JMenuBar menuBar = new JMenuBar();
    JMenu file = new JMenu("File");
    JMenuItem item =new JMenuItem("woah");
    file.add(item);
    menuBar.add(file);
    setJMenuBar(menuBar);
    init();
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
    folderPathTextField.setText(chooser.getCurrentDirectory().toString());
    hideAppButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            hideButtonPressed();
        }
    });
}

    public void init()
    {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        systemInfo  = new SystemInfo(chooser.getCurrentDirectory());
        chooser.setDialogTitle(choosertitle);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        connectStatusLabel.setText("Disconnected");
        connectStatusLabel.setForeground(Color.RED);
        connectButton.setForeground(Color.GREEN);
    }

    private void browseButtonClicked() {

        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            folderPathTextField.setText(chooser.getSelectedFile().toString());
            System.out.println("getCurrentDirectory(): "
                    + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    + chooser.getSelectedFile());
            systemInfo.setPathForHome(chooser.getSelectedFile());
            System.out.println("new path for home: " + systemInfo.getPathForHome());
            disconnectSlave();

        }
        else {
            System.out.println("No Selection ");
        }
    }

    private void connectButtonClicked() {
        if (!isSlaveConnected)
        {
            connectSlave();

            System.out.println("Connected");


        }
        else
        { System.out.println("Disconnected");
            disconnectSlave();



        }

    }

    private void hideButtonPressed()
    {

        setVisible(false);
        System.out.println("hiding");
    }

    private void unhideButtonPressed()
    {

    }

    private void connectSlave()
    {
        try {

            slave = new Client(ipTextField.getText(), 7777, systemInfo);

            connectStatusLabel.setText("Connected");
            connectStatusLabel.setForeground(Color.GREEN);
            isSlaveConnected = true;
            connectButton.setText("Disconnect");
            connectButton.setForeground(Color.RED);
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
            System.out.println("wtf?");
            disconnectSlave();
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
            disconnectSlave();


        }
    }


    private void disconnectSlave()
    {
        if ( isSlaveConnected)
            slave.stop();
        connectStatusLabel.setText("Disconnected");
        connectStatusLabel.setForeground(Color.RED);
        isSlaveConnected = false;
        connectButton.setText("Connect");
        connectButton.setForeground(Color.GREEN);


    }

    public static void main(String[] args)
    {
        try {
            new ServerSocket(10000);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
//        JFrame jFrame = new JFrame("TSMS Slave");
//
//        jFrame.setContentPane(new MainClass().panel1);
//        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
//        jFrame.pack();
//        jFrame.setVisible(true);

        MainClass mainClass = new MainClass();
        mainClass.setContentPane(mainClass.panel1);
        mainClass.setDefaultCloseOperation(mainClass.EXIT_ON_CLOSE);
        mainClass.pack();
        mainClass.setVisible(true);


    }

}
