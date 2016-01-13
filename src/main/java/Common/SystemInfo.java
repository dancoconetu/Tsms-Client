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

    public ArrayList<String> getPythonVersionWindows()
    {
        ArrayList<String> pythonVersions = new ArrayList<String>();
        File c = new File("C:\\");
        for ( File file: c.listFiles())
        {
            if (file.getName().toLowerCase().contains("python"))
            {
                pythonVersions.add(file.getName());
            }
        }

        return pythonVersions;
    }

    public String getPythonVersionsMac() {



            Runtime r = Runtime.getRuntime();
            Process p = null;

            try {


                p = r.exec("python -V ");

            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));



            String line = "";
            String mainPythonVersion="";
            try {
                mainPythonVersion = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {


                p = r.exec("python -V ");

            } catch (IOException e) {
                e.printStackTrace();
            }
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String secondaryPythonVersion="";
            try {
                secondaryPythonVersion = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mainPythonVersion.contains("Python 2"))
            {
                if (secondaryPythonVersion.contains("Python 3"))
                    return "python3";
                else
                    return "noPython3";
            }
            else
            {
                if (mainPythonVersion.contains("Python 3"))
                {
                    if (secondaryPythonVersion.contains("Python 2"))
                        return "python2";
                        else
                        return "noPython2";

                }
                else
                    return "noPythonAtAll";
            }

        }







}
