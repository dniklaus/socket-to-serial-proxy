package ch.erni.proxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;

public class SocketToSerialProxy
{

  private static ServerSocket ss;

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception
  {
    String pp = null; 
    if (pp == null)
    {
      pp = "8225";
    }

    int pport = Integer.parseInt(pp);

    CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier("COM1");

    System.out.println("Serial Proxy Starting");
    System.out.println("Serial port: " + portId.getName() + " TCP Port: " + pp);

    SerialPort port = (SerialPort) portId.open("serial madness", 4000);
    InputStream input = port.getInputStream();
    OutputStream output = port.getOutputStream();
    port.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

    ss = new ServerSocket(pport);

    while (true)
    {
      try
      {
        Socket s = ss.accept();
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
          { }
        }
      }
      catch (Throwable t)
      {
      }// just keep running
    }
  }
}
