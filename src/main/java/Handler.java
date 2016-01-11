import Common.FolderInfo;
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
            p = r.exec("cmd.exe /c C:\\Python\\python.exe C:\\TEST_EXAMPLE\\testsuite_execute.py  ");
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

    public ArrayList<File> getMissingFiles(String[][] stringFileList, ArrayList<File> fileList, FolderInfo folderInfo)
    {
        ArrayList<File> stringFilesMissing= new ArrayList<File>();
        int j =0;
        for (int i=0; i<stringFileList.length; i++)
        {   System.out.println("Path from PC"  + stringFileList[i][2]);
            stringFileList[i][2] = stringFileList[i][2].replace("\\", File.separator);
            System.out.println("Path from PC altered" + stringFileList[i][2]);
            File path2 =  new File(folderInfo.folderPath + stringFileList[i][2]);
            System.out.println("Path after: " + path2.getAbsolutePath());
            File file = new File(path2.getAbsolutePath() + File.separator + stringFileList[i][0]);
                if (!file.exists())
                {
                    stringFilesMissing.add(file);
                    System.out.println(j++);
                }
            else
                {
                    if (file.length()!= Long.parseLong(stringFileList[i][1]))
                    {
                        stringFilesMissing.add(file);
                        System.out.println(j++);
                    }
                }
           // if (!isFilePresent(stringFileList[i][0], stringFileList[i][1], fileList))

        }

        return stringFilesMissing;
    }


    private boolean isFilePresent(String fileName, String fileSize, String filePath , ArrayList<File> fileList )
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
