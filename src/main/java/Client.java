/**
 * Created by dic on 18-09-2015.
 */

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            sendMessage(msg.substring(7));
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
//        Client client = null;
//        try {
//            client = new Client("172.16.4.6", 7777, new SystemInfo());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
