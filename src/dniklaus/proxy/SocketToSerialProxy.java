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

  private final static int MILLIS_TO_WAIT_FOR_PORT_OPEN = 4000;
  private final static String COM_PORT_OWNER = "SocketToSerialProxy";

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception
  {
    CommandLineParser commandLineParser = new CommandLineParser(args);
    if (commandLineParser.IsUsage())
    {
      System.out.println("Usage: java -jar socket-serial-proxy.jar [-options]");
      System.out.println("available options:");
      System.out.println("  -h			shows help ");
      System.out
          .println("  -tcpport		port where the client can connect to, e.g. \"7777\"");
      System.out
          .println("  -comport		COM port for serial communication to lintella, e.g. \"COM30\" ");
      System.out
          .println("  -e			emulation flag, no hardware necessary, commands are emulated  ");
      return;
    }

    Integer tcpPort = commandLineParser.GetTcpPort();
    String comPort = commandLineParser.GetComPort();

    System.out.println("Serial Proxy Starting");
    System.out.println("Serial port: " + comPort + " TCP Port: "
        + tcpPort.toString());

    serverSocket = new ServerSocket(tcpPort);

    if (commandLineParser.IsEmulated())
    {
      try
      {
        do
        {
          try
          {
            Socket s = serverSocket.accept();
            byte value = 0x00;
            while (s.isConnected())
            {
              s.getOutputStream().write(value);
              System.out.println("Data sent to TcpClient: " + value);
              value++;
              Thread.sleep(500);
              while (s.getInputStream().available() > 0)
              {
                s.getInputStream().read();
              }
            }
          }
          catch (Throwable t)
          {
            System.out.println(t);
          }

          Thread.sleep(1000);
        }
        while (true);
      }
      catch (Exception e)
      {
        System.out.println(e);
      }
    }
    else
    {
      CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(comPort);

      SerialPort port = (SerialPort) portId.open(COM_PORT_OWNER,
          MILLIS_TO_WAIT_FOR_PORT_OPEN);
      InputStream input = port.getInputStream();
      OutputStream output = port.getOutputStream();
      port.setSerialPortParams(115200, SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

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
}
