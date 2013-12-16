package dniklaus.proxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class SocketToSerialProxy
{

  private static ServerSocket serverSocket;

  private final static int    MILLIS_TO_WAIT_FOR_PORT_OPEN = 4000;
  private final static String COM_PORT_OWNER     = "SocketToSerialProxy";
  private static final String defaultTcpPortName = "8225";
  private static final String defaultComPortName = "COM1";

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception
  {
    String paramTcpPortName = defaultTcpPortName;
    String paramComPortName = defaultComPortName;

    if (args.length == 2)
    {
      paramTcpPortName = args[0];
      paramComPortName = args[1];
    }
          
    String tcpPortName = defaultTcpPortName;
    if (!paramTcpPortName.equals(""))
    {
      tcpPortName = paramTcpPortName;
    }
    int pport = Integer.parseInt(tcpPortName);
    
    String comPortName = defaultComPortName;
    if (!paramComPortName.equals(""))
    {
      comPortName = paramComPortName;
    }
    CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(comPortName);

    System.out.println("Serial Proxy Starting");
    System.out.println("Serial port: " + portId.getName() + " TCP Port: " + tcpPortName);

    SerialPort port = (SerialPort) portId.open(COM_PORT_OWNER, MILLIS_TO_WAIT_FOR_PORT_OPEN);
    InputStream input = port.getInputStream();
    OutputStream output = port.getOutputStream();
    port.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

    serverSocket = new ServerSocket(pport);

    try
    {
      if (Thread.interrupted())
      {
        throw new InterruptedException();
      }
      while (true)
      {
        try
        {
          Socket s = serverSocket.accept();
          while (s.isConnected())
          {
            while (input.available() > 0)
            {
              s.getOutputStream().write(input.read());
            }
            while (s.getInputStream().available() > 0)
            {
              output.write(s.getInputStream().read());
            }
            try
            {
              Thread.sleep(100);
            }
            catch (Exception e)
            {
            }
          }
        }
        catch (Throwable t)
        {
        }// just keep running
      }
    }
    catch (InterruptedException e)
    {
      port.close();
    }
  }
}
