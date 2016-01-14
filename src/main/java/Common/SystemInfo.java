package Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by dic on 08-10-2015.
 */
public class SystemInfo {
    private  String OS = System.getProperty("os.name").toLowerCase();
    private File pathForHome;


    public SystemInfo(File pathForHome)
    {
        this.pathForHome = pathForHome;
    }

    public SystemInfo() {

        System.out.println(OS);
        String homeDir= System.getProperty("user.home");
        System.out.println(homeDir);

        if (isWindows()) {
            System.out.println("This is Windows");
            //pathForHome=homeDir + "\\TSMS\\";
        } else if (isMac()) {
            System.out.println("This is Mac");
            //pathForHome= homeDir + "/TSMS/";
        } else if (isUnix()) {
            System.out.println("This is Unix or Linux");
           // pathForHome= homeDir + "/TSMS/";
        } else {
            System.out.println("Your OS is not support!!");
        }
    }

    public void setPathForHome(File path)
    {
        pathForHome = path;
    }
    public String getPathForHomeAsString()
    {
        if (isWindows())
        return getPathForHome() + "\\";
        else
            return getPathForHome() +  "/";
    }

    public File getPathForHome()
    {
        return pathForHome;
    }

    public boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }

    public boolean isMac() {

        return (OS.indexOf("mac") >= 0);

    }

    public boolean isUnix() {

        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );

    }

    public String getOs()
    {
        return OS;
    }


    public String getPcName()
    {

        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (addr!=null)
        return  addr.getHostName();
        else
            return "Unknown";
    }

    public String getPythonVersionWindows()
    {
        String list="";
        File c = new File("C:\\");
        for ( File file: c.listFiles())
        {
            if (file.getName().toLowerCase().contains("python"))
            {
                list+=file.getName() +";";
            }
        }

        return list;
    }

    public String getPythonVersionsMac() {

        String v = "";


           File versions = new File("/Library/Frameworks/Python.framework/Versions");

        for (File file: versions.listFiles())
        {
            if (!file.getName().contains("Current"))
            {
                v+= file.getName()+";";
            }
        }

        return v;



        }







}
