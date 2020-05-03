package com.github.chuckb.raspbootin;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

//import static org.mockito.Mockito.*;

public class RuntimeSettingsUnitTest {
  @Test
  public void itRequiresAPort() {
    // Assemble
    RuntimeSettings dut = new RuntimeSettings();
    // Act
    dut.parse();
    // Assert
    String message = dut.getParseErrorMessage();
    assertTrue(message.contains("required"));
    assertTrue(message.contains("--port"));
  }

  @Test
  public void itRequiresAnImageFile() {
    // Assemble
    RuntimeSettings dut = new RuntimeSettings("--port", "/dev/ttyUSB0");
    // Act
    dut.parse();
    // Assert
    String message = dut.getParseErrorMessage();
    assertTrue(message.contains("required"));
    assertTrue(message.contains("image"));
  }

  @Test
  public void itParsesAPort() {
    // Assemble
    RuntimeSettings dut = new RuntimeSettings("--port", "/dev/ttyUSB0", "kernel.img");
    // Act
    dut.parse();
    // Assert
    assertTrue(dut.getPort().contains("/dev/ttyUSB0"));
  }

  @Test
  public void itParsesAnImageFile() {
    // Assemble
    RuntimeSettings dut = new RuntimeSettings("--port", "/dev/ttyUSB0", "kernel.img");
    // Act
    dut.parse();
    // Assert
    assertTrue(dut.getBootImage().contains("kernel.img"));
  }

  @Test
  public void itParsesABaudRate() {
    // Assemble
    RuntimeSettings dut = new RuntimeSettings("--port", "/dev/ttyUSB0", "-b", "9600", "kernel.img");
    // Act
    dut.parse();
    // Assert
    assertEquals(9600, dut.getBaud());
  }
}