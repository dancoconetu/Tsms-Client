/**
 * Created by dic on 18-09-2015.
 */

import Common.FolderInfo;
import Common.SystemInfo;
import Common.XMLClasses.XMLCreator;
import Common.XMLClasses.XMLParser;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

import static java.lang.Thread.sleep;

public class Slave implements Runnable {
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private SlaveThread client = null;
    private byte[] mybytearray;
    public SystemInfo systemInfo;
    public FolderInfo folderInfo;
    private MainClass mainClass;
    private boolean inUse = false;
   // public Mutex mutexSend = new Mutex();
    public Mutex mutexReceive  = new Mutex();
    public Mutex mutexSend = new Mutex();
    public boolean STOP = false;
    public Socket socketFilesSend;
    public Socket socketFilesReceive;
    private int filesCount;


    public Slave(String serverName, int serverPort, SystemInfo systemInfo, MainClass mainClass) throws IOException
    {
            this.mainClass = mainClass;
            System.out.println("Establishing connection. Please wait ...");
            this.systemInfo = systemInfo;
            folderInfo = new FolderInfo(systemInfo);
            socket = new Socket(serverName, serverPort);

            System.out.println("Connected: " + socket);
            socketFilesSend = new Socket(serverName,serverPort);
        new DataOutputStream(socketFilesSend.getOutputStream()).writeUTF("FileSocketSend");

        socketFilesReceive = new Socket(serverName, serverPort);
        new DataOutputStream(socketFilesReceive.getOutputStream()).writeUTF("FileSocketReceive");

            start();
        sendMessage("StringSocket");

    }

    public boolean isAlive()
    {   PrintWriter out = new PrintWriter(streamOut, true);
        out.println("output");
        return !out.checkError();
    }
    public void run()
    {
        while (thread != null)
        {
            try
            {
                String s= console.readLine();
                if(s.equals("2"))
                {   System.out.println("wtf?");
                    filesCount =0;
                    sendMultipleFiles(folderInfo.folderPath);
                    System.out.println("Files sent: "  + filesCount);
                }

                streamOut.writeUTF(s);
                streamOut.flush();
            }
            catch (IOException ioe)
            {
                System.out.println("Sending error: " + ioe.getMessage());
                stop();
            }
        }
    }


    public void handle(String msg)
    {
        try {
            mutexReceive.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        XMLParser xmlParser = new XMLParser();
        Handler handler = new Handler(this);
        if (msg.equals(".bye"))
        {
            System.out.println("Good bye. Press RETURN to exit ...");
            stop();
        }
        else
        {
            System.out.println(msg);
           // sendMessage(msg.substring(7));
        }

        if (msg.equals("server:system"))
        {
            sendMessage(handler.returnSystemInfo());
            folderInfo.createFolder("Haleluia");
        }
        if (msg.equals("server:script"))
        {
            sendMessage(handler.runScript("dd"));
        }

        if(msg.equals("server:2"))
        {

            sendMultipleFiles(folderInfo.folderPath);
        }

        if(msg.equals("server:STOP"))
        {

           STOP=true;
        }



        if(msg.contains("<SendFile"))
        {

            Hashtable hashtable = xmlParser.parseSendFile(msg);
            receiveFile(hashtable.get("FileName").toString(), hashtable.get("FilePath").toString(), Long.parseLong(hashtable.get("FileSize").toString()) );
        }

        if(msg.contains("<SendMultipleFiles"))
        {
            String[][] filesInMaster = xmlParser.parseSendMultipleFiles(msg);
            ArrayList<File> availableFiles = new ArrayList<File>();
            folderInfo.listf(folderInfo.folderPath.toString(), availableFiles);
            ArrayList<File> missingFiles = handler.getMissingFiles(filesInMaster, availableFiles,folderInfo);
            System.out.println("Missings files nr: " + missingFiles.size());
            XMLCreator xmlCreator = new XMLCreator(folderInfo);
            String xmlFilesMissing = xmlCreator.createSendMultipleFilesXml(missingFiles);
            System.out.println(xmlFilesMissing);
            sendMessage(xmlFilesMissing);
        }


        if(msg.contains("SendOsInfo"))
        {   XMLCreator xmlCreator = new XMLCreator(folderInfo);
            String xmlToSend = xmlCreator.sendOsInfo(new ArrayList<String>(), systemInfo.getPcName(), systemInfo.getOs());

            sendMessage(xmlToSend);
        }


        mutexReceive.release();

    }

    public void start() throws IOException
    {
        for (File file: folderInfo.getAllFilesWithExtension("IIQ"))
        System.out.println(file.getName());
        console = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null)
        {
            client = new SlaveThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    public void sendMessage(String message) {
        try
        {
//            mutexSend.acquire();
            streamOut.writeUTF(message);
            streamOut.flush();
            System.out.println(" \n \n Message: \n" + message + "\n \n ");

              mutexSend.release();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        finally {

        }

    }

    public void sendMessageWithoutMutex(String message)
    {
        try {
            streamOut.writeUTF(message);
            streamOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }

    public void sendMultipleFiles(File folder)
    {
        for (File f: folderInfo.getOnlyFiles(folder))
        {

            sendFile(f);
            System.out.println(f.getName() + " path from TSMS: " + f.getAbsolutePath().substring( folderInfo.folderPath.getAbsolutePath().length()));

        }

        for (File f : folderInfo.getFolders(folder))
        {
            System.out.println(f.getName() + ": " + f.getAbsolutePath().substring( folderInfo.folderPath.getAbsolutePath().length()));
            sendMultipleFiles(f);
        }

    }

    public void sendFile(File myFile)
    {
        XMLCreator xmlCreator = new XMLCreator(folderInfo);
        String fileXml = xmlCreator.createSendFileXMLDoc(myFile);
        sendMessage(fileXml);
        System.out.println(fileXml);
        try {
            while(!new DataInputStream(socketFilesSend.getInputStream()).readUTF().equals(myFile.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mutexSend.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        DataOutputStream dos;
        //String imagePath = systemInfo.getPathForHomeAsString()  + file;
        //sendMessageWithoutMutex("Sending...");
        try
        {
            bos = new BufferedOutputStream(socketFilesSend.getOutputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        dos = new DataOutputStream(bos);
        sleepTime();

        try
        {   System.out.println("Sending " + myFile.getCanonicalPath() + "(" + myFile.length() + " bytes)");


            mybytearray = new byte[(int) myFile.length()];
            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);

            bis.read(mybytearray, 0, mybytearray.length);
            //sendMessageWithoutMutex("ImageFound");

            long fileLength = myFile.length();
            //dos.writeLong(fileLength);

            bos.write(mybytearray, 0, mybytearray.length);
            bos.flush();
            System.out.println("Done.");
            mutexSend.release();


        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("File not found!");
            //sendMessageWithoutMutex("ImageNotFound");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.out.println("sent");
//            sleepTime();
            filesCount++;


            //sendMessage("succesfully sent");
             try {
                 if (bis != null) bis.close();
                 if (fis != null) fis.close();



             } catch (IOException e) {
                e.printStackTrace();
            }

        }

        mutexSend.release();
    }

    public void stop()
    {
        System.out.println("step2");
        if (thread != null)
        {
            thread.stop();
            thread = null;
            System.out.println("step3");
            mainClass.showDisconnectedInformation();

        }
        try
        {
            //if (console != null) console.close();
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();


        }
        catch (IOException ioe)
        {
            System.out.println("Error closing ...");
        }

        mainClass.disconnectSlave();

        client.close();
        client.stop();

    }

    public void receiveFile(String imageName, String imagePath, long fileSize)
    {
//        try {
//            mutexReceive.acquire();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        try
//        {
//            sleep(500);
//        }
//        catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        String IMAGE_TO_BE_RECEIVED ="";
        long sizeReceived = 0;
        //long fileSize = 0;
        try
        {  // sendMessage("Go");
            long startTime = System.currentTimeMillis();
            BufferedInputStream bis = new BufferedInputStream(socketFilesReceive.getInputStream());
            DataInputStream dis = new DataInputStream(bis);
            //String imageName = dis.readUTF();
           // String imagePath = dis.readUTF();
            imagePath = imagePath.replace("\\", File.separator);
            File path2 =  new File(folderInfo.folderPath + imagePath);
            path2.mkdirs();
//            String imageFound = dis.readUTF();
//            System.out.println(imageFound);
//            if (!imageFound.equals("ImageFound") || imageFound.equals("ImageNotFound")  )
//            {
//                throw new Exception();
//            }
            sleepTime();
            IMAGE_TO_BE_RECEIVED = path2.getCanonicalPath() + File.separator + imageName ;
            fos = new FileOutputStream(IMAGE_TO_BE_RECEIVED);
            bos = new BufferedOutputStream(fos);
            //fileSize = dis.readLong();
            System.out.println("File size: " + fileSize);
            sizeReceived = 0;
            int bytesRead = 8192;
            byte[] buffer = new byte[bytesRead];
            while(sizeReceived<fileSize && (bytesRead = bis.read(buffer, 0, 8192))>0)
            {
                sizeReceived += bytesRead;
                //System.out.println(sizeReceived + " Available: " + bis.available() + "Count: " + bytesRead);
                bos.write(buffer, 0, bytesRead);
                bos.flush();
            }
            long estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println("File " + IMAGE_TO_BE_RECEIVED + " downloaded (" + sizeReceived + " bytes read)" +
                     " Time Elapsed: " + estimatedTime/1000.0 );
            if (fileSize != sizeReceived )
                System.out.println("malicious file sent");


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (bos != null) bos.close();
                if (fos != null) fos.close();
                if (fileSize != sizeReceived )
                {
                    System.out.println("\n\n\n\n-----------------------malicious file sent: + " +  IMAGE_TO_BE_RECEIVED + "-------------------------\n\n\n\n");
                    new File(IMAGE_TO_BE_RECEIVED).delete();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        inUse = false;

        //mutexReceive.release();
    }

    public void sleepTime()
    {
        try
        {
            sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[])
    {
//        Slave client = null;
//        try {
//            client = new Slave("172.16.4.6", 7777, new Common.SystemInfo());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
