/**
 * Created by dic on 18-09-2015.
 */

import java.io.*;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class Client implements Runnable {
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ClientThread client = null;
    private String FILE_TO_SEND = "C:\\Users\\dic\\Heroeswithin_roshan.mp3";
    private String IMAGE_TO_SEND = "C:\\Users\\dic\\CF000037.IIQ";
    private String IMAGE_TO_SEND_WINDOWS = "C:\\Users\\dic\\CF000037.IIQ";
    private String IMAGE_TO_SEND_MAC = "/Users/testdepartment/Desktop/LEA-Credo40-L.IIQ";
    private byte[] mybytearray;
    private SystemInfo systemInfo;
    private FolderInfo folderInfo;
    private MainClass mainClass;
    private boolean inUse = false;


    public Client(String serverName, int serverPort, SystemInfo systemInfo, MainClass mainClass) throws IOException
    {
            this.mainClass = mainClass;
            System.out.println("Establishing connection. Please wait ...");
            this.systemInfo = systemInfo;
            folderInfo = new FolderInfo(systemInfo);
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();

    }

    public boolean isAlive()
    {   PrintWriter out = new PrintWriter(streamOut, true);
        out.println("output");
        return !out.checkError();
    }
    public void run() {
        while (thread != null)
        {
            try
            {
                streamOut.writeUTF(console.readLine());
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
        Handler handler = new Handler();
        if (msg.equals(".bye"))
        {
            System.out.println("Good bye. Press RETURN to exit ...");
            stop();
        }
        else
        {
            System.out.println(msg.substring(7));
           // sendMessage(msg.substring(7));
        }

        if (msg.equals("server:system"))
        {
            sendMessage(handler.returnSystemInfo());
        }
        if (msg.equals("server:script"))
        {
            sendMessage(handler.runScript("dd"));
        }
        if (msg.equals("server:send")) {
            sendFile("CF000237.IIQ");
        }
        if(msg.equals("server:sendAll"))
        {

            sendMultipleFiles();
        }

        if(msg.equals("server:sendToClient"))
        {   inUse = true;
            receiveFile();
        }
        if(msg.equals("server:xml"))
        {
            XMLCreator xmlCreator = new XMLCreator();
            System.out.println("Xml: " + xmlCreator.createScriptRunningXML( "hello", "2.7.0", folderInfo.getAllFilesWithExtension("IIQ"), "py"));
            System.out.println("the Second xml: " + xmlCreator.createSendFilesXml(folderInfo.getAllFilesWithExtension("IIQ")));
            handler.handleXml(xmlCreator.createScriptRunningXML("hello", "2.7.0", folderInfo.getAllFilesWithExtension("IIQ"), "py"));
            handler.handleXml(xmlCreator.createSendFilesXml(folderInfo.getAllFilesWithExtension("IIQ")));
        }
    }

    public void start() throws IOException
    {
        for (File file: folderInfo.getAllFilesWithExtension("IIQ"))
        System.out.println(file.getName());
        console = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null)
        {
            client = new ClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    public void sendMessage(String message) {
        try
        {
            streamOut.writeUTF(message);
            streamOut.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void sendMultipleFiles()
    {
        for (File file: folderInfo.getAllFilesWithExtension("IIQ"))
        {
            sendFile( file.getName());
        }
    }

    public void sendFile(String image) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        //OutputStream os = null;
        BufferedOutputStream bos = null;
        DataOutputStream dos;
        String imagePath = systemInfo.getPathForHomeAsString()  + image;
        sendMessage("Sending...");
        try
        {
            bos = new BufferedOutputStream(socket.getOutputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        dos = new DataOutputStream(bos);
        sleepTime();

        try
        {
            DataInputStream streamIn  = new DataInputStream(socket.getInputStream());
            while (!streamIn.readUTF().equals("Go")){}
            sendMessage(image);
            File myFile = new File(imagePath);

            mybytearray = new byte[(int) myFile.length()];

                fis = new FileInputStream(myFile);


            bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);
            sendMessage("ImageFound");
            long fileLength = myFile.length();
            dos.writeLong(fileLength);

            System.out.println("Sending " + imagePath + "(" + mybytearray.length + " bytes)");
            bos.write(mybytearray, 0, mybytearray.length);
            bos.flush();
            System.out.println("Done.");


        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("File not found!");
            sendMessage("ImageNotFound");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.out.println("sent");
            sleepTime();

            //sendMessage("succesfully sent");
//             try {
//                 if (bis != null) bis.close();
//
//
//             } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
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




    public void receiveFile()
    {
        try
        {
            sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try
        {   sendMessage("Go");
            System.out.println("Goooooooooooooooooooooooooooo");
            long startTime = System.currentTimeMillis();
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            DataInputStream dis = new DataInputStream(bis);
            String imageName = dis.readUTF();
            String imageFound = dis.readUTF();
            System.out.println(imageFound);
            if (!imageFound.equals("ImageFound") || imageFound.equals("ImageNotFound")  )
            {
                throw new Exception();
            }
            String IMAGE_TO_BE_RECEIVED;
            if (systemInfo.isWindows())
            IMAGE_TO_BE_RECEIVED = folderInfo.folderPath + "\\"  + imageName ;
            else
                IMAGE_TO_BE_RECEIVED = folderInfo.folderPath + "/" +  imageName;
            fos = new FileOutputStream(IMAGE_TO_BE_RECEIVED);
            bos = new BufferedOutputStream(fos);
            long fileSize = dis.readLong();
            System.out.println("File size: " + fileSize);
            int sizeReceived = 0;
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
            System.out.println("File " + IMAGE_TO_BE_RECEIVED + " downloaded (" + sizeReceived + " bytes read)"
                    + " Time Elapsed: " + estimatedTime/1000.0 );
            if (fileSize != sizeReceived )
                System.out.println("malicious file sent");
            /*if (imageCounter==99)
            {
                imageCounter = 0;
                repeted++;
            }
            if (imageCounter<100)
            {
                send("server:" + "send");
            }*/
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

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        inUse = false;
    }

    public void sleepTime()
    {
        try
        {
            sleep(100);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[])
    {
//        Client client = null;
//        try {
//            client = new Client("172.16.4.6", 7777, new SystemInfo());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
