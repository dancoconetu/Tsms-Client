import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by dic on 24-09-2015.
 */
public class Handler {
    private int counter = 0;
    private Slave slave;
    public Handler(Slave slave)
    {
        this.slave = slave;
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

    }

    public String runScript(String scriptName)
    {
        Runtime r = Runtime.getRuntime();
        Process p = null;
        try {
            if(slave.systemInfo.isWindows())
            p = r.exec("cmd.exe /c C:\\Python34\\python.exe hello.py ");
            else
                p = r.exec("ls /Users/testdepartment/Desktop");

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


    public String[][] handleXml(String xmlString)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("File");
        String[][] list = new String[nodeList.getLength()][2];
        for (int temp = 0; temp < nodeList.getLength(); temp++) {

            Node nNode = nodeList.item(temp);

            System.out.println("\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE ) {

                Element eElement = (Element) nNode;
                String fileName= eElement.getAttribute("FileName") ;
                String fileSize = eElement.getAttribute("FileSize");
                System.out.println("Image name: " +  fileName);
                System.out.println("Size:"  +  fileSize);
                list[temp][0] = fileName;
                list[temp][1] = fileSize;

                //list[]
            }
        }

        //Node node = document.getElementById("TaskType");
        System.out.println("node name: " + document.getDocumentElement().getNodeName());

        return list;
    }

    public String[][] getMissingFiles(String[][] stringFileList, ArrayList<File> fileList)
    {
        String[][] stringFilesMissing= new String[stringFileList.length][2];
        int j =0;
        for (int i=0; i<stringFileList.length; i++)
        {
            if (!isFilePresent(stringFileList[i][0], stringFileList[i][1], fileList))
            {
                stringFilesMissing[j][0] = stringFileList[i][0];
                stringFilesMissing[j][1] = stringFileList[i][1];
                j++;
            }
        }

        return stringFilesMissing;
    }


    private boolean isFilePresent(String fileName, String fileSize, ArrayList<File> fileList )
    {

        for (File f: fileList)
        {
            if (f.getName().equals(fileName))
            {
                if ((f.length()+"").equals(fileSize))
                {   System.out.println("Counter: " + ++counter);
                    return true;
                }
            }
        }
        return false;
    }




}
