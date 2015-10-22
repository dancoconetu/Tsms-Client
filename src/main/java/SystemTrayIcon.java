import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Created by dic on 20-10-2015.
 */



public class SystemTrayIcon {

    private static TrayIcon trayIcon;
    private static  MainClass mainClass;

    public SystemTrayIcon(MainClass mainClass)
    {   this.mainClass = mainClass;
        ShowTrayIcon();

    }

    private static void ShowTrayIcon()
    {
        trayIcon = new TrayIcon(CreateIcon("icon2.png", "Tray Icon"));
        final SystemTray tray = SystemTray.getSystemTray();
        System.out.println("Image size: " + trayIcon.getSize());
        final PopupMenu popupMenu = new PopupMenu();
        MenuItem hideItem = new MenuItem("Hide/Unhide");
        hideItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeHiddenStatus();
                //mainClass.unhideButtonPressed();
            }
        });
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        popupMenu.add(hideItem);

        popupMenu.addSeparator();
        popupMenu.add(exitItem);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2)
                {
                    changeHiddenStatus();
                    //mainClass.unhideButtonPressed();
                }
            }
        });

        trayIcon.setToolTip("TSMS Slave");

        trayIcon.setPopupMenu(popupMenu);

        try
        {
            tray.add(trayIcon);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public  static void changeHiddenStatus()
    {
        if (mainClass.IsHidden())
            mainClass.unhideButtonPressed();
        else
            mainClass.hideButtonPressed();
    }

    public static Image CreateIcon(String path, String desc)
    {
        URL imageURL = SystemTrayIcon.class.getResource(path);
        return (new ImageIcon(imageURL, desc).getImage());
    }
}
