/**
 * Created by dic on 18-09-2015.
 */

import Common.FolderInfo;
import Common.SystemInfo;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import java.io.*;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class Slave implements Runnable {
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private SlaveThread client = null;
    private String FILE_TO_SEND = "C:\\Users\\dic\\Heroeswithin_roshan.mp3";
    private String IMAGE_TO_SEND = "C:\\Users\\dic\\tsms-client-1.0-SNAPSHOT-jar-with-dependencies.jar";
    private String IMAGE_TO_SEND_WINDOWS = "C:\\Users\\dic\\CF000037.IIQ";
    private String IMAGE_TO_SEND_MAC = "/Users/testdepartment/Desktop/LEA-Credo40-L.IIQ";
    private byte[] mybytearray;
    public SystemInfo systemInfo;
    public FolderInfo folderInfo;
    private MainClass mainClass;
    private boolean inUse = false;
   // public Mutex mutexSend = new Mutex();
    public Mutex mutexReceive  = new Mutex();
    public Mutex mutexSend = new Mutex();
    public boolean STOP = false;


    public Slave(String serverName, int serverPort, SystemInfo systemInfo, MainClass mainClass) throws IOException
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

        if(msg.equals("server:sendToClient"))
        {   inUse = true;
            receiveFile();
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
            client = new SlaveThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    public void sendMessage(String message) {
        try
        {   mutexSend.acquire();
            streamOut.writeUTF(message);
            streamOut.flush();
            mutexSend.release();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public void sendFile(File myFile) {

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
        sendMessageWithoutMutex("Sending...");
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
        {   System.out.println("Sending " + myFile.getCanonicalPath() + "(" + myFile.length() + " bytes)");

            DataInputStream streamIn  = new DataInputStream(socket.getInputStream());
           // while (!streamIn.readUTF().equals("Go")){}
            if (myFile.length()> 150502457)
                throw new FileNotFoundException();
            sendMessageWithoutMutex(myFile.getName()); //sending file name
            sendMessageWithoutMutex(myFile.getParentFile().getAbsolutePath().substring(folderInfo.folderPath.getAbsolutePath().length()));

            mybytearray = new byte[(int) myFile.length()];
            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);

            bis.read(mybytearray, 0, mybytearray.length);
            sendMessageWithoutMutex("ImageFound");

            long fileLength = myFile.length();
            dos.writeLong(fileLength);

            bos.write(mybytearray, 0, mybytearray.length);
            bos.flush();
            System.out.println("Done.");
            mutexSend.release();


        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("File not found!");
            sendMessageWithoutMutex("ImageNotFound");

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

    public void receiveFile()
    {
        try {
            mutexReceive.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        String IMAGE_TO_BE_RECEIVED ="";
        long sizeReceived = 0;
        long fileSize = 0;
        try
        {  // sendMessage("Go");
            long startTime = System.currentTimeMillis();
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            DataInputStream dis = new DataInputStream(bis);
            String imageName = dis.readUTF();
            String imagePath = dis.readUTF();
            File path2 =  new File(folderInfo.folderPath + imagePath);
            path2.mkdirs();
            String imageFound = dis.readUTF();
            System.out.println(imageFound);
            if (!imageFound.equals("ImageFound") || imageFound.equals("ImageNotFound")  )
            {
                throw new Exception();
            }
            IMAGE_TO_BE_RECEIVED = path2.getCanonicalPath() + File.separator + imageName ;
            fos = new FileOutputStream(IMAGE_TO_BE_RECEIVED);
            bos = new BufferedOutputStream(fos);
            fileSize = dis.readLong();
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
                    System.out.println("malicious file sent");
                    new File(IMAGE_TO_BE_RECEIVED).delete();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        inUse = false;

        mutexReceive.release();
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
