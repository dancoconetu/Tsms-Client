/**
 * Created by dic on 18-09-2015.
 */

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class SlaveThread extends Thread
{  private Socket           socket   = null;
    private Slave slave = null;
    private DataInputStream  streamIn = null;

    public SlaveThread(Slave _slave, Socket _socket)
    {  slave = _slave;
        socket   = _socket;
        open();
        start();
    }
    public void open()
    {  try
    {  streamIn  = new DataInputStream(socket.getInputStream());
    }
    catch(IOException ioe)
    {  System.out.println("Error getting input stream: " + ioe);
        slave.stop();
    }
    }
    public void close()
    {  try
    {  if (streamIn != null) streamIn.close();
    }
    catch(IOException ioe)
    {  System.out.println("Error closing input stream: " + ioe);
    }
    }

    public boolean isRunning()
    {
        return !socket.isClosed();
    }


    public void run()
    {  while (true)
    {  try
    {  slave.handle(streamIn.readUTF());

    }
    catch(IOException ioe)
    {
        System.out.println("Listening error: " + ioe.getMessage());
        System.out.println("step1");
        slave.stop();




    }
    }
    }
}
