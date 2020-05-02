package com.github.chuckb.raspbootin;

import com.beust.jcommander.*;

public class RuntimeSettings {
  // Command line args
  private String[] argv;
  // Program name that you want to pass to command line parsing...which will be put into usage info
  private static final String programName = "JRaspBootCom";

  // command line args
  @Parameter(description = "<Boot image file name>", required = true)
  private String bootImage = "kernel.img";
  @Parameter(names={"--baud", "-b"},
      description = "Baud rate for serial port communication to target (default 115200)")
  private int baud = 115200;
  @Parameter(names={"--port", "-p"},  
      description="Boot loader serial port for communication (default /dev/ttyUSB0)", required = true)
  private String port = "/dev/ttyUSB0";
  @Parameter(names = "--help", help = true)
  private boolean help = false;

  // Internal jCommander variable
  private JCommander jc;

  // Parse error message
  private String parseErrorMessage;

	public RuntimeSettings(String ... argv) {
		if (argv == null)
		{
			throw new IllegalArgumentException("Arguments cannot be null.");
		}
    // Init the jcommander parser
    jc = JCommander.newBuilder()
        .programName(programName)
        .addObject(this)
        .build();
    this.argv = argv;
  }
    
  public boolean parse() {
    // parse command line args
    try {
      jc.parse(argv);
      return true;
    } catch (ParameterException pe) {
      // print the parameter error, show the usage, and bail
      parseErrorMessage = pe.getMessage();
      return false;
    }
  }

  public void printUsage() {
    jc.usage();
  }

  // Define getters
  public String getParseErrorMessage() {
    return parseErrorMessage;
  }

  public String getPort() {
    return port;
  }

  public boolean getHelp() {
    return help;
  }

  public int getBaud() {
    return baud;
  }

  public String getBootImage() {
    return bootImage;
  }
}