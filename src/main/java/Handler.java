import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by dic on 24-09-2015.
 */
public class Handler {
    public Handler()
    {

    }

    public String returnSystemInfo()
    {String hostname = "Unknown";
        try
        {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Hostname can not be resolved");
        }

        return hostname + ":" +  System.getProperty("os.name");
        //return System.getProperty("os.name");
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


    public void handleXml(String xmlString)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse( new InputSource( new StringReader( xmlString ) ) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("File");
        for (int temp = 0; temp < nodeList.getLength(); temp++) {

            Node nNode = nodeList.item(temp);

            System.out.println("\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                System.out.println("Image name: " + eElement.getAttribute("FileName"));
                System.out.println("Size:"  + eElement.getAttribute("FileSize"));
            }
        }

        //Node node = document.getElementById("TaskType");
        System.out.println("node name: " + document.getDocumentElement().getNodeName());
    }



}
