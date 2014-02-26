package dniklaus.proxy;

import java.util.HashMap;

public class CommandLineParser
{

  private String[] _cmdLineArgs;
  private HashMap<String, String> _cmdList;

  public CommandLineParser(String[] args)
  {
    _cmdLineArgs = args;
    _cmdList = new HashMap<String, String>();
    boolean commandAvailable = false;
    String command = "";
    String argument = "";
    for (String arg : args)
    {
      if (arg.contains("-"))
      {
        command = arg;
        commandAvailable = true;
        _cmdList.put(arg, "");
      }
      else if (commandAvailable && _cmdList.containsKey(command))
      {
        _cmdList.put(command, arg);
      }
    }

  }

  public boolean IsUsage()
  {
    if (_cmdList.containsKey("-h"))
      return true;
    return false;
  }

  public boolean IsEmulated()
  {
    if (_cmdList.containsKey("-e"))
      return true;
    return false;
  }

  public int GetTcpPort() throws Exception
  {
    if (_cmdList.containsKey("-tcpport"))
    {
      String tcpPort = _cmdList.get("-tcpport");
      return (int) Integer.parseInt(tcpPort);
    }
    throw new Exception("No tcp port provided");
  }

  public String GetComPort() throws Exception
  {
    if (_cmdList.containsKey("-comport"))
    {
      String comPort = _cmdList.get("-comport");
      return comPort;
    }
    throw new Exception("No tcp port provided");
  }

}
