package com.github.chuckb.raspbootin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import com.fazecast.jSerialComm.*;

public class JRaspBootCom {
  private final int MAXSIZE = 0x200000;
  
  /**
   * Entry point for boot loader driver application
   * 
   * @param argv  Args from the command line
   */
  public static void main(String ... argv) throws Exception, FileNotFoundException {
    JRaspBootCom main = new JRaspBootCom();
    RuntimeSettings runtimeSettings = new RuntimeSettings(argv);
    if (runtimeSettings.parse()) {
      if (runtimeSettings.getHelp()) {
        // print out the usage to sysout
        runtimeSettings.printUsage();
      } else {
        // run the app
        main.run(runtimeSettings);
        System.exit(0);
      }
    } else {
      // print the parameter error, show the usage, and bail
      System.err.println(runtimeSettings.getParseErrorMessage());
      runtimeSettings.printUsage();
      System.exit(1);
    }
  }  

  /**
   * Run the boot loader driver application with processed command args.
   * 
   * @param runtimeSettings   Wired up settings from the command line.
   */
  public void run(RuntimeSettings runtimeSettings) throws Exception, FileNotFoundException {
    SerialPort comPort = null;
    InputStream imageInputStream = null;
    OutputStream serialOutputStream = null;
    InputStream serialInputStream = null;

    try {
      // Check that we get an image file of some size
      File file = new File(runtimeSettings.getBootImage());
      if (!file.exists() || !file.isFile() || file.length() == 0) {
        throw new Exception(String.format("File %s not found", runtimeSettings.getBootImage()));
      }

      // Check that the file size is within limits
      if (file.length() > MAXSIZE) {
        throw new Exception(
          String.format("kernel %s too big [%d bytes of %d allowable]", 
            runtimeSettings.getBootImage(), 
            file.length(), 
            MAXSIZE));
      }

      // Open the serial port
      comPort = SerialPort.getCommPort(runtimeSettings.getPort());
      comPort.setBaudRate(runtimeSettings.getBaud());
      while(!comPort.openPort()) {
        System.err.println(String.format("### Waiting for %s to become available", runtimeSettings.getPort()));
        Thread.sleep(1000);
      }
      System.err.println(String.format("### Listening on %s", runtimeSettings.getPort()));

      // Get streams to process data to/from target
      serialOutputStream = comPort.getOutputStream();
      serialInputStream = comPort.getInputStream();

      // Open the boot image file
      imageInputStream = new FileInputStream(runtimeSettings.getBootImage());

      // Instantiate and run the driver
      BootLoaderDriver driver = new BootLoaderDriver(
          (int) new File(runtimeSettings.getBootImage()).length(), 
          runtimeSettings.getBootImage(), 
          imageInputStream, 
          serialOutputStream, 
          serialInputStream, 
          System.out, 
          System.in, 
          System.err);
      driver.run();
    } finally {
      // Clean up stuff
      if (Objects.nonNull(serialInputStream)) {
        serialInputStream.close();
      }
      if (Objects.nonNull(serialOutputStream)) {
        serialOutputStream.close();
      }
      if (Objects.nonNull(comPort)) {
        comPort.closePort();
      }
      if (Objects.nonNull(imageInputStream)) {
        imageInputStream.close();
      }
    }
  }
}