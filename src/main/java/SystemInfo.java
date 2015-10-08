/**
 * Created by dic on 08-10-2015.
 */
public class SystemInfo {
    private  String OS = System.getProperty("os.name").toLowerCase();
    private String pathForHome="";


    public SystemInfo() {

        System.out.println(OS);
        String homeDir= System.getProperty("user.home");
        System.out.println(homeDir);

        if (isWindows()) {
            System.out.println("This is Windows");
            pathForHome=homeDir + "\\TSMS\\";
        } else if (isMac()) {
            System.out.println("This is Mac");
            pathForHome= homeDir + "/TSMS/";
        } else if (isUnix()) {
            System.out.println("This is Unix or Linux");
            pathForHome= homeDir + "/TSMS/";
        } else {
            System.out.println("Your OS is not support!!");
        }
    }

    public String getPathForHome()
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



}
