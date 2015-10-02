import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by dic on 24-09-2015.
 */
public class Handler {
    public Handler()
    {

    }

    public String returnSystemInfo()
    {
        return System.getProperty("os.name");
    }

    public String runScript(String scriptName)
    {
        Runtime r = Runtime.getRuntime();
        Process p = null;
        try {
            p = r.exec("cmd.exe /c python hello.py ");

        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            System.out.println("Waiting for batch file ...");
            p.waitFor();
            System.out.println("Batch file done.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String line = "";

        try {
            while (br.ready())
            {
                    String s = br.readLine();
                    System.out.println(s);
                    line+=s + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return line;
    }


    public String runCommand(String commandName)
    {
        Runtime r = Runtime.getRuntime();
        Process p = null;
        String line = "";
        try {
            p = r.exec(commandName);

        } catch (IOException e) {
            line+= e.toString();
            e.printStackTrace();
        }

        try {
            System.out.println("Waiting for batch file ...");
            p.waitFor();
            System.out.println("Batch file done.");
        } catch (Exception e) {
            e.printStackTrace();
            line+= e.toString();
        }


        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (br.ready())
            {
                String s = br.readLine();
                System.out.println(s);
                line+=s + "\n";

            }

        } catch (Exception e) {
            e.printStackTrace();
            line+= e.toString();
        }

        return line;
    }




}
