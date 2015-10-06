/**
 * Created by dic on 18-09-2015.
 */

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

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

    public Client(String serverName, int serverPort) {
        System.out.println("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
        }
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
        if (msg.equals("server:send"))
        {
            sendFile();
        }
    }

    public void start() throws IOException
    {
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

    public void sendFile() {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        //OutputStream os = null;
        BufferedOutputStream bos = null;
        DataOutputStream dos;
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
        try
        {
            sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        IMAGE_TO_SEND= IMAGE_TO_SEND_WINDOWS;
        try
        {
            DataInputStream streamIn  = new DataInputStream(socket.getInputStream());
            while (!streamIn.readUTF().equals("Go")){}
            File myFile = new File(IMAGE_TO_SEND);
            mybytearray = new byte[(int) myFile.length()];
            long fileLength = myFile.length();
            dos.writeLong(fileLength);
            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);
            System.out.println("Sending " + IMAGE_TO_SEND + "(" + mybytearray.length + " bytes)");
            bos.write(mybytearray, 0, mybytearray.length);
            bos.flush();
            System.out.println("Done.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.out.println("sent");
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
        if (thread != null)
        {
            thread.stop();
            thread = null;
        }
        try
        {
            if (console != null) console.close();
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        }
        catch (IOException ioe)
        {
            System.out.println("Error closing ...");
        }
        client.close();
        client.stop();
    }

    public static void main(String args[])
    {
        Client client = null;
        client = new Client("172.16.4.6", 7777);
    }
}
